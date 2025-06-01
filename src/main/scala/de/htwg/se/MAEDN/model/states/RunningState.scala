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
    val nextFigure = getNextMovableFigure(newRolled)
    Success(
      copy(
        rolled = newRolled,
        selectedFigure = if (nextFigure == -1) 0 else nextFigure
      )
    )
  }

  override def playNext(): Try[Manager] = rolled match {
    case -1 =>
      controller.eventQueue.enqueue(
        Event.PlayNextEvent((getCurrentPlayer + 1) % players.size)
      )
      Success(copy(moves = moves + 1, rolled = 0, selectedFigure = 0))
    case 0 =>
      playDice()
    case _ =>
      val canMoveAny = players(getCurrentPlayer).figures.exists { figure =>
        board.canFigureMove(figure, players.flatMap(_.figures), rolled)
      }
      if (!canMoveAny) {
        controller.eventQueue.enqueue(
          Event.PlayNextEvent((getCurrentPlayer + 1) % players.size)
        )
        Success(copy(rolled = 0, moves = moves + 1, selectedFigure = 0))
      } else {
        moveFigure() match {
          case s @ Success(_) => s
          case Failure(_) =>
            val stillCanMove = players(getCurrentPlayer).figures.exists {
              figure =>
                board.canFigureMove(figure, players.flatMap(_.figures), rolled)
            }
            if (!stillCanMove) {
              controller.eventQueue.enqueue(
                Event.PlayNextEvent((getCurrentPlayer + 1) % players.size)
              )
              Success(copy(rolled = 0, moves = moves + 1, selectedFigure = 0))
            } else {
              Failure(new IllegalArgumentException("Invalid move!"))
            }
        }
      }
  }

  override def moveFigure(): Try[Manager] = {
    val currentFigures = players(getCurrentPlayer).figures
    val movableIndices = currentFigures.zipWithIndex.collect {
      case (figure, idx)
          if board.canFigureMove(figure, players.flatMap(_.figures), rolled) =>
        idx
    }
    if (movableIndices.isEmpty) {
      controller.eventQueue.enqueue(
        Event.PlayNextEvent((getCurrentPlayer + 1) % players.size)
      )
      return Success(copy(rolled = 0, moves = moves + 1, selectedFigure = 0))
    }
    val selectedIdx =
      if (movableIndices.size == 1) movableIndices.head else selectedFigure
    val selectedFig = currentFigures(selectedIdx)
    if (!board.canFigureMove(selectedFig, players.flatMap(_.figures), rolled)) {
      if (movableIndices.isEmpty) {
        controller.eventQueue.enqueue(
          Event.PlayNextEvent((getCurrentPlayer + 1) % players.size)
        )
        Success(copy(rolled = 0, moves = moves + 1, selectedFigure = 0))
      } else {
        Success(copy(selectedFigure = movableIndices.head))
      }
    } else {
      val figures = players.flatMap(_.figures)
      val newFigures = board.moveFigure(selectedFig, figures, rolled)
      if (newFigures == figures) {
        if (movableIndices.isEmpty) {
          controller.eventQueue.enqueue(
            Event.PlayNextEvent((getCurrentPlayer + 1) % players.size)
          )
          Success(copy(rolled = 0, moves = moves + 1, selectedFigure = 0))
        } else {
          Success(copy(selectedFigure = movableIndices.head))
        }
      } else {
        controller.eventQueue.enqueue(Event.MoveFigureEvent(selectedFig.id))
        val updatedPlayers = players.map { player =>
          player.copy(figures = newFigures.filter(_.owner.id == player.id))
        }
        Success(
          copy(
            players = updatedPlayers,
            rolled = if (rolled == 6) 0 else -1,
            selectedFigure = 0
          )
        )
      }
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

  private def getNextMovableFigure(rolledValue: Int = rolled): Int =
    players(getCurrentPlayer).figures.zipWithIndex
      .find { case (figure, _) =>
        board.canFigureMove(figure, players.flatMap(_.figures), rolledValue)
      }
      .map(_._2)
      .getOrElse(-1)
}
