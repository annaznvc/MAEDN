package de.htwg.se.MAEDN.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.states.{MenuState, ConfigState}
import de.htwg.se.MAEDN.util.Event

class ManagerSpec extends AnyWordSpec with Matchers {

  "A Manager (in MenuState)" when {
    val controller = new Controller
    val manager: Manager = Manager(controller)

    "initialized" should {
      "have correct default values" in {
        manager.moves shouldBe 0
        manager.board.size shouldBe 8
        manager.players.size shouldBe 2
        all(manager.players.map(_.figures.size)) shouldBe 4
        manager.selectedFigure shouldBe 0
      }

      "return values from getters" in {
        manager.getPlayerCount shouldBe 2
        manager.getFigureCount shouldBe 4
        manager.getBoardSize shouldBe 8
        manager.getCurrentPlayer shouldBe 0
      }

      "return itself as snapshot" in {
        manager.getSnapshot.getBoardSize shouldBe manager.getBoardSize
      }
    }

    "executing methods" should {
      "run all Manager methods for coverage" in {
        manager.increaseBoardSize().getBoardSize shouldBe manager.getBoardSize
        manager.decreaseBoardSize().getBoardSize shouldBe manager.getBoardSize
        manager.increaseFigures().getFigureCount shouldBe manager.getFigureCount
        manager.decreaseFigures().getFigureCount shouldBe manager.getFigureCount

        manager.moveUp().getCurrentPlayer shouldBe manager.getCurrentPlayer
        manager.moveDown().getCurrentPlayer shouldBe manager.getCurrentPlayer
        manager.playDice().getBoardSize shouldBe manager.getBoardSize
        manager.playNext().getPlayerCount shouldBe manager.getPlayerCount
        manager.moveFigure().getBoardSize shouldBe manager.getBoardSize
      }

      "execute quitGame and enqueue QuitGameEvent" in {
        val result = manager.quitGame()
        result shouldBe manager
        controller.eventQueue.dequeue() shouldBe Event.QuitGameEvent
      }

      "execute startGame and return a ConfigState, enqueue ConfigEvent" in {
        val result = manager.startGame()
        result shouldBe a[ConfigState]
        controller.eventQueue.dequeue() shouldBe Event.ConfigEvent
      }

        "quitGame" should {
    "enqueue QuitGameEvent and return same instance" in {
        val before = controller.eventQueue.size
        val result = manager.quitGame()
        result shouldBe manager
        controller.eventQueue.size shouldBe before + 1
        controller.eventQueue.dequeue() shouldBe Event.QuitGameEvent
    }
    }

        "startGame" should {
    "enqueue ConfigEvent and return a ConfigState" in {
        val before = controller.eventQueue.size
        val result = manager.startGame()
        result shouldBe a[Manager]
        result.getClass.getSimpleName should include("ConfigState")
        controller.eventQueue.size shouldBe before + 1
        controller.eventQueue.dequeue() shouldBe Event.ConfigEvent
    }
    }

        "Default Manager trait implementation" should {
    "return this in startGame and quitGame" in {
        val dummy = new Manager {
        override val controller = new Controller
        override val rolled = 0
        override val state = State.Menu
        }

        dummy.startGame() shouldBe dummy
        dummy.quitGame() shouldBe dummy
    }
    }



    }
  }
}
