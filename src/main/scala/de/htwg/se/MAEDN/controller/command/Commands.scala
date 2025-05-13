package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.Manager
import de.htwg.se.MAEDN.model.State
import de.htwg.se.MAEDN.util.Event

case class DecreaseBoardSizeCommand(controller: Controller) extends Command {
  override def execute(): Manager = {
    controller.manager = controller.manager.decreaseBoardSize()
    controller.manager
  }
}

case class DecreaseFiguresCommand(controller: Controller) extends Command {
  override def execute(): Manager = {
    controller.manager = controller.manager.decreaseFigures()
    controller.manager
  }
}

case class IncreaseBoardSizeCommand(controller: Controller) extends Command {
  override def execute(): Manager = {
    controller.manager = controller.manager.increaseBoardSize()
    controller.manager
  }
}

case class IncreaseFiguresCommand(controller: Controller) extends Command {
  override def execute(): Manager = {
    controller.manager = controller.manager.increaseFigures()
    controller.manager
  }
}

case class MoveDownCommand(controller: Controller) extends Command {
  override def execute(): Manager = {
    controller.manager = controller.manager.moveDown()
    controller.manager
  }
}

case class MoveUpCommand(controller: Controller) extends Command {
  override def execute(): Manager = {
    controller.manager = controller.manager.moveDown()
    controller.manager
  }
}

case class MoveFigureCommand(controller: Controller) extends Command {
  override def execute(): Manager = {
    controller.manager = controller.manager.moveFigure()
    controller.manager
  }
}

case class PlayDiceCommand(controller: Controller) extends Command {
  override def execute(): Manager = {
    controller.manager = controller.manager.playDice()
    controller.manager
  }
}

case class PlayNextCommand(controller: Controller) extends Command {
  override def execute(): Manager = {
    controller.manager = controller.manager.playNext()
    controller.manager
  }
}

case class QuitGameCommand(controller: Controller) extends Command {
  override def execute(): Manager = {
    controller.manager = controller.manager.quitGame()
    controller.manager
  }
}

case class StartGameCommand(controller: Controller) extends Command {
  override def execute(): Manager = {
    controller.manager = controller.manager.startGame()
    controller.manager
  }
}

class UndoCommand(controller: Controller) extends Command {
  override def execute(): Manager = {
    if controller.undoStack.nonEmpty then {
      controller.manager = controller.undoStack.pop().getSnapshot
      controller.redoStack.push(controller.manager.getSnapshot)
    }
    controller.eventQueue.enqueue(Event.UndoEvent)
    controller.manager
  }
}

case class RedoCommand(controller: Controller) extends Command {
  override def execute(): Manager = {
    if controller.redoStack.nonEmpty then {
      controller.undoStack.push(controller.manager.getSnapshot)
      controller.manager = controller.redoStack.pop().getSnapshot
    }
    controller.eventQueue.enqueue(Event.RedoEvent)
    controller.manager
  }
}
