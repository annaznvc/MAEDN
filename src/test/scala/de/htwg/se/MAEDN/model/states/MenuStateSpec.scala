package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.controller.controllerImp.Controller
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util.Event
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class MenuStateSpec extends AnyWordSpec with Matchers {

  "A MenuState" should {

    val controller = new Controller
    val state = MenuState(controller, 0, Board(8), PlayerFactory(2, 4))

    "have state == Menu" in {
      state.state shouldBe State.Menu
    }

    "return correct board/figure/player info" in {
      state.getBoardSize shouldBe 8
      state.getPlayerCount shouldBe 2
      state.getFigureCount shouldBe 4
      state.getCurrentPlayer shouldBe 0
    }

    "transition to ConfigState on startGame and enqueue event" in {
      val newStateTry = state.startGame()
      newStateTry.isSuccess shouldBe true
      newStateTry.get.state shouldBe State.Config
    }

    "return itself on quitGame and enqueue quit event" in {
      val quitState = state.quitGame()
      quitState.isSuccess shouldBe true
      quitState.get shouldBe state
    }
  }
}
