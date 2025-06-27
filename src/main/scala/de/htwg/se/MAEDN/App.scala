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

    // Check if we should run in TUI-only mode
    val tuiOnly = sys.env.get("MAEDN_TUI_ONLY").contains("true") ||
      sys.props.get("java.awt.headless").contains("true") ||
      args.contains("--tui-only")

    if (tuiOnly) {
      println("Running in TUI-only mode...")
      // Run only TUI in the main thread
      val tui: TUI = DependencyInjector.getInstance[TUI]
      tui.run()
    } else {
      println("Running in GUI + TUI mode...")
      // Start TUI in a separate thread
      new Thread(new Runnable {
        override def run(): Unit = {
          val tui: TUI = DependencyInjector.getInstance[TUI]
          tui.run()
        }
      }).start()

      // Start GUI in the main thread
      try {
        val gui: GUI = DependencyInjector.getInstance[GUI]
        gui.main(args)
      } catch {
        case _: UnsupportedOperationException | _: java.awt.HeadlessException =>
          println("GUI not available, falling back to TUI-only mode...")
          // If GUI fails, keep TUI running and prevent exit
          Thread.currentThread().join()
      }
    }
  }
}
