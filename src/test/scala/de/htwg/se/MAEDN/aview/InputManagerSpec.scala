package de.htwg.se.MAEDN.aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.jline.terminal.TerminalBuilder
import de.htwg.se.MAEDN.controller.Controller

class InputManagerSpec extends AnyWordSpec with Matchers {
  "An InputManager" should {
    "initialize without exceptions using a dumb terminal" in {
      noException should be thrownBy {
        val terminal = TerminalBuilder.builder().dumb(true).build()
        val controller = new Controller()
        val inputManager = new InputManager(controller, terminal)
        inputManager.terminal should not be (null)
      }
    }

  }
}
