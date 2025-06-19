package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.controller.controllerImp.Controller
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util.Event
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ConfigStateSpec extends AnyWordSpec with Matchers {

  "A ConfigState" should {

    val controller = new Controller
    val board = Board(8)
    val players = PlayerFactory(2, 4)
    val state = ConfigState(controller, 0, board, players)

    "have state == Config" in {
      state.state shouldBe State.Config
    }

    "start game and transition to RunningState" in {
      val next = state.startGame()
      next.isSuccess shouldBe true
      next.get shouldBe a[RunningState]
    }

    "quit game and return to MenuState" in {
      val next = state.quitGame()
      next.isSuccess shouldBe true
      next.get shouldBe a[MenuState]
    }

    "increase board size but not exceed 12" in {
      val newState = state.copy(board = Board(11)).increaseBoardSize()
      newState.isSuccess shouldBe true
      newState.get.board.size shouldBe 12

      val capped = newState.get.increaseBoardSize()
      capped.get.board.size shouldBe 12 // cap reached
    }

    "decrease board size but not go below 8" in {
      val newState = state.copy(board = Board(9)).decreaseBoardSize()
      newState.isSuccess shouldBe true
      newState.get.board.size shouldBe 8

      val capped = newState.get.decreaseBoardSize()
      capped.get.board.size shouldBe 8 // min reached
    }

    "increase number of figures per player but not exceed board size" in {
      val s = state.copy(players = PlayerFactory(2, 7), board = Board(8))
      val newState = s.increaseFigures()
      newState.isSuccess shouldBe true
      newState.get.players.head.figures should have size 8

      val capped = newState.get.increaseFigures()
      capped.get.players.head.figures should have size 8
    }

    "decrease number of figures per player but not go below 1" in {
      val s = state.copy(players = PlayerFactory(2, 2))
      val newState = s.decreaseFigures()
      newState.isSuccess shouldBe true
      newState.get.players.head.figures should have size 1

      val capped = newState.get.decreaseFigures()
      capped.get.players.head.figures should have size 1
    }

    "increase number of players up to 4" in {
      val s = state.copy(players = PlayerFactory(2, 3))
      val newState = s.moveUp()
      newState.isSuccess shouldBe true
      newState.get.players should have size 3

      val capped = newState.get.moveUp().get.moveUp()
      capped.get.players should have size 4
    }

    "decrease number of players down to 2" in {
      val s = state.copy(players = PlayerFactory(4, 3))
      val newState = s.moveDown()
      newState.isSuccess shouldBe true
      newState.get.players should have size 3

      val capped = newState.get.moveDown().get.moveDown()
      capped.get.players should have size 2
    }
  }
}
