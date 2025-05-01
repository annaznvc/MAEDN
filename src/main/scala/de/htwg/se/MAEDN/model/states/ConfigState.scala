package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.model.{Manager, Board, Player, BoardFactory, State}
import de.htwg.se.MAEDN.util.Event
import de.htwg.se.MAEDN.controller.Controller

case class ConfigState(
    override val controller: Controller,
    override val moves: Int,
    override val board: Board,
    override val players: List[Player],
    override val rolled: Int = 0,
    val boardFactory: BoardFactory = BoardFactory()
) extends Manager {

  override def getBoardSize: Int = boardFactory.normalFieldCount
  override val state: State = State.Config
  override def startGame(): Manager = {
    controller.eventQueue.enqueue(Event.StartGameEvent)
    RunningState(
      controller,
      moves,
      boardFactory.withPlayers(players).build(),
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
    copy(boardFactory = boardFactory.withBoardSize(getBoardSize + 1))
  }
  override def decreaseBoardSize(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    copy(boardFactory = boardFactory.withBoardSize(getBoardSize - 1))
  }

  override def increaseFigures(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newFigureCount = Math.max(1, players.head.figures.size + 1)
    val newPlayers = players.map(player =>
      player.copy(figures = List.fill(newFigureCount)(player.figures.head))
    )
    val newFactory = boardFactory.withFigureCount(newFigureCount)
    copy(players = newPlayers, boardFactory = newFactory)
  }

  override def decreaseFigures(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newFigureCount = Math.max(1, players.head.figures.size - 1)
    val newPlayers = players.map(player =>
      player.copy(figures = List.fill(newFigureCount)(player.figures.head))
    )
    val newFactory = boardFactory.withFigureCount(newFigureCount)
    copy(players = newPlayers, boardFactory = newFactory)
  }

  override def moveUp(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newFactory = boardFactory.withPlayers(players)
    if (players.size < 4) {
      copy(
        players = players :+ Player(
          players.size + 1,
          List.fill(players.head.figures.size)(players.head.figures.head),
          players.head.color
        ),
        boardFactory = newFactory
      )
    } else {
      this
    }
  }

  override def moveDown(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newFactory = boardFactory.withPlayers(players)
    if (players.size > 2) {
      copy(players = players.dropRight(1), boardFactory = newFactory)
    } else {
      this
    }
  }
}
