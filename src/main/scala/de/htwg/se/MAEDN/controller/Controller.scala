package de.htwg.se.MAEDN.controller

import scala.collection.mutable.Stack
import scala.util.{Success, Failure}

import de.htwg.se.MAEDN.controller.command.{Command, UndoCommand, RedoCommand}
import de.htwg.se.MAEDN.model.{Manager, IMemento}
import de.htwg.se.MAEDN.util._

class Controller extends Observable {
  var manager: Manager = Manager(
    this
  ) // Manager Instanz erzeugt, controller als paramter damit der manager events erzeugen kann

  val undoStack = Stack[IMemento]()
  val redoStack = Stack[IMemento]()

  def executeCommand(command: Command): Unit = {
    if (command.isNormal) {
      manager.createMemento.foreach { case memento: IMemento =>
        undoStack.push(memento)
        redoStack.clear()
      }
    }

    command.execute() match {
      case Failure(exception) =>
        eventQueue.enqueue(Event.ErrorEvent(exception.getMessage))
      case Success(newManager) =>
        manager = newManager
    }
    notifyObservers()
  }
}
