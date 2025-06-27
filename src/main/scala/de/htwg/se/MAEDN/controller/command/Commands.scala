package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.model.IManager
import de.htwg.se.MAEDN.model.State
import de.htwg.se.MAEDN.util.{Event, FileIO, FileFormat}
import de.htwg.se.MAEDN.model.gameDataImp.GameData

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

case class QuitGameCommand(controller: IController, fileIO: FileIO)
    extends Command {
  override def execute(): Try[IManager] = {
    controller.manager.state match {
      case State.Running =>
        // Auto-save if in running state
        controller.manager.createMemento.foreach { memento =>
          val autoSaveResult = Try {
            memento match {
              case gameData: GameData =>
                fileIO.save(
                  gameData,
                  "autosave",
                  FileFormat.JSON,
                  encrypt = true
                ) match {
                  case Success(_) => println("Game auto-saved before quitting")
                  case Failure(_) => println("Failed to auto-save game")
                }
              case _ => // Do nothing for other memento types
            }
          }
          // Silently continue if auto-save fails
          autoSaveResult.recover { case _ => () }
        }
        controller.manager.quitGame()
      case _ => controller.manager.quitGame()
    }
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

/** Command to continue from the most recent save file.
  *
  * This command looks for the most recent save file, loads it, and transitions
  * from menu state directly to running state.
  */
case class ContinueGameCommand(
    controller: IController,
    fileIO: FileIO
) extends Command {
  override def execute(): Try[IManager] = {
    fileIO.listSaveFiles() match {
      case Success(files) if files.nonEmpty =>
        // Get the most recent save file (assuming files are sorted)
        val latestSave = files.head
          .stripSuffix(".enc")
          .stripSuffix(".json")
          .stripSuffix(".xml")
        fileIO.load(latestSave, GameData) match {
          case Success(gameData) =>
            gameData.restoreManager(controller) match {
              case Success(restoredManager) =>
                controller.enqueueEvent(Event.StartGameEvent)
                controller.manager = restoredManager
                Success(restoredManager)
              case Failure(exception) =>
                Failure(
                  new RuntimeException(
                    s"Failed to restore game state: ${exception.getMessage}",
                    exception
                  )
                )
            }
          case Failure(exception) =>
            Failure(
              new RuntimeException(
                s"Failed to load game from file: ${exception.getMessage}",
                exception
              )
            )
        }
      case Success(_) =>
        Failure(new RuntimeException("No save files found to continue from"))
      case Failure(exception) =>
        Failure(
          new RuntimeException(
            s"Failed to check save files: ${exception.getMessage}",
            exception
          )
        )
    }
  }

  override def isNormal: Boolean =
    false // Loading doesn't affect undo/redo stack
}
