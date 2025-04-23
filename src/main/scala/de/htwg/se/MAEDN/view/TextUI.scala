package de.htwg.se.MAEDN.view


import de.htwg.se.MAEDN.controller._
import de.htwg.se.MAEDN.model._
import scala.io.StdIn.readLine


class TextUI(controller: GameController): //GameController kümmert sich um roll, move, ednturn usw


  def run(): Unit = //Unit = sowas wie void
    println("\nThe game begins!")


    while !controller.isGameOver do //läuft solange das spiel nicht vorbei ist
      var player = controller.currentPlayer //holt aktuellen spieler


      val checkedPlayer = controller.game.checkForFinish(player) ///////////////////Hat der aktuelle Spieler alle Figuren im Ziel? ja -> out
      if checkedPlayer != player then //hat sich beim spieler was geändert?
        controller.game.players = controller.game.players.updated(controller.game.currentPlayerIndex, checkedPlayer) //update
        controller.endTurn()
      else //also checkedplayer == player also Spieler NICHT fertig...
        while player.status.isInstanceOf[Out] do //Überspringe Spieler, die out sind
          controller.endTurn()
          player = controller.currentPlayer //aktueller Spieler ändert sich durch endturn, also muss aktueller spieler neu geholt werden


        val allAtHome = player.figures.forall(_.isAtHome)
        var remainingRolls = if (allAtHome) 3 else 1
        var takeAnotherTurn = false
        var movedSuccessfully = false


        while remainingRolls > 0 do
          // interpolations verwenden um Variablen in Strings zu verwenden wie in GameController
          readLine(s"\n${player.name}'s turn (${player.color}), press ENTER to roll the dice... (${remainingRolls} roll${if remainingRolls > 1 then "s" else ""} left)")
          val roll = controller.roll()
          if roll == 6 then
            remainingRolls = 1 //noch mal würfeln bei 6
          else remainingRolls -= 1 //sonst wurf verbraucht verbraucht


         




          clearScreen()
          println(s"${player.name} rolled a $roll")
          println("Current Board:")
          println(renderBoard())




          println("Your Figures:")
          for (figure, i) <- player.figures.zipWithIndex do //Pattern Match auf Tuppel (Figur, Index) alle figuren des spielers + Index
            println(s"  [$i] ${figure.state}") //gibt die Figur mit dem Index aus und spielstand


          val board = controller.game.board


          val validFigures = player.figures.zipWithIndex.flatMap { //Liste aller Figuren des aktuellen Spielers


            //falls: Figur zu hause ist und 6 gewürfelt wurde
            case (Figure(_, _, Home), i) if roll == 6 =>
              val startPos = board.startPosition(player.color) //Startposition auf dem Spielfeld für diesen Spieler holen
              if player.hasFigureAt(startPos) then None else Some(i) //Wenn Figur auf Startposition ist, dann kann sie nicht bewegt werden, also None zurückgeben, sonst die Figur zurückgeben


            //falls: Figur auf dem Spielfeld ist
            case (fig @ Figure(_, _, OnBoard(pos)), i) => //mit @ könne wir auf die Figur zugreifen!!!
              val path = board.boardPath
              val goalEntry = board.goalEntryPosition(player.color) //Einstiefspunkt zum Zielbereich
              val goalPath = board.goalPath(player.color)
              val currentIndex = path.indexWhere(_ == pos) //sucht in Path an welcher Stelle sich aktuelle Position der Figur befindet
              if currentIndex == -1 then None //wenn aktuelle Position nicht im Pfad ist, dann ungültig, none
              else
                val goalEntryIndex = path.indexWhere(_ == goalEntry) //an welcher stelle im path lieft ziel-einstieg.position goalEntry?
                val newIndex = currentIndex + roll //Wert entlang des RUndwegs
                val crossesGoalEntry = //überquert die figur mit diesem zug die einstiegsstelle zum zielbereich?
                  (currentIndex <= goalEntryIndex && newIndex > goalEntryIndex) || // Man bewegt sich vorwärts auf dem pfad und überspringt den ziel einstieg
                  (goalEntryIndex < currentIndex && (newIndex % path.length) > goalEntryIndex) // man geht über das ende des pfads hinaus


                //Wenn Figur den Ziel-Einstieg überquert, dann...
                if crossesGoalEntry then
                  val distanceToEntry = (path.length + goalEntryIndex - currentIndex) % path.length /////////Wie viele Schritte braucht man bis zum Zieleinstieg?
                  if roll > distanceToEntry then
                    val stepsIntoGoal = roll - distanceToEntry - 1 //berechnet, wie viele schritte ich im zielpfad gehe, -1 weil der erste schritt über dne einstiegspunkt mich aufs erste zielfeld brignt
                    if stepsIntoGoal >= 0 && stepsIntoGoal < goalPath.length then
                      val newPos = goalPath(stepsIntoGoal)
                      val blocked = controller.game.players.exists(_.figures.exists {
                        case f if f.id == fig.id => false
                        case Figure(_, color, Goal(p)) if p == newPos && color == player.color => true
                        case Figure(_, color, Finished) if newPos == goalPath.last && color == player.color => true
                        case _ => false
                      })
                      if blocked then None else Some(i)
                    else None
                  else
                    val newPos = path((currentIndex + roll) % path.length)
                    if player.hasFigureAt(newPos) then None else Some(i)
                else
                  val newPos = path((currentIndex + roll) % path.length)
                  if player.hasFigureAt(newPos) then None else Some(i)




            //falls: Figur ist im Zielbereich
            case (fig @ Figure(_, _, Goal(pos)), i) =>
              val goalPath = board.goalPath(player.color)
              val currentIndex = goalPath.indexWhere(_ == pos)
              if currentIndex == -1 then None
              else
                val targetIndex = currentIndex + roll
                if targetIndex >= goalPath.length then None //würde figur über letztes zielfeld hinaus laufen?
                else
                  val newPos = goalPath(targetIndex)  //neue Position im Zielbereich
                  val blocked = controller.game.players.exists(_.figures.exists {
                    case f if f.id == fig.id => false //eigene Figur, die bewegt wird
                    case Figure(_, color, Goal(p)) if p == newPos && color == player.color => true //andere figur meiner farbe steht im zielpfad -> blockieren
                    case Figure(_, color, Finished) if newPos == goalPath.last && color == player.color => true //ist das zielfeld das letzte feld im zielpfad und steht da ne finished figur? -> blockieren
                    case _ => false //egal was kommt, ist nicht relevant
                  })
                  if blocked then None else Some(i) //wenn zielfeld blockiert, nichts, wenn frei, gültiger zug


            case _ => None //fall für allea ndere, was nicht oben definiert wurde
          }


          if validFigures.isEmpty then
            println("No valid moves.")
          else if validFigures.size == 1 then //nur eine bewegbare figur
            val index = validFigures.head //hol ersten und einzigen eintrag in der liste
            val figure = player.figures(index)
            movedSuccessfully = controller.move(figure.id, roll)
            player = controller.currentPlayer
            if movedSuccessfully && roll == 6 && figure.isOnBoard then
              takeAnotherTurn = true //logik für einen pash, noch mal würfeln bei einer 6


            println(if movedSuccessfully then "Moved." else "No valid move.")
          else
            println(s"Choose a figure to move: ${validFigures.mkString("[", ", ", "]")}")
            var moved = false
            while !moved do //solange bis moved true ist
              val input = readLine("Enter figure number (or 'skip'): ").trim //trim entfernt leerzeichen unnötige
              if input == "skip" then
                println("Turn skipped.")
                moved = true //beendet schleife, weil zug abgeschlossen ist
              else
                input.toIntOption match //wandelt benutzereingabe in eine zahl um
                  case Some(figIndex) if validFigures.contains(figIndex) => //wenn eingabe gültige zahl ist
                    val figure = player.figures(figIndex) //holt figur aus der liste mit dem index
                    moved = controller.move(figure.id, roll)
                    movedSuccessfully = moved //speichert, ob ein zug tatsächlich passiert
                    player = controller.currentPlayer
                    println(if moved then "Moved." else "Invalid move.")
                  case _ => println("Invalid input. Try again.")


        val playerAfterTurn = controller.game.currentPlayer
        val checkedAfterMove = controller.game.checkForFinish(playerAfterTurn) //ist der spieler nach seienm zu gfertig?
        if checkedAfterMove != playerAfterTurn then //wenn sich beim spieler etwas geändert ha...
          controller.game.players = controller.game.players.updated(controller.game.currentPlayerIndex, checkedAfterMove) //spielerliste aktualsiieren


        if !movedSuccessfully && !takeAnotherTurn then //zug vorbei
          println("Turn ended.")


        controller.endTurn()


    println("\nGame over!")
    for player <- controller.game.players do
      player.status match //unterscuht status jedes spielers
        case Out(place) => println(s"${player.name} placed $place") //wenn out, dann platz erreicht:
        case _ => println(s"${player.name} did not finish") //der, der nicht fertig wurde, did not finish






  def renderBoard(): String =
    val layout = Array.fill(11, 11)(formatField(""))
    for pos <- controller.game.board.boardPath do layout(pos.y)(pos.x) = formatField("o") //alle felder des normalen spielfpfads mit o


    val redGoals = List(Position(9, 5), Position(8, 5), Position(7, 5), Position(6, 5))
    val greenGoals = List(Position(5, 1), Position(5, 2), Position(5, 3), Position(5, 4))
    val blueGoals = List(Position(5, 9), Position(5, 8), Position(5, 7), Position(5, 6))
    val yellowGoals = List(Position(1, 5), Position(2, 5), Position(3, 5), Position(4, 5))


    redGoals.foreach(p => layout(p.y)(p.x) = formatField("R")) //zeichnet für die zielfelder fabrliche unterschiede
    greenGoals.foreach(p => layout(p.y)(p.x) = formatField("G"))
    blueGoals.foreach(p => layout(p.y)(p.x) = formatField("B"))
    yellowGoals.foreach(p => layout(p.y)(p.x) = formatField("Y"))


    for player <- controller.game.players do //schleife über alle spieler und deren figuren
      for figure <- player.figures do
        figure.state match
          case OnBoard(pos) => layout(pos.y)(pos.x) = formatField(s"${player.color.toString.head}${figure.id % 4}") //zeigt Ids innerhalb der farbe
          case Goal(pos)    => layout(pos.y)(pos.x) = formatField(s"${player.color.toString.head}${figure.id % 4}")
          case Finished     =>
            val pos = controller.game.board.goalPath(player.color).last
            layout(pos.y)(pos.x) = formatField(s"${player.color.toString.head}${figure.id % 4}")
          case _ =>


    layout.map(_.mkString("")).mkString("\n") //map verbindet jede zeile zu einer zeichenkette, also spielfeld zeile und verbindet alle zeilen des boards mit zeilenumbruch -> ergibt anzeigbaren spielfeld textblock


  def formatField(content: String): String =
    if content.length == 3 then content //spielfeld feld genau 3 zeichen breit, passt
    else if content.length == 2 then s" $content" //ein leerzeichen davor
    else if content.length == 1 then s" $content " //ein leerzeichen danach
    else "   " //leeres feld, 3 zeichen


  def clearScreen(): Unit = //löscht den bildschirm
    print("\u001b[2J") //bildschirm löschen
    print("\u001b[H") //cursor nach oben links

