package de.htwg.se.MAEDN

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.aview.tui.TUI

import scalafx.application.Platform
import de.htwg.se.MAEDN.aview.gui.GUI

object App {
  val controller = new Controller()

  def main(args: Array[String]): Unit = {
    new Thread(new Runnable {
      override def run(): Unit = {
        new TUI(controller).run()
      }
    }).start()
    val gui = new GUI(controller)
    gui.main(args)
  }
}
