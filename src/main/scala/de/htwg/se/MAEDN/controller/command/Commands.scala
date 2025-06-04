package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.model.IManager
import de.htwg.se.MAEDN.model.State
import de.htwg.se.MAEDN.util.Event

import scala.util.{Try, Success, Failure}

case class DecreaseBoardSizeCommand(controller: IController) extends Command {
  override def execute(): Try[IManager] = {
    controller.manager.decreaseBoardSize()
  }
}

case class DecreaseFiguresCommand(controller: IController) extends Command {
  override def execute(): Try[IManager] = {
    controller.manager.decreaseFigures()
  }
}

case class IncreaseBoardSizeCommand(controller: IController) extends Command {
  override def execute(): Try[IManager] = {
    controller.manager.increaseBoardSize()
  }
}

case class IncreaseFiguresCommand(controller: IController) extends Command {
  override def execute(): Try[IManager] = {
    controller.manager.increaseFigures()
  }
}

case class MoveDownCommand(controller: IController) extends Command {
  override def execute(): Try[IManager] = {
    controller.manager.moveDown()
  }
}

case class MoveUpCommand(controller: IController) extends Command {
  override def execute(): Try[IManager] = {
    controller.manager.moveUp()
  }
}

case class PlayNextCommand(controller: IController) extends Command {
  override def execute(): Try[IManager] = {
    controller.manager.playNext()
  }
}

case class QuitGameCommand(controller: IController) extends Command {
  override def execute(): Try[IManager] = {
    controller.manager.quitGame()
  }
}

case class StartGameCommand(controller: IController) extends Command {
  override def execute(): Try[IManager] = {
    controller.manager.startGame()
  }
}

class UndoCommand(controller: IController) extends Command {
  override def isNormal: Boolean = false
  override def execute(): Try[IManager] = {
    controller.undoStack.headOption match {
      case Some(memento) =>
        controller.undoStack.pop()
        memento.restoreIManager(controller) match {
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

case class RedoCommand(controller: IController) extends Command {
  override def isNormal: Boolean = false
  override def execute(): Try[IManager] = {
    controller.redoStack.headOption match {
      case Some(memento) =>
        controller.redoStack.pop()
        memento.restoreIManager(controller) match {
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
