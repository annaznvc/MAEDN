package de.htwg.se.MAEDN.controller

import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.controller.command.Command
import de.htwg.se.MAEDN.util._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.Eventually

import scala.util.{Success, Failure, Try}
import scala.concurrent.duration.*

class ControllerSpec extends AnyWordSpec with Matchers with Eventually {

  given patience: PatienceConfig =
    PatienceConfig(timeout = 1.second, interval = 50.millis)

  "A Controller" should {

    "push memento to undoStack and clear redoStack on normal command" in {
      val controller = new Controller
      val initialMemento = dummyGameData

      controller.manager = new DummyManager(controller, Some(initialMemento))

      val command = new DummyCommand(
        isNormal = true,
        result = Success(new DummyManager(controller))
      )

      controller.executeCommand(command)

      controller.undoStack.top shouldBe initialMemento
      controller.redoStack shouldBe empty
    }

    "not push memento when command is not normal" in {
      val controller = new Controller
      val command = new DummyCommand(
        isNormal = false,
        result = Success(new DummyManager(controller))
      )

      controller.executeCommand(command)

      controller.undoStack shouldBe empty
      controller.redoStack shouldBe empty
    }

    "handle failed command and enqueue error event" in {
      val controller = new Controller
      var captured: Option[Event] = None

      controller.add(new Observer {
        override def processEvent(event: Event): Unit = captured = Some(event)
      })

      val errorCommand = new DummyCommand(
        isNormal = true,
        result = Failure(new RuntimeException("fail"))
      )

      controller.executeCommand(errorCommand)

      eventually {
        captured shouldBe Some(Event.ErrorEvent("fail"))
      }
    }
    "update manager and notifyObservers on success" in {
      val controller = new Controller
      var wasNotified = false

      controller.add(new Observer {
        override def processEvent(event: Event): Unit = {
          if event == Event.StartGameEvent then wasNotified = true
        }
      })

      // 👉 Simuliere ein echtes Ereignis, das vom Observer verarbeitet werden kann
      controller.enqueueEvent(Event.StartGameEvent)

      // 👉 Führe erfolgreichen Command aus (der ruft notifyObservers() auf)
      val command = new DummyCommand(
        isNormal = true,
        result = Success(new DummyManager(controller))
      )

      controller.executeCommand(command)

      eventually {
        wasNotified shouldBe true
      }
    }

  }

  // === Dummy-Implementierungen ===

  val dummyGameData: GameData = GameData(
    moves = 0,
    board = Board(8),
    players = PlayerFactory(2, 4),
    selectedFigure = 0,
    rolled = 0
  )

  class DummyManager(
      override val controller: Controller,
      memento: Option[GameData] = Some(dummyGameData)
  ) extends Manager {
    override val rolled: Int = 0
    override val state: State = State.Menu
    override def createMemento: Option[GameData] = memento
  }

  class DummyCommand(
      override val isNormal: Boolean,
      val result: Try[Manager]
  ) extends Command {
    override def execute(): Try[Manager] = result
  }
}
