package de.htwg.se.MAEDN.aview

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.MAEDN.aview.TUI
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.util._
import de.htwg.se.MAEDN.model.State
import scala.reflect.Selectable.reflectiveSelectable
import de.htwg.se.MAEDN.controller.command._
import de.htwg.se.MAEDN.model.states._
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.model.Board
import de.htwg.se.MAEDN.model.PlayerFactory

class TUISpec extends AnyWordSpec with Matchers {

  class TestTUI(controller: Controller) extends TUI(controller) {
    val outputBuffer = new StringBuilder

    override protected def writeline(s: String): Unit = {
      outputBuffer.append(s).append("\n")
    }

    def output: String = outputBuffer.toString.trim
  }

  "A TUI" should {
    "print game cover and board on StartGameEvent" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.StartGameEvent)
      tui.output should include("Mensch")
      tui.output should include("Main Track")
    }

    "print configuration screen on ConfigEvent" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.ConfigEvent)
      tui.output should include("Players")
      tui.output should include("Board size")
    }

    "print cover only on BackToMenuEvent" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.BackToMenuEvent)
      tui.output should include("Mensch")
    }

    "print exiting message on QuitGameEvent" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.QuitGameEvent)
      tui.output should include("Exiting")
    }

    "print empty line on unknown Event" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(
        Event.UndoEvent
      ) // not handled explicitly
      tui.output shouldBe ""
    }

    "execute the real writeline method" in {
      val controller = new Controller
      val test: TUI { def callWriteline(): Unit } = new TUI(controller) {
        def callWriteline(): Unit = writeline("test")
      }
      noException should be thrownBy test.callWriteline()
    }

    "react to MoveFigureEvent with RunningState" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.MoveFigureEvent(0))
      tui.output should include("Mensch")
      tui.output should include("Main Track")
    }

    "react to PlayDiceEvent with roll != 6" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.PlayDiceEvent(3))
      tui.output should include("You rolled a 3!")
    }

    "react to PlayDiceEvent with roll == 6" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.PlayDiceEvent(6))
      tui.output should include("You rolled a 6!")
      tui.output should include("Use 'w'/'s' to select a figure")
    }

    "react to ChangeSelectedFigureEvent" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.ChangeSelectedFigureEvent(0))
      tui.output should include("Mensch")
      tui.output should include("Main Track")
    }

    "react to PlayNextEvent" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.PlayNextEvent(1))
      tui.output should include("Player 2's turn!")
      tui.output should include("Mensch")
    }

    "render cover and board for MoveFigureEvent in RunningState" in {
      val controller = new Controller
      val tui = new TestTUI(controller)

      // Wir umgehen die States und setzen direkt einen gültigen RunningState
      val running = de.htwg.se.MAEDN.model.states.RunningState(
        controller,
        moves = 0,
        board = Board(8),
        players = PlayerFactory.createPlayers(2, 4),
        rolled = 1,
        selectedFigure = 0
      )

      controller.manager = running.moveFigure() // erzeugt MoveFigureEvent
      controller.notifyObservers() // verarbeitet Event im TUI

      tui.output should include("Mensch")
      tui.output should include("Main Track")
      tui.output should include("Goal Lanes:")
    }

    "react to StartGameEvent in RunningState" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.manager = RunningState(
        controller,
        0,
        Board(8),
        PlayerFactory.createPlayers(2, 4)
      )
      controller.instantNotifyObservers(Event.StartGameEvent)
      tui.output should include("Main Track")
    }

    "react to PlayDiceEvent in RunningState" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.manager = RunningState(
        controller,
        0,
        Board(8),
        PlayerFactory.createPlayers(2, 4)
      )
      controller.instantNotifyObservers(Event.PlayDiceEvent(5))
      tui.output should include("You rolled a 5!")
      tui.output should include("Main Track")
    }

    "react to ChangeSelectedFigureEvent in RunningState" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.manager = RunningState(
        controller,
        0,
        Board(8),
        PlayerFactory.createPlayers(2, 4)
      )
      controller.instantNotifyObservers(Event.ChangeSelectedFigureEvent(1))
      tui.output should include("Main Track")
    }

    "react to InvalidMoveEvent in RunningState" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.manager = RunningState(
        controller,
        0,
        Board(8),
        PlayerFactory.createPlayers(2, 4)
      )
      controller.instantNotifyObservers(Event.InvalidMoveEvent)
      tui.output should include("Invalid move!")
      tui.output should include("Main Track")
    }

    "react to MoveFigureEvent in RunningState" in {
      val controller = new Controller
      val tui = new TestTUI(controller)
      controller.manager = RunningState(
        controller,
        0,
        Board(8),
        PlayerFactory.createPlayers(2, 4)
      )
      controller.instantNotifyObservers(Event.MoveFigureEvent(0))
      tui.output should include("Main Track")
    }

    "react to InvalidMoveEvent in non-RunningState" in {
      val controller = new Controller
      val tui = new TestTUI(controller)

      // MenuState ist der default Manager, also kein RS
      controller.instantNotifyObservers(Event.InvalidMoveEvent)

      tui.output should include("Main Track")
    }
  }
}
