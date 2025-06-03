package de.htwg.se.MAEDN.controller

import scala.collection.mutable.Stack
import scala.util.{Success, Failure}

import de.htwg.se.MAEDN.controller.command.{Command, UndoCommand, RedoCommand}
import de.htwg.se.MAEDN.model.{Manager, IMemento}
import de.htwg.se.MAEDN.util._
import de.htwg.se.MAEDN.model.IManager
import scala.collection.mutable.PriorityQueue
import de.htwg.se.MAEDN.util.Event

class Controller extends Observable with IController {
  var manager: IManager = Manager(this)

  val undoStack = Stack[IMemento]()
  val redoStack = Stack[IMemento]()

  override def executeCommand(command: Command): Unit = {
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

  override def enqueueEvent(event: Event): Unit = {
    super.enqueueEvent(event)
  }
}
