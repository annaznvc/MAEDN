package de.htwg.se.MAEDN.aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.controller.controllerImp.Controller
import de.htwg.se.MAEDN.util.Event
import de.htwg.se.MAEDN.model.states._
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.model.{Board, PlayerFactory}

class TUISpec extends AnyWordSpec with Matchers {

  class TestTUI(controller: Controller) extends TUI(controller) {
    val outputBuffer = new StringBuilder

    override protected def writeline(s: String): Unit = {
      outputBuffer.append(s).append("\n")
    }

    def output: String = outputBuffer.toString.trim
  }

  "A minimal TUI" should {

    "render board and cover on StartGameEvent" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.StartGameEvent)
      tui.output should include("Mensch")
      tui.output should include("Main Track")
    }

    "show config screen on ConfigEvent" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.ConfigEvent)
      tui.output should include("Players")
      tui.output should include("Board size")
    }

    "render cover on BackToMenuEvent" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.BackToMenuEvent)
      tui.output should include("Mensch")
    }

    "print exit message on QuitGameEvent" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.QuitGameEvent)
      tui.output should include("Exiting")
    }

    "react to PlayDiceEvent with roll 3" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.PlayDiceEvent(3))
      tui.output should include("You rolled a 3")
    }

    "react to PlayDiceEvent with roll 6" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.PlayDiceEvent(6))
      tui.output should include("You rolled a 6")
      tui.output should include("select a figure")
    }

    "react to PlayNextEvent" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.PlayNextEvent(1))
      tui.output should include("Player 2's turn")
    }

    "show correct player turn on PlayNextEvent" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.PlayNextEvent(1))
      tui.output should include("Player 2's turn")
    }

    "show UndoEvent executed! in RunningState" in {
      val controller = new Controller
      controller.manager =
        RunningState(controller, 0, Board(8), PlayerFactory(2, 4))

      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.UndoEvent)
      tui.output should include("UndoEvent executed!")
    }

    "print only board on RedoEvent outside RunningState" in {
      val controller = new Controller // MenuState ist default
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.RedoEvent)
      tui.output should include("Main Track")
      tui.output should not include "RedoEvent executed!"
    }

    "react to ErrorEvent with colored message" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(
        Event.ErrorEvent("Something went wrong")
      )
      tui.output should include("Error:")
      tui.output should include("Something went wrong")
    }

    "render player turn message in RunningState on PlayNextEvent" in {
      val controller = new Controller
      val tui = new TestTUI(controller)

      controller.manager = RunningState(
        controller,
        moves = 0,
        board = Board(8),
        players = PlayerFactory(2, 4)
      )

      controller.instantNotifyObservers(Event.PlayNextEvent(1))

      tui.output should include("Player 2's turn")
      tui.output should include(
        "Main Track"
      ) // um sicherzugehen, dass printBoard() auch durchläuft
    }

    "render empty line on unknown/unhandled event (e.g. KickFigureEvent)" in {
      val controller = new Controller
      val tui = new TestTUI(controller)

      controller.instantNotifyObservers(Event.KickFigureEvent)

      tui.output shouldBe ""
    }

  }
}
