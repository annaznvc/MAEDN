package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util.{Event, Color}
import de.htwg.se.MAEDN.controller.Controller

import scala.util.Try

case class ConfigState(
    override val controller: Controller,
    override val moves: Int,
    override val board: Board,
    override val players: List[Player],
    override val rolled: Int = 0
) extends Manager {

  override val state: State = State.Config

  override def startGame(): Try[Manager] = Try {
    controller.eventQueue.enqueue(Event.StartGameEvent)
    RunningState(
      controller,
      moves,
      board,
      players,
      0,
      0
    )
  }

  override def quitGame(): Try[Manager] = Try {
    controller.eventQueue.enqueue(Event.BackToMenuEvent)
    MenuState(controller, moves, board, players)
  }

  override def increaseBoardSize(): Try[Manager] = Try {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    copy(board = Board(Math.min(12, getBoardSize + 1)))
  }

  override def decreaseBoardSize(): Try[Manager] = Try {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    copy(board = Board(Math.max(8, getBoardSize - 1)))
  }

  override def increaseFigures(): Try[Manager] = Try {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newFigureCount = Math.min(board.size, players.head.figures.size + 1)
    val newPlayers = players.map(player =>
      player.copy(figures = List.fill(newFigureCount)(player.figures.head))
    )
    copy(players = newPlayers)
  }

  override def decreaseFigures(): Try[Manager] = Try {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newFigureCount = Math.max(1, players.head.figures.size - 1)
    val newPlayers = players.map(player =>
      player.copy(figures = List.fill(newFigureCount)(player.figures.head))
    )
    copy(players = newPlayers)
  }

  override def moveUp(): Try[Manager] = Try {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newPlayerCount = Math.min(4, players.size + 1)
    val newPlayers =
      PlayerFactory(newPlayerCount, players.head.figures.size)
    copy(players = newPlayers)
  }

  override def moveDown(): Try[Manager] = Try {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newPlayerCount = Math.max(2, players.size - 1)
    val newPlayers = players.take(newPlayerCount)
    copy(players = newPlayers)
  }
}
