package de.htwg.se.MAEDN

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.aview.TUI

object maedn extends App {
  val controller = new Controller()
  val tui = new TUI(controller)
  tui.run()
}
