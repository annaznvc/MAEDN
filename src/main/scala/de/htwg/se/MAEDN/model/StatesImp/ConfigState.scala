package de.htwg.se.MAEDN.model.statesImp

import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util.{Event, Color, PlayerFactory}
import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.module.Injectable

import scala.util.Try

case class ConfigState(
    override val controller: IController,
    override val moves: Int,
    override val board: Board,
    override val players: List[Player],
    override val rolled: Int = 0
) extends IManager
    with Injectable {

  override val state: State = State.Config
  override def startGame(): Try[IManager] = Try {
    controller.eventQueue.enqueue(Event.StartGameEvent)
    RunningState(
      controller,
      0,
      Board(board.size),
      PlayerFactory(players.size, players.head.figures.size),
      0,
      0
    )
  }

  override def quitGame(): Try[IManager] = Try {
    controller.eventQueue.enqueue(Event.BackToMenuEvent)
    MenuState(controller, moves, board, players)
  }

  override def increaseBoardSize(): Try[IManager] = Try {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    copy(board = Board(Math.min(12, getBoardSize + 1)))
  }

  override def decreaseBoardSize(): Try[IManager] = Try {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    copy(board = Board(Math.max(8, getBoardSize - 1)))
  }

  override def increaseFigures(): Try[IManager] = Try {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newFigureCount = Math.min(board.size, players.head.figures.size + 1)
    val newPlayers = players.map(player =>
      player.copy(figures =
        (0 until newFigureCount)
          .map(i =>
            player.figures.head
              .copy(id = i, figureCount = newFigureCount, index = -1)
          )
          .toList
      )
    )
    copy(players = newPlayers)
  }

  override def decreaseFigures(): Try[IManager] = Try {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newFigureCount = Math.max(1, players.head.figures.size - 1)
    val newPlayers = players.map(player =>
      player.copy(figures =
        player.figures
          .take(newFigureCount)
          .map(_.copy(figureCount = newFigureCount))
      )
    )
    copy(players = newPlayers)
  }

  override def moveUp(): Try[IManager] = Try {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newPlayerCount = Math.min(4, players.size + 1)
    val newPlayers =
      PlayerFactory(newPlayerCount, players.head.figures.size)
    copy(players = newPlayers)
  }

  override def moveDown(): Try[IManager] = Try {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newPlayerCount = Math.max(2, players.size - 1)
    val newPlayers = players.take(newPlayerCount)
    copy(players = newPlayers)
  }

  // Getter
  override def getPlayerCount: Int = players.size
  override def getFigureCount: Int =
    players.headOption.map(_.figures.size).getOrElse(0)
  override def getBoardSize: Int = board.size
  override def getCurrentPlayer: Int = 0
  override def getPlayers: List[Player] = players
  override def createMemento: Option[IMemento] = None
  override val selectedFigure: Int = 0

}
