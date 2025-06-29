package de.htwg.se.MAEDN.aview.gui

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.util.Observable.eventOrdering
import de.htwg.se.MAEDN.controller.command._
import javafx.scene.input.{KeyCode, KeyEvent}

import scala.collection.mutable.ListBuffer

class TestController extends IController {
  override def add(observer: de.htwg.se.MAEDN.util.Observer): Unit = {}
  override def remove(observer: de.htwg.se.MAEDN.util.Observer): Unit = {}
  override def enqueueEvent(event: de.htwg.se.MAEDN.util.Event): Unit = {}
  var manager: de.htwg.se.MAEDN.model.IManager = _
  override def eventQueue
      : scala.collection.mutable.PriorityQueue[de.htwg.se.MAEDN.util.Event] =
    new scala.collection.mutable.PriorityQueue[de.htwg.se.MAEDN.util.Event]()
  override def undoStack
      : scala.collection.mutable.Stack[de.htwg.se.MAEDN.model.IMemento] =
    new scala.collection.mutable.Stack[de.htwg.se.MAEDN.model.IMemento]()
  override def redoStack
      : scala.collection.mutable.Stack[de.htwg.se.MAEDN.model.IMemento] =
    new scala.collection.mutable.Stack[de.htwg.se.MAEDN.model.IMemento]()

  val executedCommands: ListBuffer[String] = ListBuffer.empty

  override def executeCommand(command: Command): Unit = {
    command match {
      case _: PlayNextCommand        => executedCommands += "PlayNext"
      case _: MoveUpCommand          => executedCommands += "MoveUp"
      case _: MoveDownCommand        => executedCommands += "MoveDown"
      case _: IncreaseFiguresCommand => executedCommands += "IncreaseFigures"
      case _: DecreaseFiguresCommand => executedCommands += "DecreaseFigures"
      case _: IncreaseBoardSizeCommand =>
        executedCommands += "IncreaseBoardSize"
      case _: DecreaseBoardSizeCommand =>
        executedCommands += "DecreaseBoardSize"
      case _: QuitGameCommand  => executedCommands += "QuitGame"
      case _: StartGameCommand => executedCommands += "StartGame"
      case _: UndoCommand      => executedCommands += "Undo"
      case _: RedoCommand      => executedCommands += "Redo"
      case _                   => executedCommands += "Unknown"
    }
  }

  def clearCommands(): Unit = executedCommands.clear()
  def getLastCommand: Option[String] = executedCommands.lastOption
  def getCommandCount: Int = executedCommands.length
}

// Helper to create KeyEvent instances
object KeyEventHelper {
  def createKeyEvent(keyCode: KeyCode): KeyEvent = {
    new KeyEvent(
      KeyEvent.KEY_PRESSED,
      "",
      "",
      keyCode,
      false,
      false,
      false,
      false
    )
  }
}

class ActionManagerSpec extends AnyWordSpec with Matchers {

  "An ActionManager" should {

    "handle onPlayNext correctly" in {
      val testController = new TestController
      val actionManager = new ActionManager(testController)

      actionManager.onPlayNext()

      testController.getLastCommand should be(Some("PlayNext"))
      testController.getCommandCount should be(1)
    }

    "handle onMoveUp correctly" in {
      val testController = new TestController
      val actionManager = new ActionManager(testController)

      actionManager.onMoveUp()

      testController.getLastCommand should be(Some("MoveUp"))
      testController.getCommandCount should be(1)
    }

    "handle onMoveDown correctly" in {
      val testController = new TestController
      val actionManager = new ActionManager(testController)

      actionManager.onMoveDown()

      testController.getLastCommand should be(Some("MoveDown"))
      testController.getCommandCount should be(1)
    }

    "handle onIncreaseFigures correctly" in {
      val testController = new TestController
      val actionManager = new ActionManager(testController)

      actionManager.onIncreaseFigures()

      testController.getLastCommand should be(Some("IncreaseFigures"))
      testController.getCommandCount should be(1)
    }

    "handle onDecreaseFigures correctly" in {
      val testController = new TestController
      val actionManager = new ActionManager(testController)

      actionManager.onDecreaseFigures()

      testController.getLastCommand should be(Some("DecreaseFigures"))
      testController.getCommandCount should be(1)
    }

    "handle onIncreaseBoardSize correctly" in {
      val testController = new TestController
      val actionManager = new ActionManager(testController)

      actionManager.onIncreaseBoardSize()

      testController.getLastCommand should be(Some("IncreaseBoardSize"))
      testController.getCommandCount should be(1)
    }

    "handle onDecreaseBoardSize correctly" in {
      val testController = new TestController
      val actionManager = new ActionManager(testController)

      actionManager.onDecreaseBoardSize()

      testController.getLastCommand should be(Some("DecreaseBoardSize"))
      testController.getCommandCount should be(1)
    }

    "handle onQuitGame correctly" in {
      val testController = new TestController
      val actionManager = new ActionManager(testController)

      actionManager.onQuitGame()

      testController.getLastCommand should be(Some("QuitGame"))
      testController.getCommandCount should be(1)
    }

    "handle onStartGame correctly" in {
      val testController = new TestController
      val actionManager = new ActionManager(testController)

      actionManager.onStartGame()

      testController.getLastCommand should be(Some("StartGame"))
      testController.getCommandCount should be(1)
    }

    "handle onUndo correctly" in {
      val testController = new TestController
      val actionManager = new ActionManager(testController)

      actionManager.onUndo()

      testController.getLastCommand should be(Some("Undo"))
      testController.getCommandCount should be(1)
    }

    "handle onRedo correctly" in {
      val testController = new TestController
      val actionManager = new ActionManager(testController)

      actionManager.onRedo()

      testController.getLastCommand should be(Some("Redo"))
      testController.getCommandCount should be(1)
    }

    "handle onBackToMenu correctly" in {
      val testController = new TestController
      val actionManager = new ActionManager(testController)

      actionManager.onBackToMenu()

      testController.getLastCommand should be(Some("QuitGame"))
      testController.getCommandCount should be(1)
    }

    "handle onOpenConfiguration correctly" in {
      val testController = new TestController
      val actionManager = new ActionManager(testController)

      actionManager.onOpenConfiguration()

      testController.getLastCommand should be(Some("StartGame"))
      testController.getCommandCount should be(1)
    }

    "handle keyboard shortcuts correctly" when {

      "X key is pressed (PlayNext)" in {
        val testController = new TestController
        val actionManager = new ActionManager(testController)
        val keyEvent = KeyEventHelper.createKeyEvent(KeyCode.X)

        actionManager.handleKeyEvent(keyEvent)

        testController.getLastCommand should be(Some("PlayNext"))
        testController.getCommandCount should be(1)
      }

      "W key is pressed (MoveUp)" in {
        val testController = new TestController
        val actionManager = new ActionManager(testController)
        val keyEvent = KeyEventHelper.createKeyEvent(KeyCode.W)

        actionManager.handleKeyEvent(keyEvent)

        testController.getLastCommand should be(Some("MoveUp"))
        testController.getCommandCount should be(1)
      }

      "S key is pressed (MoveDown)" in {
        val testController = new TestController
        val actionManager = new ActionManager(testController)
        val keyEvent = KeyEventHelper.createKeyEvent(KeyCode.S)

        actionManager.handleKeyEvent(keyEvent)

        testController.getLastCommand should be(Some("MoveDown"))
        testController.getCommandCount should be(1)
      }

      "E key is pressed (IncreaseFigures)" in {
        val testController = new TestController
        val actionManager = new ActionManager(testController)
        val keyEvent = KeyEventHelper.createKeyEvent(KeyCode.E)

        actionManager.handleKeyEvent(keyEvent)

        testController.getLastCommand should be(Some("IncreaseFigures"))
        testController.getCommandCount should be(1)
      }

      "D key is pressed (DecreaseFigures)" in {
        val testController = new TestController
        val actionManager = new ActionManager(testController)
        val keyEvent = KeyEventHelper.createKeyEvent(KeyCode.D)

        actionManager.handleKeyEvent(keyEvent)

        testController.getLastCommand should be(Some("DecreaseFigures"))
        testController.getCommandCount should be(1)
      }

      "R key is pressed (IncreaseBoardSize)" in {
        val testController = new TestController
        val actionManager = new ActionManager(testController)
        val keyEvent = KeyEventHelper.createKeyEvent(KeyCode.R)

        actionManager.handleKeyEvent(keyEvent)

        testController.getLastCommand should be(Some("IncreaseBoardSize"))
        testController.getCommandCount should be(1)
      }

      "F key is pressed (DecreaseBoardSize)" in {
        val testController = new TestController
        val actionManager = new ActionManager(testController)
        val keyEvent = KeyEventHelper.createKeyEvent(KeyCode.F)

        actionManager.handleKeyEvent(keyEvent)

        testController.getLastCommand should be(Some("DecreaseBoardSize"))
        testController.getCommandCount should be(1)
      }

      "Q key is pressed (QuitGame)" in {
        val testController = new TestController
        val actionManager = new ActionManager(testController)
        val keyEvent = KeyEventHelper.createKeyEvent(KeyCode.Q)

        actionManager.handleKeyEvent(keyEvent)

        testController.getLastCommand should be(Some("QuitGame"))
        testController.getCommandCount should be(1)
      }

      "N key is pressed (StartGame)" in {
        val testController = new TestController
        val actionManager = new ActionManager(testController)
        val keyEvent = KeyEventHelper.createKeyEvent(KeyCode.N)

        actionManager.handleKeyEvent(keyEvent)

        testController.getLastCommand should be(Some("StartGame"))
        testController.getCommandCount should be(1)
      }

      "U key is pressed (Undo)" in {
        val testController = new TestController
        val actionManager = new ActionManager(testController)
        val keyEvent = KeyEventHelper.createKeyEvent(KeyCode.U)

        actionManager.handleKeyEvent(keyEvent)

        testController.getLastCommand should be(Some("Undo"))
        testController.getCommandCount should be(1)
      }

      "I key is pressed (Redo)" in {
        val testController = new TestController
        val actionManager = new ActionManager(testController)
        val keyEvent = KeyEventHelper.createKeyEvent(KeyCode.I)

        actionManager.handleKeyEvent(keyEvent)

        testController.getLastCommand should be(Some("Redo"))
        testController.getCommandCount should be(1)
      }

      "an unmapped key is pressed" in {
        val testController = new TestController
        val actionManager = new ActionManager(testController)
        val keyEvent = KeyEventHelper.createKeyEvent(KeyCode.A) // Unmapped key

        actionManager.handleKeyEvent(keyEvent)

        // No command should be executed for unmapped keys
        testController.getCommandCount should be(0)
      }

      "multiple unmapped keys are pressed" in {
        val testController = new TestController
        val actionManager = new ActionManager(testController)

        val keyEvent1 = KeyEventHelper.createKeyEvent(KeyCode.A)
        val keyEvent2 = KeyEventHelper.createKeyEvent(KeyCode.B)
        val keyEvent3 = KeyEventHelper.createKeyEvent(KeyCode.Z)

        actionManager.handleKeyEvent(keyEvent1)
        actionManager.handleKeyEvent(keyEvent2)
        actionManager.handleKeyEvent(keyEvent3)

        // No commands should be executed for unmapped keys
        testController.getCommandCount should be(0)
      }
    }

    "properly initialize with a controller" in {
      val testController = new TestController
      val actionManager = new ActionManager(testController)

      actionManager should not be null
    }

    "handle multiple method calls independently" in {
      val testController = new TestController
      val actionManager = new ActionManager(testController)

      actionManager.onPlayNext()
      actionManager.onMoveUp()
      actionManager.onQuitGame()

      testController.getCommandCount should be(3)
      testController.executedCommands should contain allOf ("PlayNext", "MoveUp", "QuitGame")
    }

    "handle mixed keyboard and method calls" in {
      val testController = new TestController
      val actionManager = new ActionManager(testController)

      // Method calls
      actionManager.onPlayNext()
      actionManager.onUndo()

      // Keyboard calls
      val keyEvent1 = KeyEventHelper.createKeyEvent(KeyCode.W)
      val keyEvent2 = KeyEventHelper.createKeyEvent(KeyCode.S)

      actionManager.handleKeyEvent(keyEvent1)
      actionManager.handleKeyEvent(keyEvent2)

      testController.getCommandCount should be(4)
      testController.executedCommands should contain allOf ("PlayNext", "Undo", "MoveUp", "MoveDown")
    }
  }
}
