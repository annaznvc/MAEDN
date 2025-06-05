package de.htwg.se.MAEDN.aview.tui

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.util.Event
import de.htwg.se.MAEDN.model.StatesImp.{RunningState, ConfigState, MenuState}
import de.htwg.se.MAEDN.model.State
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.model.BoardImp.Board
import de.htwg.se.MAEDN.model.PlayerImp.Player
import de.htwg.se.MAEDN.aview.tui.TUI
import de.htwg.se.MAEDN.util.{Observable, Observer}
import scala.concurrent.ExecutionContext.Implicits.global
import de.htwg.se.MAEDN.util.PlayerFactory

class TUISpec extends AnyWordSpec with Matchers {

  class TestTUI(controller: IController) extends TUI(controller) {
    val outputBuffer = new StringBuilder

    override protected def writeline(s: String): Unit = {
      outputBuffer.append(s).append("\n")
    }

    def output: String = outputBuffer.toString.trim
  }

  class TestController extends IController {
    private val observable = new Observable()
    def add(observer: Observer): Unit = observable.add(observer)
    def remove(observer: Observer): Unit = observable.remove(observer)
    def enqueueEvent(event: Event): Unit = observable.enqueueEvent(event)
    def instantNotifyObservers(event: Event): Unit =
      observable.instantNotifyObservers(event)

    // Dummy-Board und Players
    val dummyBoard = Board(4, null, null, null)
    val dummyPlayers = List.empty[Player]
    var manager: de.htwg.se.MAEDN.model.IManager =
      MenuState(this, 0, dummyBoard, dummyPlayers, 0)

    // STUBS für alle abstrakten Methoden:
    def eventQueue = new scala.collection.mutable.PriorityQueue[Event]()(
      Ordering.by(_.hashCode())
    )
    def executeCommand(
        command: de.htwg.se.MAEDN.controller.command.Command
    ): Unit = ()
    def redoStack =
      new scala.collection.mutable.Stack[de.htwg.se.MAEDN.model.IMemento]()
    def undoStack =
      new scala.collection.mutable.Stack[de.htwg.se.MAEDN.model.IMemento]()

  }

  "A minimal TUI" should {

    "render board and cover on StartGameEvent" in {
      val controller = new TestController
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.StartGameEvent)
      tui.output should include("Mensch")
      tui.output should include("Main Track")
    }

    "show config screen on ConfigEvent" in {
      val controller = new TestController
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.ConfigEvent)
      tui.output should include("Players")
      tui.output should include("Board size")
    }

    "render cover on BackToMenuEvent" in {
      val controller = new TestController
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.BackToMenuEvent)
      tui.output should include("Mensch")
    }

    "print exit message on QuitGameEvent" in {
      val controller = new TestController
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.QuitGameEvent)
      tui.output should include("Exiting")
    }

    "react to PlayDiceEvent with roll 3" in {
      val controller = new TestController
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.PlayDiceEvent(3))
      tui.output should include("You rolled a 3")
    }

    "react to PlayDiceEvent with roll 6" in {
      val controller = new TestController
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.PlayDiceEvent(6))
      tui.output should include("You rolled a 6")
      tui.output should include("select a figure")
    }

    "react to PlayNextEvent" in {
      val controller = new TestController
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.PlayNextEvent(1))
      tui.output should include("Player 2's turn")
    }

    "show correct player turn on PlayNextEvent" in {
      val controller = new TestController
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.PlayNextEvent(1))
      tui.output should include("Player 2's turn")
    }

    "show UndoEvent executed! in RunningState" in {
      val controller = new TestController
      // Fixed Board constructor - you need to provide the missing strategy parameters
      // This is a placeholder - replace with actual strategy implementations
      import de.htwg.se.MAEDN.model.IMoveStrategy
      val dummyStrategy = new IMoveStrategy {
        def canMove(
            figure: de.htwg.se.MAEDN.model.IFigure,
            figures: List[de.htwg.se.MAEDN.model.IFigure],
            size: Int,
            rolled: Int
        ): Boolean = true

        def moveFigure(
            figure: de.htwg.se.MAEDN.model.IFigure,
            figures: List[de.htwg.se.MAEDN.model.IFigure],
            size: Int,
            rolled: Int
        ): List[de.htwg.se.MAEDN.model.IFigure] = figures
      }

      controller.manager = RunningState(
        controller,
        0,
        Board(
          8,
          dummyStrategy,
          dummyStrategy,
          dummyStrategy
        ), // Fixed Board constructor
        PlayerFactory(2, 4) // <-- KORREKT!
      )

      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.UndoEvent)
      tui.output should include("UndoEvent executed!")
    }

    "print only board on RedoEvent outside RunningState" in {
      val controller = new TestController // MenuState ist default
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(Event.RedoEvent)
      tui.output should include("Main Track")
      tui.output should not include "RedoEvent executed!"
    }

    "react to ErrorEvent with colored message" in {
      val controller = new TestController
      val tui = new TestTUI(controller)
      controller.instantNotifyObservers(
        Event.ErrorEvent("Something went wrong")
      )
      tui.output should include("Error:")
      tui.output should include("Something went wrong")
    }

    "render player turn message in RunningState on PlayNextEvent" in {
      val controller = new TestController
      val tui = new TestTUI(controller)

      // Fixed Board and PlayerFactory usage
      import de.htwg.se.MAEDN.model.IMoveStrategy
      val dummyStrategy = new IMoveStrategy {
        def canMove(
            figure: de.htwg.se.MAEDN.model.IFigure,
            figures: List[de.htwg.se.MAEDN.model.IFigure],
            size: Int,
            rolled: Int
        ): Boolean = true

        def moveFigure(
            figure: de.htwg.se.MAEDN.model.IFigure,
            figures: List[de.htwg.se.MAEDN.model.IFigure],
            size: Int,
            rolled: Int
        ): List[de.htwg.se.MAEDN.model.IFigure] = figures
      }

      controller.manager = RunningState(
        controller,
        moves = 0,
        board = Board(
          8,
          dummyStrategy,
          dummyStrategy,
          dummyStrategy
        ), // Fixed Board constructor
        players = PlayerFactory(2, 4) // Fixed PlayerFactory usage
      )

      controller.instantNotifyObservers(Event.PlayNextEvent(1))

      tui.output should include("Player 2's turn")
      tui.output should include(
        "Main Track"
      ) // um sicherzugehen, dass printBoard() auch durchläuft
    }

    "render empty line on unknown/unhandled event (e.g. KickFigureEvent)" in {
      val controller = new TestController
      val tui = new TestTUI(controller)

      controller.instantNotifyObservers(Event.KickFigureEvent)

      tui.output shouldBe ""
    }

  }
}
