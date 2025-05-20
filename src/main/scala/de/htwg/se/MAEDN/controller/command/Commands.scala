package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.GameData
import de.htwg.se.MAEDN.util.Event

// ========== COMMON COMMAND BASE ==========

abstract class BaseCommand(controller: Controller) extends Command {
  protected var before: Option[GameData] = None
  protected var after: Option[GameData] = None
  protected var event: Option[Event] = None

  override def undoStep(): Unit = {
    before.foreach(data =>
      controller.manager = controller.manager.setGameData(data)
    )
    event.foreach(controller.eventQueue.enqueue)
  }

  override def redoStep(): Unit = {
    after.foreach(data =>
      controller.manager = controller.manager.setGameData(data)
    )
    event.foreach(controller.eventQueue.enqueue)
  }
}

// ========== INDIVIDUAL COMMANDS ==========

class DecreaseBoardSizeCommand(controller: Controller)
    extends BaseCommand(controller) {
  override def doStep(): Unit = {
    before = Some(controller.manager.getGameData)
    controller.manager = controller.manager.decreaseBoardSize()
    after = Some(controller.manager.getGameData)
  }
}

class DecreaseFiguresCommand(controller: Controller)
    extends BaseCommand(controller) {
  override def doStep(): Unit = {
    before = Some(controller.manager.getGameData)
    controller.manager = controller.manager.decreaseFigures()
    after = Some(controller.manager.getGameData)
  }
}

class IncreaseBoardSizeCommand(controller: Controller)
    extends BaseCommand(controller) {
  override def doStep(): Unit = {
    before = Some(controller.manager.getGameData)
    controller.manager = controller.manager.increaseBoardSize()
    after = Some(controller.manager.getGameData)
  }
}

class IncreaseFiguresCommand(controller: Controller)
    extends BaseCommand(controller) {
  override def doStep(): Unit = {
    before = Some(controller.manager.getGameData)
    controller.manager = controller.manager.increaseFigures()
    after = Some(controller.manager.getGameData)
  }
}

class MoveDownCommand(controller: Controller) extends BaseCommand(controller) {
  override def doStep(): Unit = {
    before = Some(controller.manager.getGameData)
    controller.manager = controller.manager.moveDown()
    after = Some(controller.manager.getGameData)
  }
}

class MoveUpCommand(controller: Controller) extends BaseCommand(controller) {
  override def doStep(): Unit = {
    before = Some(controller.manager.getGameData)
    controller.manager = controller.manager.moveUp()
    after = Some(controller.manager.getGameData)
  }
}

class PlayNextCommand(controller: Controller) extends BaseCommand(controller) {
  override def doStep(): Unit = {
    before = Some(controller.manager.getGameData)
    controller.manager = controller.manager.playNext()
    after = Some(controller.manager.getGameData)
  }
}

class QuitGameCommand(controller: Controller) extends BaseCommand(controller) {
  override def doStep(): Unit = {
    before = Some(controller.manager.getGameData)
    controller.manager = controller.manager.quitGame()
    after = Some(controller.manager.getGameData)
  }
}

class StartGameCommand(controller: Controller) extends BaseCommand(controller) {
  override def doStep(): Unit = {
    before = Some(controller.manager.getGameData)
    controller.manager = controller.manager.startGame()
    after = Some(controller.manager.getGameData)
  }
}

// ========== UNDO / REDO COMMANDS (SEPARAT, OPTIONAL) ==========
// Hinweis: Mit neuem UndoManager werden diese Commands evtl. nicht mehr gebraucht!

class UndoCommand(controller: Controller) extends Command {
  override def doStep(): Unit = {
    controller.undo()
    controller.eventQueue.enqueue(
      Event.MoveFigureEvent(-1)
    ) // oder passender Event
  }
  override def undoStep(): Unit = ()
  override def redoStep(): Unit = ()
}

class RedoCommand(controller: Controller) extends Command {
  override def doStep(): Unit = {
    controller.redo()
    controller.eventQueue.enqueue(
      Event.MoveFigureEvent(-1)
    ) // oder passender Event
  }
  override def undoStep(): Unit = ()
  override def redoStep(): Unit = ()
}
