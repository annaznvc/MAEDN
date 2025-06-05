package de.htwg.se.MAEDN.controller

import de.htwg.se.MAEDN.model.GameDataImp.GameData
import de.htwg.se.MAEDN.model.BoardImp.Board
import de.htwg.se.MAEDN.util.PlayerFactory
import de.htwg.se.MAEDN.model.IManager
import de.htwg.se.MAEDN.model.State

import de.htwg.se.MAEDN.model.StrategyImp.{KickFigureStrategy, NormalMoveStrategy, ToBoardStrategy}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Millis, Seconds, Span}
import scala.concurrent.duration._
import scala.util.{Try, Success, Failure}
import de.htwg.se.MAEDN.util.{Event, Observer}
import de.htwg.se.MAEDN.controller.command.Command
// Dummy-Strategien für Tests
val DefaultMoveStrategy = new NormalMoveStrategy()
val DefaultToBoardStrategy = new ToBoardStrategy()
val DefaultKickFigureStrategy = new KickFigureStrategy()

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
    board = Board(8, DefaultMoveStrategy, DefaultToBoardStrategy, DefaultKickFigureStrategy),
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
    // Dummy-Implementierungen für alle abstrakten Methoden von IManager:
    override def startGame() = ???
    override def quitGame() = ???
    override def moveUp() = ???
    override def moveDown() = ???
    override def increaseFigures() = ???
    override def decreaseFigures() = ???
    override def increaseBoardSize() = ???
    override def decreaseBoardSize() = ???
    override def playDice() = ???
    override def playNext() = ???
    override def moveFigure() = ???
    override def getPlayers = Nil
    override def getPlayerCount = 0
    override def getFigureCount = 0
    override def getBoardSize = 0
    override def getCurrentPlayer = 0
    override val selectedFigure: Int = 0
  }

  class DummyCommand(
      override val isNormal: Boolean,
      val result: Try[IManager]
  ) extends Command {
    override def execute(): Try[IManager] = result
  }
}