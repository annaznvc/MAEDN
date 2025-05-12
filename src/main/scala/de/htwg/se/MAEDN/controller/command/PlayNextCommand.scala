package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.controller.Controller

case class PlayNextCommand(controller: Controller) extends Command {
  override def execute(): Unit = {
    controller.manager = controller.manager.playNext()
    controller.notifyObservers()

  }
}
