package de.htwg.se.MAEDN.controller.command

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.{Manager, IMemento}
import de.htwg.se.MAEDN.util.Event
import de.htwg.se.MAEDN.model.State

class CommandsSpec extends AnyWordSpec with Matchers {

  "All Command classes" should {

    "execute Increase/Decrease/Move commands without error" in {
      val controller = new Controller()
      controller.manager = new DummyManager()

      IncreaseBoardSizeCommand(controller).execute() shouldBe a[Manager]
      DecreaseBoardSizeCommand(controller).execute() shouldBe a[Manager]
      IncreaseFiguresCommand(controller).execute() shouldBe a[Manager]
      DecreaseFiguresCommand(controller).execute() shouldBe a[Manager]
      MoveUpCommand(controller).execute() shouldBe a[Manager]
      MoveDownCommand(controller).execute() shouldBe a[Manager]
      PlayNextCommand(controller).execute() shouldBe a[Manager]
      QuitGameCommand(controller).execute() shouldBe a[Manager]
      StartGameCommand(controller).execute() shouldBe a[Manager]
    }

    "handle UndoCommand when undoStack is not empty" in {
      val controller = new Controller()
      val manager = new DummyManager()
      controller.manager = manager
      controller.undoStack.push(manager)
      val undo = new UndoCommand(controller)

      undo.execute() shouldBe a[Manager]
      controller.redoStack should have size 1
    }

    "handle RedoCommand when redoStack is not empty" in {
      val controller = new Controller()
      val manager = new DummyManager()
      controller.manager = manager
      controller.redoStack.push(manager)
      val redo = RedoCommand(controller)

      redo.execute() shouldBe a[Manager]
      controller.undoStack should have size 1
    }

    "handle UndoCommand when undoStack is empty (coverage)" in {
      val controller = new Controller()
      val manager = new DummyManager()
      controller.manager = manager
      val undo = new UndoCommand(controller)

      undo.execute() shouldBe a[
        Manager
      ] // sollte einfach das aktuelle Manager-Objekt zurückgeben
      controller.redoStack shouldBe empty // nichts wurde gepusht
    }

    "handle RedoCommand when redoStack is empty (coverage)" in {
      val controller = new Controller()
      val manager = new DummyManager()
      controller.manager = manager
      val redo = RedoCommand(controller)

      redo.execute() shouldBe a[Manager]
      controller.undoStack shouldBe empty
    }

  }
}

// DummyManager with all methods
class DummyManager extends Manager with IMemento {
  override val controller = null
  override val rolled = 0
  override val state = State.Menu
  override def getSnapshot: Manager = this

  override def increaseBoardSize(): Manager = this
  override def decreaseBoardSize(): Manager = this
  override def increaseFigures(): Manager = this
  override def decreaseFigures(): Manager = this
  override def moveUp(): Manager = this
  override def moveDown(): Manager = this
  override def playNext(): Manager = this
  override def quitGame(): Manager = this
  override def startGame(): Manager = this
}
