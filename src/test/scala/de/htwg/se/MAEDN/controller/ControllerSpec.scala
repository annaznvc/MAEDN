package de.htwg.se.MAEDN.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.controller.command.{Command, UndoCommand, RedoCommand}
import de.htwg.se.MAEDN.model.{Manager, IMemento}
import de.htwg.se.MAEDN.util.{Event, Observer}
import de.htwg.se.MAEDN.model.State

class ControllerSpec extends AnyWordSpec with Matchers {

  "A Controller" should {

    "initialize with a Manager and empty undo/redo stacks" in {
      val controller = new Controller()
      controller.manager = new DummyManager()
      assert(controller.manager != null)
      controller.undoStack shouldBe empty
      controller.redoStack shouldBe empty
    }

    "execute a normal command and push to undoStack" in {
      val controller = new Controller()
      controller.manager = new DummyManager()
      val command = new TestCommand(controller)

      controller.executeCommand(command)

      command.wasExecuted shouldBe true
      controller.undoStack should have size 1
    }

    "not push UndoCommand to undoStack" in {
      val controller = new Controller()
      controller.manager = new DummyManager()
      val command = new TestUndoCommand(controller)

      controller.executeCommand(command)

      command.wasExecuted shouldBe true
      controller.undoStack shouldBe empty
    }

    "not push RedoCommand to undoStack" in {
      val controller = new Controller()
      controller.manager = new DummyManager()
      val command = new TestRedoCommand(controller)

      controller.executeCommand(command)

      command.wasExecuted shouldBe true
      controller.undoStack shouldBe empty
    }

    "notify observers after executing command" in {
      val controller = new Controller()
      controller.manager = new DummyManager()
      var wasNotified = false

      controller.add(new Observer {
        override def processEvent(event: Event): Unit = wasNotified = true
      })

      controller.executeCommand(new TestCommand(controller))

      wasNotified shouldBe true
    }
  }
}

// ✅ DummyManager – sicher, keine Player/Figure-Referenzen
class DummyManager extends Manager with IMemento {
  override val controller = null
  override val rolled = 0
  override val state = State.Menu
  override def getSnapshot: Manager = this
}

// ✅ DummyCommand – triggert Event, damit Observer reagiert
class TestCommand(controller: Controller) extends Command {
  var wasExecuted = false
  override def execute(): Manager = {
    wasExecuted = true
    controller.eventQueue.enqueue(Event.UndoEvent)
    controller.manager
  }
}

// ✅ Richtiger Typ für UndoCommand
class TestUndoCommand(controller: Controller) extends UndoCommand(controller) {
  var wasExecuted = false
  override def execute(): Manager = {
    wasExecuted = true
    controller.manager
  }
}

// ✅ Richtiger Typ für RedoCommand
class TestRedoCommand(controller: Controller) extends RedoCommand(controller) {
  var wasExecuted = false
  override def execute(): Manager = {
    wasExecuted = true
    controller.manager
  }
}
