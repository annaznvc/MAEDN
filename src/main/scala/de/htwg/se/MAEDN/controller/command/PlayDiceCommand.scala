package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.controller.Controller

case class PlayDiceCommand(controller: Controller) extends Command {
  override def execute(): Unit = {
    controller.manager = controller.manager.playDice()
    controller.notifyObservers()
  }
}
