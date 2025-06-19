package de.htwg.se.MAEDN.controller.controllerImp

import scala.collection.mutable.{Stack, PriorityQueue}
import scala.util.{Success, Failure}

import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.controller.command.{Command, UndoCommand, RedoCommand}
import de.htwg.se.MAEDN.model.{IManager, IMemento, State}
import de.htwg.se.MAEDN.util._
import de.htwg.se.MAEDN.module.Injectable
import de.htwg.se.MAEDN.controller.command.QuitGameCommand

class Controller extends Observable with IController with Injectable {
  // Use dependency injection to get the manager instance

  var manager: IManager = inject[IManager]

  val undoStack = Stack[IMemento]()
  val redoStack = Stack[IMemento]()

  override def executeCommand(command: Command): Unit = {
    if (command.isNormal) {
      manager.createMemento.foreach { case memento: IMemento =>
        undoStack.push(memento)
        redoStack.clear()
      }
    }

    if (manager.state != State.GameOver) {
      command.execute() match {
        case Failure(exception) =>
          eventQueue.enqueue(Event.ErrorEvent(exception.getMessage))
        case Success(newManager) =>
          manager = newManager
      }
      notifyObservers()
    } else {
      if (command.isInstanceOf[QuitGameCommand]) {
        command.execute() match {
          case Failure(exception) =>
            eventQueue.enqueue(Event.ErrorEvent(exception.getMessage))
          case Success(newManager) =>
            manager = newManager
        }
        notifyObservers()
      }
    }
  }

  override def enqueueEvent(event: Event): Unit = {
    super.enqueueEvent(event)
  }

}
