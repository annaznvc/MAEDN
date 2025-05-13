package de.htwg.se.MAEDN.aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.util.Event
import de.htwg.se.MAEDN.model.{Manager, State}
import de.htwg.se.MAEDN.controller.command._
import org.jline.terminal.TerminalBuilder
s
class TUISpec extends AnyWordSpec with Matchers {

  val sharedTerminal = TerminalBuilder.builder().dumb(true).build()

  class TestTUI(ctrl: Controller) extends TUI(ctrl) {
    var lines: List[String] = Nil
    override protected def writeline(s: String): Unit = lines :+= s
  }

  "TUI" should {

    "react to StartGameEvent" in {
      val controller = new Controller()
      val tui = new TestTUI(controller)
      tui.processEvent(Event.StartGameEvent)
      tui.lines.exists(_.contains("Main Board")) shouldBe true
    }

    "react to InvalidMoveEvent" in {
      val controller = new Controller()
      val tui = new TestTUI(controller)
      tui.processEvent(Event.InvalidMoveEvent)
      tui.lines.exists(_.contains("Invalid move")) shouldBe true
    }

    "react to BackToMenuEvent" in {
      val controller = new Controller()
      val tui = new TestTUI(controller)
      tui.processEvent(Event.BackToMenuEvent)
      tui.lines.exists(_.contains("Menu")) shouldBe true
    }

    "call run() should invoke writeline and update" in {
      val controller = new Controller()
      var wasCalled = false
      class RunTestTUI(controller: Controller) extends TUI(controller) {
        override protected def writeline(s: String): Unit = wasCalled = true
        override def update(): Unit = ()
      }
      val tui = new RunTestTUI(controller)
      tui.run()
      wasCalled shouldBe true
    }

    "execute commands from mock input manager" in {
      val controller = new Controller()

      class CommandTestTUI(ctrl: Controller) extends TUI(controller) {
        var executed: List[String] = Nil
        var quitCalled = false

        override protected def writeline(s: String): Unit = ()
        override protected def quit(): Unit = quitCalled = true

        override val inputManager =
          new InputManager(ctrl, sharedTerminal) {
            private var inputs: List[() => Command] = List(
              () => StartGameCommand(ctrl),
              () => IncreaseBoardSizeCommand(ctrl),
              () => QuitGameCommand(ctrl),
              () => null
            )

            override def currentInput: Option[Command] = {
              inputs match {
                case head :: tail =>
                  inputs = tail
                  Option(head()).filter(_ != null)
                case Nil => None
              }
            }

            override def isEscape: Boolean = false
          }

        override def update(): Unit = {
          inputManager.currentInput match {
            case Some(cmd) =>
              executed :+= cmd.getClass.getSimpleName
              cmd.execute()
              update()
            case None =>
              quit()
          }
        }
      }

      val tui = new CommandTestTUI(controller)
      tui.update()

      tui.executed should contain("StartGameCommand")
      tui.executed should contain("IncreaseBoardSizeCommand")
      tui.quitCalled shouldBe true
    }

    "react to ConfigEvent" in {
      val controller = new Controller()
      val tui = new TestTUI(controller)
      tui.processEvent(Event.ConfigEvent)
      tui.lines.exists(_.contains("Players")) shouldBe true
    }

    "react to QuitGameEvent" in {
      val controller = new Controller()
      var quitWasCalled = false
      class QuitTestTUI(controller: Controller) extends TUI(controller) {
        override protected def quit(): Unit = quitWasCalled = true
        override protected def writeline(s: String): Unit = ()
      }
      val tui = new QuitTestTUI(controller)
      tui.processEvent(Event.QuitGameEvent)
      quitWasCalled shouldBe true
    }

    "react to unknown event" in {
      val controller = new Controller()
      val tui = new TestTUI(controller)
      tui.processEvent(Event.RollDiceEvent(4))
      tui.lines.exists(_ == "") shouldBe true
    }

    "call quit() should clear terminal and print exit message" in {
      val controller = new Controller()
      var lines: List[String] = Nil
      var closed = false

      class QuitTestTUI(controller: Controller) extends TUI(controller) {
        override protected def writeline(s: String): Unit = lines :+= s
        override protected def quit(): Unit = {
          super.quit()
          closed = true
        }
        def callQuit(): Unit = quit()
      }

      val tui = new QuitTestTUI(controller)
      tui.callQuit()
      lines.exists(_.contains("Exiting")) shouldBe true
      closed shouldBe true
    }
  }
}
