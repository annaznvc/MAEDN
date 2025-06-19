package de.htwg.se.MAEDN

import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.aview.gui.GUI
import de.htwg.se.MAEDN.aview.tui.TUI
import de.htwg.se.MAEDN.module.DependencyInjector

/** Updated App object that uses dependency injection instead of raw object
  * creation. This demonstrates how to properly use the MAEDNModule for object
  * creation.
  */
object App {

  def main(args: Array[String]): Unit = {
    // Use dependency injection to get instances instead of creating them directly
    val controller: IController = DependencyInjector.getInstance[IController]

    // Start TUI in a separate thread
    new Thread(new Runnable {
      override def run(): Unit = {
        val tui: TUI = DependencyInjector.getInstance[TUI]
        tui.run()
      }
    }).start()

    // Start GUI in the main thread
    val gui: GUI = DependencyInjector.getInstance[GUI]
    gui.main(args)
  }
}
