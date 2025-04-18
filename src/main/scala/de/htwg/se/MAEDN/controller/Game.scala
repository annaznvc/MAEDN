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

  def moveFigure(player: Player, figureId: Int, steps: Int): Player =
    val figureOpt = player.figureById(figureId)
    if figureOpt.isEmpty then return player

    val figure = figureOpt.get

    figure.state match
      case Home =>
        if steps == 6 then
          val startPos = board.startPosition(player.color)
          if players.exists(_.hasFigureAt(startPos)) then return player // Blocked
          else
            val updated = figure.copy(state = OnBoard(startPos))
            val updatedPlayer = player.copy(figures = player.figures.updated(player.figures.indexOf(figure), updated))
            return checkForFinish(updatedPlayer)
        else return player

      case OnBoard(pos) =>
        val goalEntry = board.goalEntryPosition(player.color)
        if pos == goalEntry && steps == 1 then
          val goalPath = board.goalPath(player.color)
          val firstGoal = goalPath.head
          val updatedFigure = figure.copy(state = Goal(firstGoal))
          val updatedPlayer = player.copy(figures = player.figures.updated(player.figures.indexOf(figure), updatedFigure))
          return checkForFinish(updatedPlayer)
        else
          val path = board.boardPath
          val currentIndex = path.indexOf(pos)
          if currentIndex == -1 then return player

          val newIndex = (currentIndex + steps) % path.length
          val newPos = path(newIndex)

          if player.hasFigureAt(newPos) then return player // blocked by own figure

          players = players.map { p =>
            if p != player && p.hasFigureAt(newPos) then
              val kickedFigure = p.figureAt(newPos).get
              val updatedFigures = p.figures.updated(p.figures.indexOf(kickedFigure), kickedFigure.copy(state = Home))
              p.copy(figures = updatedFigures)
            else p
          }

          val updatedFigure = figure.copy(state = OnBoard(newPos))
          val updatedPlayer = player.copy(figures = player.figures.updated(player.figures.indexOf(figure), updatedFigure))
          return checkForFinish(updatedPlayer)

      case Goal(pos) =>
        val goalPath = board.goalPath(player.color)
        val currentIndex = goalPath.indexOf(pos)

        if currentIndex == -1 || currentIndex + steps >= goalPath.length then
          return player // can't move beyond goal
        else
          val newPos = goalPath(currentIndex + steps)

          val updatedFigure =
            if currentIndex + steps == goalPath.length - 1 then
              figure.copy(state = Finished)
            else
              figure.copy(state = Goal(newPos))

          val updatedPlayer = player.copy(figures = player.figures.updated(player.figures.indexOf(figure), updatedFigure))
          return checkForFinish(updatedPlayer)

      case Finished =>
        player

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
