package de.htwg.se.MAEDN.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model.IManager
import de.htwg.se.MAEDN.util.{Event, Observer}
import de.htwg.se.MAEDN.model.IMemento
import de.htwg.se.MAEDN.controller.command.Command
import scala.util.{Try, Success}
import scala.collection.mutable

class IControllerSpec extends AnyWordSpec with Matchers {

  // Dummy-Implementierungen
  class DummyManager extends IManager {
    // Felder mit Defaultwerten
    override val moves: Int = 0
    override val board = null // oder ein echtes Dummy-Board, falls benötigt
    override val players = Nil
    override val selectedFigure: Int = 0
    override val rolled: Int = 0
    override val state = null
    override val controller = null

    // Methoden geben einfach sich selbst zurück (wie im echten IManager-Default)
    override def startGame() = Success(this)
    override def quitGame() = Success(this)
    override def moveUp() = Success(this)
    override def moveDown() = Success(this)
    override def increaseFigures() = Success(this)
    override def decreaseFigures() = Success(this)
    override def increaseBoardSize() = Success(this)
    override def decreaseBoardSize() = Success(this)
    override def playDice() = Success(this)
    override def playNext() = Success(this)
    override def moveFigure() = Success(this)

    // Getter
    override def getPlayers = players
    override def getPlayerCount = 0
    override def getFigureCount = 0
    override def getBoardSize = 0
    override def getCurrentPlayer = 0

    // Memento
    override def createMemento: Option[IMemento] = None
  }

  class DummyCommand extends Command {
    override def execute(): Try[IManager] = Success(new DummyManager)
  }

  "An IController" should {
    "add and remove observers" in {
      val controller = IController()
      var called = false
      val observer = new Observer {
        override def processEvent(event: Event): Unit = called = true
      }
      controller.add(observer)
      controller.enqueueEvent(Event.StartGameEvent)
      controller.remove(observer)
      // Es wird kein Event verarbeitet, da remove aufgerufen wurde
      called shouldBe false
    }

    "execute a command" in {
      val controller = IController()
      controller.manager = new DummyManager
      val command = new DummyCommand
      noException should be thrownBy controller.executeCommand(command)
    }

    "manage undo and redo stacks" in {
      val controller = IController()
      controller.undoStack shouldBe a[mutable.Stack[_]]
      controller.redoStack shouldBe a[mutable.Stack[_]]
    }

    "enqueue events" in {
      val controller = IController()
      controller.enqueueEvent(Event.StartGameEvent)
      controller.eventQueue.nonEmpty shouldBe true
    }
  }
}
