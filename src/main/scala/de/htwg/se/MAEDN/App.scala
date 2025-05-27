package de.htwg.se.MAEDN

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.aview.gui.MAEDNApplication
import de.htwg.se.MAEDN.aview.tui.TUI

import scalafx.application.Platform

object App {
  val controller = new Controller()

  def main(args: Array[String]): Unit = {
    // Start JavaFX application properly on the JavaFX Application Thread
    val jfxArgs = Array.empty[String]

    // Launch JavaFX in a separate thread but properly using JFXApp3.launch
    new Thread(() => {
      val app = new MAEDNApplication(controller)
      app.main(jfxArgs)
    }).start()

    // Run TUI in the main thread
    val tui = new TUI(controller)
    tui.run()
  }
}
