package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.controller.Controller

class UndoCommand(controller: Controller) extends Command {
  override def execute(): Unit = controller.undo()
}
