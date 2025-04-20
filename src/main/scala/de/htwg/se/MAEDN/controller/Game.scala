package de.htwg.se.MAEDN.controller

import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util._

class Game(initialPlayers: List[Player]):
  var players: List[Player] = initialPlayers
  var currentPlayerIndex: Int = 0
  val board = new Board()
  var placementsGiven: Int = 0

  def currentPlayer: Player = players(currentPlayerIndex)

  def nextPlayer(): Unit =
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size

  def isGameOver: Boolean =
    players.count(p => p.status.isInstanceOf[Out]) == players.size - 1

  def rollDice(): Int =
    Dice.roll()

  def moveFigure(player: Player, figureId: Int, steps: Int): Player = {
    println(s"[DEBUG] moveFigure: Player ${player.name}, figureId=$figureId, steps=$steps")

    val figureOpt = player.figureById(figureId)
    if figureOpt.isEmpty then
      println("[DEBUG] No figure found.")
      return player

    val figure = figureOpt.get
    println(s"[DEBUG] Found figure with state: ${figure.state}")

    figure.state match {
      case Home =>
        if steps == 6 then
          val startPos = board.startPosition(player.color)
          val blocker = players.flatMap(_.figures).find(_.state match
            case OnBoard(p) => p == startPos
            case _ => false
          )

          blocker match
            case Some(b) if b.color == player.color =>
              println("[DEBUG] Start position is blocked by own figure.")
              player
            case Some(b) =>
              println("[DEBUG] Start position is occupied by enemy — hitting it.")
              players = players.map { p =>
                if p.figures.contains(b) then
                  val updated = p.figures.updated(p.figures.indexOf(b), b.copy(state = Home))
                  p.copy(figures = updated)
                else p
              }
              val updatedFigure = figure.copy(state = OnBoard(startPos))
              val updatedPlayer = player.copy(
                figures = player.figures.updated(player.figures.indexOf(figure), updatedFigure)
              )
              checkForFinish(updatedPlayer)
            case None =>
              println("[DEBUG] Start position is free.")
              val updatedFigure = figure.copy(state = OnBoard(startPos))
              val updatedPlayer = player.copy(
                figures = player.figures.updated(player.figures.indexOf(figure), updatedFigure)
              )
              checkForFinish(updatedPlayer)
        else
          player

      case OnBoard(pos) =>
        val path = board.boardPath
        val goalEntry = board.goalEntryPosition(player.color)
        val goalPath = board.goalPath(player.color)
        val currentIndex = path.indexOf(pos)

        if currentIndex == -1 then return player

        val goalEntryIndex = path.indexOf(goalEntry)
        val newIndex = currentIndex + steps

        val crossesGoalEntry =
          (currentIndex <= goalEntryIndex && newIndex > goalEntryIndex) ||
          (goalEntryIndex < currentIndex && (newIndex % path.length) > goalEntryIndex)

        println(s"[DEBUG] currentIndex=$currentIndex, goalEntryIndex=$goalEntryIndex, newIndex=$newIndex")
        println(s"[DEBUG] crossesGoalEntry=$crossesGoalEntry")

        if crossesGoalEntry then
          val distanceToEntry = (path.length + goalEntryIndex - currentIndex) % path.length
          println(s"[DEBUG] distanceToEntry=$distanceToEntry")

          if steps > distanceToEntry then
            val stepsIntoGoal = steps - distanceToEntry - 1
            println(s"[DEBUG] stepsIntoGoal=$stepsIntoGoal")

            if stepsIntoGoal >= 0 && stepsIntoGoal < goalPath.length then
              val newPos = goalPath(stepsIntoGoal)

              val goalBlocked = players.exists(_.figures.exists {
                case f if f.id == figure.id => false
                case Figure(_, _, Goal(p2)) => p2 == newPos
                case Figure(_, _, Finished) => goalPath.last == newPos
                case _ => false
              })

              if goalBlocked then
                println("[DEBUG] Goal tile is already occupied.")
                return player

              val updatedFigure =
                if stepsIntoGoal == goalPath.length - 1 then
                  figure.copy(state = Finished)
                else
                  figure.copy(state = Goal(newPos))
              val updatedPlayer = player.copy(
                figures = player.figures.updated(player.figures.indexOf(figure), updatedFigure)
              )
              return checkForFinish(updatedPlayer)
            else
              println("[DEBUG] Invalid: stepsIntoGoal out of bounds. Cannot enter goal path.")
              return player
          else
            println("[DEBUG] Not enough steps to reach goal entry.")

        val nextIndex = (currentIndex + steps) % path.length
        val newPos = path(nextIndex)

        if player.hasFigureAt(newPos) then return player

        // Handle hit
        players = players.map { p =>
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

      case Goal(pos) =>
        val goalPath = board.goalPath(player.color)
        val currentIndex = goalPath.indexOf(pos)

        if currentIndex == -1 then return player

        val targetIndex = currentIndex + steps

        if targetIndex >= goalPath.length then
          println("[DEBUG] Move exceeds goal path. Invalid move.")
          return player

        val pathToCheck = goalPath.slice(currentIndex + 1, targetIndex + 1)

        val isBlocked = pathToCheck.exists { pos =>
          players.exists(_.figures.exists {
            case f if f.id == figure.id => false
            case Figure(_, _, Goal(p2)) if p2 == pos => true
            case Figure(_, _, Finished) if goalPath.last == pos => true
            case _ => false
          })
        }

        if isBlocked then
          val remainingFields = goalPath.slice(currentIndex + 1, goalPath.length)
          val allBlocked = remainingFields.forall(pos =>
            players.exists(p =>
              p != player && p.figures.exists {
                case Figure(_, _, Goal(p2)) => p2 == pos
                case Figure(_, _, Finished) => goalPath.last == pos
                case _ => false
              }
            )
          )

          if allBlocked then
            println("[DEBUG] All goal fields ahead are blocked. Auto-finishing.")
            val updatedFigure = figure.copy(state = Finished)
            val updatedPlayer = player.copy(
              figures = player.figures.updated(player.figures.indexOf(figure), updatedFigure)
            )
            return checkForFinish(updatedPlayer)
          else
            println("[DEBUG] Move blocked in goal path.")
            return player

        val newPos = goalPath(targetIndex)

        val updatedFigure =
          if targetIndex == goalPath.length - 1 then
            figure.copy(state = Finished)
          else
            figure.copy(state = Goal(newPos))

        val updatedPlayer = player.copy(
          figures = player.figures.updated(player.figures.indexOf(figure), updatedFigure)
        )
        checkForFinish(updatedPlayer)

      case Finished =>
        player
    }
  }

  private def checkForFinish(updatedPlayer: Player): Player =
    val allFinished = updatedPlayer.figures.forall(_.isFinished)
    val alreadyOut = updatedPlayer.status match
      case Out(_) => true
      case _ => false

    if allFinished && !alreadyOut then
      placementsGiven += 1
      updatedPlayer.copy(status = Out(placementsGiven))
    else
      updatedPlayer
