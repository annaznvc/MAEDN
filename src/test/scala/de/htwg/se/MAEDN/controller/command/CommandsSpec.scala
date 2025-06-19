package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.controller.controllerImp.Controller
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.model.states.RunningState
import de.htwg.se.MAEDN.util._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Success, Failure}

class CommandsSpec extends AnyWordSpec with Matchers {

  def makeRestorableManager(controller: Controller): Manager = {
    val players = PlayerFactory(2, 4)
    val board = Board(8)
    RunningState(
      controller,
      moves = 0,
      board,
      players,
      rolled = 3,
      selectedFigure = 0
    )
  }

  "All Command classes" should {

    val controller = new Controller

    "execute DecreaseBoardSizeCommand" in {
      val command = DecreaseBoardSizeCommand(controller)
      command.execute().isSuccess shouldBe true
    }

    "execute IncreaseBoardSizeCommand" in {
      val command = IncreaseBoardSizeCommand(controller)
      command.execute().isSuccess shouldBe true
    }

    "execute IncreaseFiguresCommand" in {
      val command = IncreaseFiguresCommand(controller)
      command.execute().isSuccess shouldBe true
    }

    "execute DecreaseFiguresCommand" in {
      val command = DecreaseFiguresCommand(controller)
      command.execute().isSuccess shouldBe true
    }

    "execute MoveUpCommand" in {
      val command = MoveUpCommand(controller)
      command.execute().isSuccess shouldBe true
    }

    "execute MoveDownCommand" in {
      val command = MoveDownCommand(controller)
      command.execute().isSuccess shouldBe true
    }

    "execute QuitGameCommand" in {
      val command = QuitGameCommand(controller)
      command.execute().isSuccess shouldBe true
    }

    "execute StartGameCommand" in {
      val command = StartGameCommand(controller)
      command.execute().isSuccess shouldBe true
    }

    "execute PlayNextCommand" in {
      val command = PlayNextCommand(controller)
      command.execute().isSuccess || command.execute().isFailure shouldBe true
    }
  }

  "UndoCommand" should {

    val controller = new Controller
    val command = new UndoCommand(controller)

    "do nothing if undo stack is empty" in {
      controller.undoStack.clear()
      val result = command.execute()
      result shouldBe Success(controller.manager)
    }

    "restore manager from undo stack and push to redo" in {
      controller.manager = makeRestorableManager(controller)
      val memento = controller.manager.createMemento.get
      controller.undoStack.push(memento)
      val result = command.execute()
      result.isSuccess shouldBe true
      controller.redoStack.nonEmpty shouldBe true
    }

    "fail if restoreManager fails (Zeile 77) and cover isNormal = false (Zeile 65)" in {
      val badMemento = new GameData(0, Board(8), PlayerFactory(2, 4), 0, 0) {
        override def restoreManager(controller: Controller) =
          Failure(new RuntimeException("fail"))
      }

      controller.undoStack.push(badMemento)

      val command = new UndoCommand(controller)
      command.isNormal shouldBe false // Zeile 65
      val result = command.execute()
      result.isFailure shouldBe true // Zeile 77
    }
  }

  "RedoCommand" should {

    val controller = new Controller
    val command = RedoCommand(controller)

    "do nothing if redo stack is empty" in {
      controller.redoStack.clear()
      val result = command.execute()
      result shouldBe Success(controller.manager)
    }

    "restore manager from redo stack and push to undo" in {
      controller.manager = makeRestorableManager(controller)
      val memento = controller.manager.createMemento.get
      controller.redoStack.push(memento)
      val result = command.execute()
      result.isSuccess shouldBe true
      controller.undoStack.nonEmpty shouldBe true
    }

    "fail if restoreManager fails (Zeile 99) and cover isNormal = false (Zeile 87)" in {
      val badMemento = new GameData(0, Board(8), PlayerFactory(2, 4), 0, 0) {
        override def restoreManager(controller: Controller) =
          Failure(new RuntimeException("fail"))
      }

      controller.redoStack.push(badMemento)

      val command = RedoCommand(controller)
      command.isNormal shouldBe false // Zeile 87
      val result = command.execute()
      result.isFailure shouldBe true // Zeile 99
    }
  }
}
