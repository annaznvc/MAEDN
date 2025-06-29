package de.htwg.se.MAEDN.controller

import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.model.gameDataImp.GameData
import de.htwg.se.MAEDN.controller.command.{Command, QuitGameCommand}
import de.htwg.se.MAEDN.util._
import de.htwg.se.MAEDN.util.FileIO
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.Eventually

import scala.util.{Success, Failure, Try}
import scala.concurrent.duration.*
import controllerImp.Controller

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

      (controller.undoStack.top eq initialMemento) shouldBe true
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
        override def processEvent(event: Event): Unit =
          if event == Event.StartGameEvent then wasNotified = true
      })

      // Simuliere ein echtes Enqueue + notifyObservers
      controller.enqueueEvent(Event.StartGameEvent)

      val command = new DummyCommand(
        isNormal = true,
        result = Success(new DummyManager(controller))
      )

      controller.executeCommand(command)

      eventually {
        wasNotified shouldBe true
      }
    }

    // === Tests für QuitGameCommand bei State.GameOver ===

       

    "handle QuitGameCommand failure when game is over and enqueue error event" in {
      val controller = new Controller
      var captured: Option[Event] = None

      controller.add(new Observer {
        override def processEvent(e: Event): Unit = captured = Some(e)
      })

      val dummyFileIO = new FileIO {
        override def save[T <: Serializable[T]](
            data: T,
            filename: String,
            format: FileFormat,
            encrypt: Boolean = false
        ): Try[String] = Success("dummy/path")

        override def load[T](
            filename: String,
            deserializer: Deserializable[T]
        ): Try[T] = Failure(new RuntimeException("nicht benötigt"))

        override def listSaveFiles(format: Option[FileFormat] = None): Try[List[String]] =
          Success(Nil)
      }

      controller.manager = new DummyManager(controller) {
        override val state: State = State.GameOver
      }

      class DummyQuitCommand(result: Try[IManager])
          extends QuitGameCommand(controller, dummyFileIO) {
        override def execute(): Try[IManager] = result
      }

      controller.executeCommand(new DummyQuitCommand(Failure(new RuntimeException("quit failed"))))

      eventually {
        captured shouldBe Some(Event.ErrorEvent("quit failed"))
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
  ) extends IManager {
    override val rolled: Int = 0
    override val state: State = State.Menu
    override def createMemento: Option[GameData] = memento
  }

  class DummyCommand(
      override val isNormal: Boolean,
      val result: Try[IManager]
  ) extends Command {
    override def execute(): Try[IManager] = result
  }
}