package de.htwg.se.MAEDN.controller

import de.htwg.se.MAEDN.controller.command.Command
import de.htwg.se.MAEDN.controller.command.{RedoCommand, UndoCommand}
import de.htwg.se.MAEDN.model.Manager
import de.htwg.se.MAEDN.util.{Observable, UndoManager}

class Controller extends Observable {
  var manager: Manager = Manager(this)
  val undoManager = new UndoManager()

  def executeCommand(command: Command): Unit = {
    command match {
      case _: UndoCommand | _: RedoCommand =>
        // For Undo/Redo commands, just execute them directly without adding to the undoManager
        command.doStep()
      case _ =>
        // For normal commands, use the undoManager as before
        undoManager.doStep(command)
    }
    notifyObservers()
  }

  def undo(): Unit = {
    undoManager.undoStep()
    notifyObservers()
  }

  def redo(): Unit = {
    undoManager.redoStep()
    notifyObservers()
  }
}
