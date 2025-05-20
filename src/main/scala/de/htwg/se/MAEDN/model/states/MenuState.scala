package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util.Event
import de.htwg.se.MAEDN.model.GameData

case class MenuState(
    override val controller: Controller,
    override val data: GameData
) extends Manager {
  override def getGameData: GameData = data

  override val state: State = State.Menu

  override def startGame(): Manager = {
    controller.eventQueue.enqueue(Event.ConfigEvent)
    ConfigState(controller, data)
  }

  override def quitGame(): Manager = {
    controller.eventQueue.enqueue(Event.QuitGameEvent)
    this
  }

  override def setGameData(newData: GameData): Manager =
    this.copy(data = newData)
}
