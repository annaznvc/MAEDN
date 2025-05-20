package de.htwg.se.MAEDN.controller

import de.htwg.se.MAEDN.controller.command.Command
import de.htwg.se.MAEDN.model.Manager
import de.htwg.se.MAEDN.util.{Observable, UndoManager}

class Controller extends Observable {
  var manager: Manager = Manager(this)
  val undoManager = new UndoManager()

  def executeCommand(command: Command): Unit = {
    undoManager.doStep(command)
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
