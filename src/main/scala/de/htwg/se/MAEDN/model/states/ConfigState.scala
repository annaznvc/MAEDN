package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.model.{Manager, Board, Player, State, Figure}
import de.htwg.se.MAEDN.util.Event
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.util.Color

case class ConfigState(
    override val controller: Controller,
    override val moves: Int,
    override val board: Board,
    override val players: List[Player],
    override val rolled: Int = 0
) extends Manager {

  override val state: State = State.Config

  override def startGame(): Manager = {
    controller.eventQueue.enqueue(Event.StartGameEvent)
    RunningState(
      controller,
      moves,
      board,
      players,
      0,
      0,
      true
    )
  }

  override def quitGame(): Manager = {
    controller.eventQueue.enqueue(Event.BackToMenuEvent)
    MenuState(controller, moves, board, players)
  }

  override def increaseBoardSize(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    copy(board = Board(Math.min(12, getBoardSize + 1)))
  }

  override def decreaseBoardSize(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    copy(board = Board(Math.max(8, getBoardSize - 1)))
  }

  override def increaseFigures(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newFigureCount = Math.min(board.size, players.head.figures.size)
    val newPlayers = players.map(player =>
      player.copy(figures = List.fill(newFigureCount)(player.figures.head))
    )
    copy(players = newPlayers)
  }

  override def decreaseFigures(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newFigureCount = Math.max(1, players.head.figures.size - 1)
    val newPlayers = players.map(player =>
      player.copy(figures = List.fill(newFigureCount)(player.figures.head))
    )
    copy(players = newPlayers)
  }

  override def moveUp(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newPlayerCount = Math.min(4, players.size + 1)
    val newPlayers = Color.values.zipWithIndex.map { case (color, index) =>
      val placeholder = Player(index + 1, List.empty, color)
      val figures = (1 to players.head.figures.size)
        .map(i => Figure(i, placeholder, -1))
        .toList
      placeholder.copy(figures = figures)
    }
    copy(players = newPlayers)
  }

  override def moveDown(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newPlayerCount = Math.max(2, players.size - 1)
    val newPlayers = players.take(newPlayerCount)
    copy(players = newPlayers)
  }
}
