package controller

import model._
import util.Dice

class Game(initialPlayers: List[Player]):
  var players: List[Player] = initialPlayers
  var currentPlayerIndex: Int = 0

  def currentPlayer: Player = players(currentPlayerIndex)

  def nextPlayer(): Unit =
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size

  def isGameOver: Boolean =
    players.exists(p => p.figures.forall(_.state == Finished))

  def rollDice(): Int =
    Dice.roll()

  def moveFigure(player: Player, figureId: Int, steps: Int): Player =
    val updatedFigures = player.figures.map {
      case f if f.id == figureId && f.state.isInstanceOf[OnBoard] =>
        val OnBoard(pos) = f.state
        val newPos = Position(pos.x + steps, pos.y)
        f.copy(state = OnBoard(newPos))
      case other => other
    }

    player.copy(figures = updatedFigures)
