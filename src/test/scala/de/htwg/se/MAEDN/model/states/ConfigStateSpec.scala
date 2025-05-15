package de.htwg.se.MAEDN.model.states

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.util.Event
import scala.collection.mutable

class ConfigStateSpec extends AnyWordSpec with Matchers {

  "A ConfigState" when {
    val controller = new Controller {
      override val eventQueue: mutable.Queue[Event] = mutable.Queue.empty
    }

    val board = Board(10)
    val players = PlayerFactory.createPlayers(2, 4)
    val configState = ConfigState(controller, 0, board, players)

    "starting the game" should {
      val next = configState.startGame()
      "enqueue StartGameEvent" in {
        controller.eventQueue.dequeue() shouldBe Event.StartGameEvent
      }
      "return a RunningState" in {
        next shouldBe a [RunningState]
      }
    }

    "quitting the game" should {
      val next = configState.quitGame()
      "enqueue BackToMenuEvent" in {
        controller.eventQueue.dequeue() shouldBe Event.BackToMenuEvent
      }
      "return a MenuState" in {
        next shouldBe a [MenuState]
      }
    }

    "increasing the board size" should {
      val next = configState.copy(board = Board(11)).increaseBoardSize()
      "enqueue ConfigEvent" in {
        controller.eventQueue.dequeue() shouldBe Event.ConfigEvent
      }
      "not exceed maximum board size of 12" in {
        next.board.size should be <= 12
      }
    }

    "decreasing the board size" should {
      val state = configState.copy(board = Board(9))
      val next = state.decreaseBoardSize()
      "enqueue ConfigEvent" in {
        controller.eventQueue.dequeue() shouldBe Event.ConfigEvent
      }
      "not go below minimum board size of 8" in {
        next.board.size should be >= 8
      }
    }

    "increasing figures per player" should {
      val next = configState.increaseFigures()
      "enqueue ConfigEvent" in {
        controller.eventQueue.dequeue() shouldBe Event.ConfigEvent
      }
      "increase figure count up to board size" in {
        next.players.head.figures.size should be > configState.players.head.figures.size
      }
    }

    "decreasing figures per player" should {
      val state = configState.copy(players = PlayerFactory.createPlayers(2, 4))
      val next = state.decreaseFigures()
      "enqueue ConfigEvent" in {
        controller.eventQueue.dequeue() shouldBe Event.ConfigEvent
      }
      "not go below 1 figure" in {
        next.players.head.figures.size should be >= 1
      }
    }

    "increasing number of players" should {
      val state = configState.copy(players = PlayerFactory.createPlayers(2, 4))
      val next = state.moveUp()
      "enqueue ConfigEvent" in {
        controller.eventQueue.dequeue() shouldBe Event.ConfigEvent
      }
      "not exceed 4 players" in {
        next.players.size should be <= 4
      }
    }

    "decreasing number of players" should {
      val state = configState.copy(players = PlayerFactory.createPlayers(3, 4))
      val next = state.moveDown()
      "enqueue ConfigEvent" in {
        controller.eventQueue.dequeue() shouldBe Event.ConfigEvent
      }
      "not go below 2 players" in {
        next.players.size should be >= 2
      }
    }
  }
}
