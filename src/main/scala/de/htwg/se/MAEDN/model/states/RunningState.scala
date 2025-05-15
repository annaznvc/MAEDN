package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.model.{IState, Manager, Board, Player, State}
import de.htwg.se.MAEDN.util.{Event, Dice}
import de.htwg.se.MAEDN.controller.Controller

case class RunningState(
    override val controller: Controller,
    override val moves: Int,
    override val board: Board,
    override val players: List[Player],
    override val rolled: Int = 0,
    override val selectedFigure: Int = 0
) extends Manager {
  override val state: State = State.Running

  override def moveUp(): Manager = {
    val selected = (selectedFigure + 1) % players.head.figures.size
    controller.eventQueue.enqueue(Event.ChangeSelectedFigureEvent(selected))
    controller.notifyObservers()
    copy(selectedFigure = selected)
  }

  override def moveDown(): Manager = {
    val selected = (selectedFigure - 1 + players.head.figures.size) % players.head.figures.size
    controller.eventQueue.enqueue(Event.ChangeSelectedFigureEvent(selected))
    controller.notifyObservers()
    copy(selectedFigure = selected)
  }

  override def playDice(): Manager = {
    val newRolled = Dice.roll()
    controller.eventQueue.enqueue(Event.PlayDiceEvent(newRolled))
    controller.notifyObservers()
    copy(rolled = newRolled)
  }

  override def playNext(): Manager = {
    rolled match {
      case -1 =>
        controller.eventQueue.enqueue(Event.PlayNextEvent((getCurrentPlayer + 1) % players.size))
        controller.notifyObservers()
        copy(moves = moves + 1, rolled = 0)
      case 0 =>
        playDice()
      case _ =>
        moveFigure()
    }
  }

  override def moveFigure(): Manager = {
    if (
      !board.checkIfMoveIsPossible(
        players.flatMap(_.figures),
        rolled,
        players(getCurrentPlayer).color
      )
    ) {
      controller.eventQueue.enqueue(Event.PlayNextEvent((getCurrentPlayer + 1) % players.size))
      controller.notifyObservers()
      return copy(rolled = 0, moves = moves + 1)
    }

    val figures = players.flatMap(_.figures)
    val figure = players(getCurrentPlayer).figures(selectedFigure)
    val newFigures = board.moveFigure(figure, figures, rolled)

    if (newFigures == figures) {
      controller.eventQueue.enqueue(Event.InvalidMoveEvent)
      controller.notifyObservers()
      this
    } else {
      controller.eventQueue.enqueue(Event.MoveFigureEvent(figure.id))
      controller.notifyObservers()

      val updatedPlayers = players.zipWithIndex.map { case (player, _) =>
        val playerFigures = newFigures.filter(_.owner.id == player.id)
        player.copy(figures = playerFigures)
      }

      copy(
        players = updatedPlayers,
        rolled = if (rolled == 6) 0 else -1
      )
    }
  }

  override def quitGame(): Manager = {
    controller.eventQueue.enqueue(Event.BackToMenuEvent)
    controller.notifyObservers()
    MenuState(controller, moves, board, players)
  }
}