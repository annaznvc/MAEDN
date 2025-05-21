package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.Manager
import de.htwg.se.MAEDN.model.State
import de.htwg.se.MAEDN.util.Event

import scala.util.{Try, Success, Failure}

case class DecreaseBoardSizeCommand(controller: Controller) extends Command {
  override def execute(): Try[Manager] = {
    controller.manager.decreaseBoardSize()
  }
}

case class DecreaseFiguresCommand(controller: Controller) extends Command {
  override def execute(): Try[Manager] = {
    controller.manager.decreaseFigures()
  }
}

case class IncreaseBoardSizeCommand(controller: Controller) extends Command {
  override def execute(): Try[Manager] = {
    controller.manager.increaseBoardSize()
  }
}

case class IncreaseFiguresCommand(controller: Controller) extends Command {
  override def execute(): Try[Manager] = {
    controller.manager.increaseFigures()
  }
}

case class MoveDownCommand(controller: Controller) extends Command {
  override def execute(): Try[Manager] = {
    controller.manager.moveDown()
  }
}

case class MoveUpCommand(controller: Controller) extends Command {
  override def execute(): Try[Manager] = {
    controller.manager.moveUp()
  }
}

case class PlayNextCommand(controller: Controller) extends Command {
  override def execute(): Try[Manager] = {
    controller.manager.playNext()
  }
}

case class QuitGameCommand(controller: Controller) extends Command {
  override def execute(): Try[Manager] = {
    controller.manager.quitGame()
  }
}

case class StartGameCommand(controller: Controller) extends Command {
  override def execute(): Try[Manager] = {
    controller.manager.startGame()
  }
}

class UndoCommand(controller: Controller) extends Command {
  override def isNormal: Boolean = false
  override def execute(): Try[Manager] = {
    controller.undoStack.headOption match {
      case Some(memento) =>
        controller.undoStack.pop()
        memento.restoreManager(controller) match {
          case Success(manager) =>
            manager.createMemento.foreach(controller.redoStack.push)
            controller.manager = manager
            controller.eventQueue.enqueue(Event.UndoEvent)
            Success(manager)
          case Failure(ex) =>
            Failure(ex)
        }
      case None =>
        controller.eventQueue.enqueue(Event.ErrorEvent("No undo available"))
        Success(controller.manager)
    }
  }
}

case class RedoCommand(controller: Controller) extends Command {
  override def isNormal: Boolean = false
  override def execute(): Try[Manager] = {
    controller.redoStack.headOption match {
      case Some(memento) =>
        controller.redoStack.pop()
        memento.restoreManager(controller) match {
          case Success(manager) =>
            manager.createMemento.foreach(controller.undoStack.push)
            controller.manager = manager
            controller.eventQueue.enqueue(Event.RedoEvent)
            Success(manager)
          case Failure(ex) =>
            Failure(ex)
        }
      case None =>
        controller.eventQueue.enqueue(Event.ErrorEvent("No redo available"))
        Success(controller.manager)
    }
  }
}
