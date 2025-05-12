package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.State

case class MoveFigureCommand(controller: Controller) extends Command {
  override def execute(): Unit = {
    if (controller.manager.state == State.Running) {
      controller.manager = controller.manager.moveFigure()
      controller.notifyObservers()
    }
  }
}
