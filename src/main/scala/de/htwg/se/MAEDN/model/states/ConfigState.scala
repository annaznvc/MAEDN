package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.model.{Manager, Board, Player, BoardFactory, State}
import de.htwg.se.MAEDN.util.Event
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.model.Figure

case class ConfigState(
    override val controller: Controller,
    override val moves: Int,
    override val board: Board,
    override val players: List[Player],
    override val rolled: Int = 0,
    val boardFactory: BoardFactory = BoardFactory()
) extends Manager {

  val syncedBoardFactory: BoardFactory = boardFactory
    .withFigureCount(players.headOption.map(_.figures.size).getOrElse(1))
    .withPlayers(players)

  override def getBoardSize: Int = syncedBoardFactory.normalFieldCount
  override val state: State = State.Config

  override def startGame(): Manager = {
    controller.eventQueue.enqueue(Event.StartGameEvent)
    RunningState(
      controller,
      moves,
      syncedBoardFactory.build(),
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
    val availableColors = List(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW)
      .diff(players.map(_.color))

    if (players.size < 4 && availableColors.nonEmpty) {
      val newColor = availableColors.head
      val newPlayerId = players.map(_.id).max + 1
      val newPlayer = Player(
        newPlayerId,
        (1 to players.head.figures.size)
          .map(i => Figure(i, null))
          .toList, // temp null owner
        newColor
      )
      val properPlayer = newPlayer.copy(figures =
        newPlayer.figures.map(f => f.copy(owner = newPlayer))
      )
      copy(
        players = players :+ properPlayer,
        boardFactory = newFactory.withPlayers(players :+ properPlayer)
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
