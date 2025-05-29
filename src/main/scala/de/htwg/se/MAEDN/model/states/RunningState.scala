package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.model.{IState, Manager, Board, Player, State}
import de.htwg.se.MAEDN.util.{Event, Dice}
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.GameData

import scala.util.{Try, Success, Failure}

case class RunningState(
    override val controller: Controller,
    override val moves: Int,
    override val board: Board,
    override val players: List[Player],
    override val rolled: Int = 0,
    override val selectedFigure: Int = 0
) extends Manager {
  override val state: State = State.Running

  override def moveUp(): Try[Manager] = {
    val selected = (selectedFigure + 1) % players.head.figures.size
    controller.eventQueue.enqueue(Event.ChangeSelectedFigureEvent(selected))
    Try(copy(selectedFigure = selected))
  }

  override def moveDown(): Try[Manager] = {
    val selected =
      (selectedFigure - 1 + players.head.figures.size) % players.head.figures.size
    controller.eventQueue.enqueue(Event.ChangeSelectedFigureEvent(selected))
    Try(copy(selectedFigure = selected))
  }

  override def playDice(): Try[Manager] = {
    val newRolled = Dice.roll()
    controller.eventQueue.enqueue(Event.PlayDiceEvent(newRolled))
    Try(copy(rolled = newRolled, selectedFigure = getNextMovableFigure()))
  }

  override def playNext(): Try[Manager] = {
    rolled match {
      case -1 => {
        controller.eventQueue.enqueue(
          Event.PlayNextEvent((getCurrentPlayer + 1) % players.size)
        )
        Try(
          copy(
            moves = moves + 1,
            rolled = 0,
            selectedFigure = getNextMovableFigure()
          )
        )
      }
      case 0 => playDice()
      case _ => moveFigure()
    }
  }

  override def moveFigure(): Try[Manager] = {
    if (
      !board.checkIfMoveIsPossible(
        players.flatMap(_.figures),
        rolled,
        players(getCurrentPlayer).color
      )
    ) {
      controller.eventQueue.enqueue(
        Event.PlayNextEvent(
          (getCurrentPlayer + 1) % players.size
        )
      )
      return Try(
        copy(
          rolled = 0,
          moves = moves + 1,
          selectedFigure = getNextMovableFigure()
        )
      )
    }

    val figures = players.flatMap(_.figures)
    val figure = players(getCurrentPlayer).figures(selectedFigure)

    val newFigures = board.moveFigure(figure, figures, rolled)

    if (newFigures == figures) {
      Failure(new IllegalArgumentException("Invalid move!"))
    } else {
      // Update the players with the new figures
      controller.eventQueue.enqueue(Event.MoveFigureEvent(figure.id))
      val updatedPlayers = players.zipWithIndex.map { case (player, index) =>
        val playerFigures = newFigures.filter(_.owner.id == player.id)
        player.copy(figures = playerFigures)
      }

      Success(
        copy(
          players = updatedPlayers,
          rolled = if (rolled == 6) 0 else -1
        )
      )
    }
  }

  override def quitGame(): Try[Manager] = {
    controller.eventQueue.enqueue(Event.BackToMenuEvent)
    Try(MenuState(controller, moves, board, players))
  }

  override def createMemento: Option[GameData] =
    Some(
      GameData(
        moves,
        board,
        players,
        selectedFigure,
        rolled
      )
    )

  private def getNextMovableFigure(): Int = {
    players(getCurrentPlayer).figures.zipWithIndex.find { case (figure, _) =>
      board.canFigureMove(figure, players.flatMap(_.figures), rolled)
    } match {
      case Some((_, index)) => index
      case None             => 0
    }
  }
}
