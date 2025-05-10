package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.controller.Controller

case class IncreaseFiguresCommand(controller: Controller) extends Command {
  override def execute(): Unit = {
    controller.manager = controller.manager.increaseFigures()
    controller.notifyObservers()
  }
}
