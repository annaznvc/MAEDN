package de.htwg.se.MAEDN.model.statesImp

import de.htwg.se.MAEDN.model.{IManager, Board, State, IMemento, Player, Figure}
import de.htwg.se.MAEDN.util.{Event, Dice}
import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.module.Injectable

import scala.util.{Try, Success, Failure}

case class RunningState(
    override val controller: IController,
    override val moves: Int,
    override val board: Board,
    override val players: List[Player],
    override val rolled: Int = 0,
    override val selectedFigure: Int = 0,
    override val state: State = State.Running
) extends IManager
    with Injectable {

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
    val dice = inject[Dice.type]
    val newRolled = dice.roll()
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
    val currentPlayer = getCurrentPlayer
    val currentFigures = players(currentPlayer).figures
    val allFigures = players.flatMap(_.figures)
    val movableIndices = getMovableIndices(currentFigures, allFigures)

    if (movableIndices.isEmpty) {
      moveToNextPlayer()
    } else {
      val selectedIdx = selectFigureIndex(movableIndices)
      val selectedFig = currentFigures(selectedIdx)

      if (!board.canFigureMove(selectedFig, allFigures, rolled)) {
        handleInvalidSelection(movableIndices)
      } else {
        executeMove(selectedFig, allFigures, currentPlayer)
      }
    }
  }

  private def getMovableIndices(
      figures: List[Figure],
      allFigures: List[Figure]
  ): List[Int] = {
    figures.zipWithIndex.collect {
      case (figure, idx) if board.canFigureMove(figure, allFigures, rolled) =>
        idx
    }
  }

  private def selectFigureIndex(movableIndices: List[Int]): Int = {
    if (movableIndices.size == 1) movableIndices.head else selectedFigure
  }

  private def handleInvalidSelection(movableIndices: List[Int]): IManager = {
    if (movableIndices.isEmpty) moveToNextPlayer()
    else copy(selectedFigure = movableIndices.head)
  }
  private def executeMove(
      selectedFig: Figure,
      allFigures: List[Figure],
      currentPlayer: Int
  ): IManager = {
    val newFigures = board.moveFigure(selectedFig, allFigures, rolled)

    if (newFigures == allFigures) {
      copy(selectedFigure =
        getMovableIndices(players(currentPlayer).figures, allFigures).headOption
          .getOrElse(0)
      )
    } else {
      controller.enqueueEvent(Event.MoveFigureEvent(selectedFig.id))
      val updatedPlayers = updatePlayersWithNewFigures(newFigures)
      val newState = createNewState(updatedPlayers)

      // Check if player has won (all figures reached the end)
      val playerWon = checkWinCondition(updatedPlayers(currentPlayer))
      if (playerWon) {
        cleanupSaveFiles()
        controller.enqueueEvent(Event.WinEvent(currentPlayer))
        return createNewState(updatedPlayers, State.GameOver)
      }

      newState
    }
  }

  private def moveToNextPlayer(): IManager = {
    controller.enqueueEvent(
      Event.PlayNextEvent((getCurrentPlayer + 1) % players.size)
    )
    copy(rolled = 0, moves = moves + 1, selectedFigure = 0)
  }

  private def updatePlayersWithNewFigures(
      newFigures: List[Figure]
  ): List[Player] = {
    players.map { player =>
      player.copy(figures = newFigures.filter(_.owner.id == player.id))
    }
  }

  private def createNewState(
      updatedPlayers: List[Player],
      state: State = State.Running
  ): IManager = {
    copy(
      players = updatedPlayers,
      rolled = if (rolled == 6) 0 else -1,
      selectedFigure = 0,
      state = state
    )
  }

  override def quitGame(): Try[IManager] = Try {
    controller.enqueueEvent(Event.BackToMenuEvent)
    MenuState(controller, moves, board, players)
  }

  override def createMemento: Option[IMemento] =
    Some(
      IMemento(
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

  /** Check if a player has won the game (all figures have reached the end) */
  private def checkWinCondition(player: Player): Boolean = {
    // Assuming figures with index >= board.size * 4 have reached the end
    // This logic may need to be adjusted based on your game's specific win condition
    player.figures.forall(_.index >= board.size * 4)
  }

  /** Clean up save files when game is completed */
  private def cleanupSaveFiles(): Unit = {
    val cleanupResult = Try {
      val fileIO = inject[de.htwg.se.MAEDN.util.FileIO]
      // Delete autosave file since game is completed
      fileIO.deleteSaveFile("autosave.enc")
      fileIO.deleteSaveFile("autosave.json")
      fileIO.deleteSaveFile("autosave.xml")
    }
    // Silently continue if cleanup fails
    cleanupResult.recover { case _ => () }
  }
}
