package de.htwg.se.MAEDN.aview.tui

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.aview.tui.TUI
import de.htwg.se.MAEDN.controller.controllerImp.Controller
import de.htwg.se.MAEDN.util.Event
import org.jline.terminal.Terminal
import org.jline.terminal.impl.DumbTerminal
import java.io.{InputStream, OutputStream}

class TestableTUI(controller: Controller, terminal: Terminal)
    extends TUI(controller, terminal) {
  val output = new StringBuilder
  override protected def writeline(s: String): Unit =
    output.append(s).append("\n")
  override protected def quit(): Unit = output.append("Exiting...\n")
}

class TUISpec extends AnyWordSpec with Matchers {
  "TUI" should {
    val controller = new Controller
    val terminal = new DumbTerminal(System.in, System.out)
    val tui = new TestableTUI(controller, terminal)

    "render board and cover on StartGameEvent" in {
      tui.output.clear()
      tui.processEvent(Event.StartGameEvent)
      tui.output.toString should include("Mensch")
    }

    "show config screen on ConfigEvent" in {
      tui.output.clear()
      tui.processEvent(Event.ConfigEvent)
      tui.output.toString should include("Players")
    }

    "render cover on BackToMenuEvent" in {
      tui.output.clear()
      tui.processEvent(Event.BackToMenuEvent)
      tui.output.toString should include("Mensch")
    }

    "print exit message on QuitGameEvent" in {
      tui.output.clear()
      tui.processEvent(Event.QuitGameEvent)
      tui.output.toString should include("Exiting")
    }

    "react to ErrorEvent" in {
      tui.output.clear()
      tui.processEvent(Event.ErrorEvent("Invalid move!"))
      tui.output.toString should include("Invalid move")
    }
  }
}
