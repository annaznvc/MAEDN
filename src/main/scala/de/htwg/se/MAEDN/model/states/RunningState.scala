package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.model.{IManager, Board, Player, State, GameData}
import de.htwg.se.MAEDN.util.{Event, Dice}
import de.htwg.se.MAEDN.controller.IController

import scala.util.{Try, Success, Failure}

case class RunningState(
    override val controller: IController,
    override val moves: Int,
    override val board: Board,
    override val players: List[Player],
    override val rolled: Int = 0,
    override val selectedFigure: Int = 0
) extends IManager {

  override val state: State = State.Running

  override def moveUp(): Try[IManager] = Try {
    val selected = (selectedFigure + 1) % players.head.figures.size
    controller.enqueueEvent(Event.ChangeSelectedFigureEvent(selected))
    copy(selectedFigure = selected)
  }

  override def moveDown(): Try[IManager] = Try {
    val selected =
      (selectedFigure - 1 + players.head.figures.size) % players.head.figures.size
    controller.enqueueEvent(Event.ChangeSelectedFigureEvent(selected))
    copy(selectedFigure = selected)
  }

  override def playDice(): Try[IManager] = Try {
    val newRolled = Dice.roll()
    controller.enqueueEvent(Event.PlayDiceEvent(newRolled))
    val nextFigure = getNextMovableFigure(newRolled)
    copy(
      rolled = newRolled,
      selectedFigure = if (nextFigure == -1) 0 else nextFigure
    )
  }

  override def playNext(): Try[IManager] = rolled match {
    case -1 =>
      controller.enqueueEvent(
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
        controller.enqueueEvent(
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
              controller.enqueueEvent(
                Event.PlayNextEvent((getCurrentPlayer + 1) % players.size)
              )
              Success(copy(rolled = 0, moves = moves + 1, selectedFigure = 0))
            } else {
              Failure(new IllegalArgumentException("Invalid move!"))
            }
        }
      }
  }

  override def moveFigure(): Try[IManager] = Try {
    val currentFigures = players(getCurrentPlayer).figures
    val movableIndices = currentFigures.zipWithIndex.collect {
      case (figure, idx)
          if board.canFigureMove(figure, players.flatMap(_.figures), rolled) =>
        idx
    }
    if (movableIndices.isEmpty) {
      controller.enqueueEvent(
        Event.PlayNextEvent((getCurrentPlayer + 1) % players.size)
      )
      return Success(
        copy(rolled = 0, moves = moves + 1, selectedFigure = 0)
      )
    }
    val selectedIdx =
      if (movableIndices.size == 1) movableIndices.head else selectedFigure
    val selectedFig = currentFigures(selectedIdx)
    if (!board.canFigureMove(selectedFig, players.flatMap(_.figures), rolled)) {
      if (movableIndices.isEmpty) {
        controller.enqueueEvent(
          Event.PlayNextEvent((getCurrentPlayer + 1) % players.size)
        )
        Success(copy(rolled = 0, moves = moves + 1, selectedFigure = 0)).get
      } else {
        copy(selectedFigure = movableIndices.head)
      }
    } else {
      val figures = players.flatMap(_.figures)
      val newFigures = board.moveFigure(selectedFig, figures, rolled)
      if (newFigures == figures) {
        if (movableIndices.isEmpty) {
          controller.enqueueEvent(
            Event.PlayNextEvent((getCurrentPlayer + 1) % players.size)
          )
          Success(copy(rolled = 0, moves = moves + 1, selectedFigure = 0)).get
        } else {
          copy(selectedFigure = movableIndices.head)
        }
      } else {
        controller.enqueueEvent(Event.MoveFigureEvent(selectedFig.id))
        val updatedPlayers = players.map { player =>
          player.copy(figures = newFigures.filter(_.owner.id == player.id))
        }
        copy(
          players = updatedPlayers,
          rolled = if (rolled == 6) 0 else -1,
          selectedFigure = 0
        )
      }
    }
  }

  override def quitGame(): Try[IManager] = Try {
    controller.enqueueEvent(Event.BackToMenuEvent)
    // Hier ggf. MenuState(controller, ...) zurückgeben, falls sinnvoll
    this
  }

  override def startGame(): Try[IManager] = Try(this)
  override def increaseBoardSize(): Try[IManager] = Try(this)
  override def decreaseBoardSize(): Try[IManager] = Try(this)
  override def increaseFigures(): Try[IManager] = Try(this)
  override def decreaseFigures(): Try[IManager] = Try(this)

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

  override def getPlayerCount: Int = players.size
  override def getFigureCount: Int =
    players.headOption.map(_.figures.size).getOrElse(0)
  override def getBoardSize: Int = board.size
  override def getCurrentPlayer: Int = moves % players.size
  override def getPlayers: List[Player] = players

  private def getNextMovableFigure(rolledValue: Int = rolled): Int =
    players(getCurrentPlayer).figures.zipWithIndex
      .find { case (figure, _) =>
        board.canFigureMove(figure, players.flatMap(_.figures), rolledValue)
      }
      .map(_._2)
      .getOrElse(-1)
}
