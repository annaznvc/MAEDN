package de.htwg.se.MAEDN.controller

import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util._

class Game(initialPlayers: List[Player]):
  var players: List[Player] = initialPlayers
  var currentPlayerIndex: Int = 0
  val board = new Board()
  var placementsGiven: Int = 0

  def currentPlayer: Player = players(currentPlayerIndex)

  def nextPlayer(): Unit = //erhöht aktuellen Spielerindex um 1 und modulo damit man wieder bei 0 anfängt wenn man am ende der spielerliste angkelkommen ist
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size

  def isGameOver: Boolean =
    players.count(p => p.status.isInstanceOf[Out]) == players.size - 1 //wenn alle out sind bis auf einen spieler, ist das spiel verloren

  def rollDice(): Int = Dice.roll()

 
  def moveFigure(player: Player, figureId: Int, steps: Int): Player = {
    println(s"[DEBUG] moveFigure: Player ${player.name}, figureId=$figureId, steps=$steps")

    //optionale figr, die vllt vorhanden ist also none oder some weil figureByID ja so definiert ist
    val figureOpt = player.figureById(figureId)
    if figureOpt.isEmpty then
      println("[DEBUG] No figure found.")
      return player //gibt spieler unverändert zurück, kein zug, weil figur fehlt

    val figure = figureOpt.get //mit get holen wir den inhalt aus some raus
    println(s"[DEBUG] Found figure with state: ${figure.state}")


    ///////////////////////////////////////
    figure.state match {
      //Fall 1: figur zu hause
      case Home =>
        if steps == 6 then //wenn steps 6 sind
          val startPos = board.startPosition(player.color) //hole startposition für spielerfarbe
          val blocker = players.flatMap(_.figures).find(_.state match //suche blocker (andere figur auf dem startfeld)
            case OnBoard(p) => p == startPos //suche erste Figur, die auf dem Startfeld steht, nur figuren auf dem brett werden geprüft
            case _ => false
          )

          ///////////////////////////////////////
          blocker match
            //Fall 1.1: Blocker ist eigene Figur, kein Zug
            case Some(b) if b.color == player.color =>
              println("[DEBUG] Start position is blocked by own figure.")
              player
              //Fall 1.2: Blocker ist Gegner
            case Some(b) =>
              println("[DEBUG] Start position is occupied by enemy — hitting it.")
              players = players.map { p => //erzeugen neue spielerliste mit ziel die figur auf dem startfeld zurück ins haus zu schicken
                if p.figures.contains(b) then //wenn spieler p die figur b hat....
                  val updated = p.figures.updated(p.figures.indexOf(b), b.copy(state = Home)) //figur b durch neue version ersetzt, aber jetzt mit status home
                  p.copy(figures = updated) //kopieren spieler p aber mit aktuelisierten figuren
                else p //wenn dieser spieler nicht der besitzer von b ist, nicht ändern
              }
              val updatedFigure = figure.copy(state = OnBoard(startPos)) //figur, die rauskommen soll, wird auf startfeld gesetzt
              val updatedPlayer = player.copy( //Spieler wird aktualsieiert, alte figur wird ersetzt durch neue version, die nun auf brett steht
                figures = player.figures.updated(player.figures.indexOf(figure), updatedFigure)
              )
              checkForFinish(updatedPlayer)
              //Fall 1.3: Kein Blocker gefunden, STartfeld frei
            case None =>
              println("[DEBUG] Start position is free.")
              val updatedFigure = figure.copy(state = OnBoard(startPos)) //figur wird auf das startfeld für die farbe gesetzt und ist von home zu on board
              val updatedPlayer = player.copy(
                figures = player.figures.updated(player.figures.indexOf(figure), updatedFigure)
              )
              checkForFinish(updatedPlayer)
              //SOnst kein 6er gewürfelt, kein zug möglich. spieler zurückgeben
        else player

        ///////////////////////////////////

      //Fall 2: Figur auf dem Brett
      case OnBoard(pos) =>
        val path = board.boardPath
        val goalEntry = board.goalEntryPosition(player.color)
        val goalPath = board.goalPath(player.color)
        val currentIndex = path.indexOf(pos)

        if currentIndex == -1 then return player //Wenn ungültig, nicht im Pfad, kein Zug


        val goalEntryIndex = path.indexOf(goalEntry) //Berechne Index des Zieleinstiegs
        val newIndex = currentIndex + steps //Berechne neuen Index

        val crossesGoalEntry = //Prüfe, ob Zieleisntieg überquert wird
          (currentIndex <= goalEntryIndex && newIndex > goalEntryIndex) || //Einstieg liegt hinter der Figur
          (goalEntryIndex < currentIndex && (newIndex % path.length) > goalEntryIndex) //Figur steht kurz vor Ende, Einstieg liegt am ANfang

        println(s"[DEBUG] currentIndex=$currentIndex, goalEntryIndex=$goalEntryIndex, newIndex=$newIndex")
        println(s"[DEBUG] crossesGoalEntry=$crossesGoalEntry")

        //Wenn Zieleinstieg überquert
        if crossesGoalEntry then
          val distanceToEntry = (path.length + goalEntryIndex - currentIndex) % path.length //Berechne Entfernung zum Zieleinstieg
          println(s"[DEBUG] distanceToEntry=$distanceToEntry")

          if steps > distanceToEntry then //Wenn Schritte > distanceToEntry
            val stepsIntoGoal = steps - distanceToEntry - 1 //-1 weil der Schritt aufs Ziel-Einstiegsfeld selbst nicht mehr Teil des Zielpfads ist
            println(s"[DEBUG] stepsIntoGoal=$stepsIntoGoal")

            if stepsIntoGoal >= 0 && stepsIntoGoal < goalPath.length then //Wenn Schritte im Zielpfad im erlaubten Bereich
              val newPos = goalPath(stepsIntoGoal) //Zielposition im zielpfad= goalPath(stepsIntoGoal)

              val goalBlocked = players.exists(_.figures.exists { //prüfe, ob Zielposition blockiert ist
                case f if f.id == figure.id => false //eigene Figur, nicht blockiert
                case Figure(_, color, Goal(p2)) if p2 == newPos && color == player.color => true
                case Figure(_, color, Finished) if newPos == goalPath.last && color == player.color => true
                case _ => false
              })

              //Wenn blockiert
              if goalBlocked then //zug nicht erlauben
                println("[DEBUG] Goal tile is blocked by own figure.")
                return player
              //Wenn nicht blockiert
              val updatedFigure =
                if stepsIntoGoal == goalPath.length - 1 then 
                  figure.copy(state = Finished) //wenn am letzten feld -> finished
                else
                  figure.copy(state = Goal(newPos)) //sonst goal
              val updatedPlayer = player.copy(
                figures = player.figures.updated(player.figures.indexOf(figure), updatedFigure) //spieler aktualisieren und checkforfinish
              )
              return checkForFinish(updatedPlayer)
            else
              println("[DEBUG] Invalid: stepsIntoGoal out of bounds. Cannot enter goal path.")
              return player
          else
            println("[DEBUG] Not enough steps to reach goal entry.")

////////////////////////////////////
        val nextIndex = (currentIndex + steps) % path.length
        val newPos = path(nextIndex)

        if player.hasFigureAt(newPos) then return player //Wenn eigenes Feld blockiert, kein Zug


        players = players.map { p => //prüfe, ob Gegner auf neuem Feld -> zurück nach Hause, setze eigene figut auf neue position
          if p != player && p.hasFigureAt(newPos) then
            val kicked = p.figureAt(newPos).get
            val updated = p.figures.updated(p.figures.indexOf(kicked), kicked.copy(state = Home))
            p.copy(figures = updated)
          else p
        }

        val updatedFigure = figure.copy(state = OnBoard(newPos))
        val updatedPlayer = player.copy(
          figures = player.figures.updated(player.figures.indexOf(figure), updatedFigure)
        )
        checkForFinish(updatedPlayer)

        //Fall 3: Figur im Zielpfad
      case Goal(pos) =>
        val goalPath = board.goalPath(player.color)
        val currentIndex = goalPath.indexOf(pos) //berechne index in zielpfad

        if currentIndex == -1 then return player //wenn ungültig, kein zug

        val targetIndex = currentIndex + steps //berechne ziel index

        if targetIndex >= goalPath.length then //wenn über zielpgad hinaus, kein zug
          println("[DEBUG] Move exceeds goal path. Invalid move.")
          return player

        val pathToCheck = goalPath.slice(currentIndex + 1, targetIndex + 1) //slice gibt uns den teil des arrays zurück, der zwischen den beiden indizes liegt, also die felder, die wir überqueren müssen. wir müssen prüfen, ob auf dem weg zur zielposition andere figuren im weg stehen

        val isBlocked = pathToCheck.exists { pos => //prüfe, ob ziel0pfad felder blockeirt sind (eigene figur)
          players.exists(_.figures.exists { //wenn blockiert, kein zug
            case f if f.id == figure.id => false
            case Figure(_, color, Goal(p2)) if p2 == pos && color == player.color => true
            case Figure(_, color, Finished) if goalPath.last == pos && color == player.color => true
            case _ => false
          })
        }

        if isBlocked then //kein zug wenn blockiert
          println("[DEBUG] Move blocked in goal path. Cannot proceed.")
          return player

        val newPos = goalPath(targetIndex)

        val updatedFigure =
          if targetIndex == goalPath.length - 1 then //Wenn Zielposition das letzte Feld im Zielpfad ist
            figure.copy(state = Finished) //figur finished
          else
            figure.copy(state = Goal(newPos)) //sonst figur auf goal

        val updatedPlayer = player.copy( //spieler aktualisieren und checkforfinish
          figures = player.figures.updated(player.figures.indexOf(figure), updatedFigure)
        )
        checkForFinish(updatedPlayer)

      case Finished => player
    }
  }




  def checkForFinish(updatedPlayer: Player): Player =
    val allFinished = updatedPlayer.figures.forall(f => f.isFinished || f.isInGoal)
    val alreadyOut = updatedPlayer.status match
      case Out(_) => true
      case _ => false

    if allFinished && !alreadyOut then
      placementsGiven += 1
      updatedPlayer.copy(status = Out(placementsGiven))
    else updatedPlayer
