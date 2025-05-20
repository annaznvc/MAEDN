package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util.Event
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.GameData

case class ConfigState(
    override val controller: Controller,
    override val data: GameData
) extends Manager {

  override val state: State = State.Config

  override def setGameData(newData: GameData): Manager =
    this.copy(data = newData)

  override def getGameData: GameData = data

  override def startGame(): Manager = {
    controller.eventQueue.enqueue(Event.StartGameEvent)
    RunningState(controller, data)
  }

  override def quitGame(): Manager = {
    controller.eventQueue.enqueue(Event.BackToMenuEvent)
    MenuState(controller, data)
  }

  override def increaseBoardSize(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    copy(data = data.copy(board = Board(Math.min(12, data.board.size + 1))))
  }

  override def decreaseBoardSize(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    copy(data = data.copy(board = Board(Math.max(8, data.board.size - 1))))
  }

  override def increaseFigures(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newCount = Math.min(data.board.size, data.players.head.figures.size + 1)
    val newPlayers = data.players.map(p =>
      p.copy(figures = List.fill(newCount)(p.figures.head))
    )
    copy(data = data.copy(players = newPlayers))
  }

  override def decreaseFigures(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newCount = Math.max(1, data.players.head.figures.size - 1)
    val newPlayers = data.players.map(p =>
      p.copy(figures = List.fill(newCount)(p.figures.head))
    )
    copy(data = data.copy(players = newPlayers))
  }

  override def moveUp(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newCount = Math.min(4, data.players.size + 1)
    val newPlayers =
      PlayerFactory.createPlayers(newCount, data.players.head.figures.size)
    copy(data = data.copy(players = newPlayers))
  }

  override def moveDown(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    val newCount = Math.max(2, data.players.size - 1)
    val newPlayers = data.players.take(newCount)
    copy(data = data.copy(players = newPlayers))
  }
}
