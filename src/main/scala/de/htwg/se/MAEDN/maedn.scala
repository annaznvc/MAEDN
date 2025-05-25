package de.htwg.se.MAEDN

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.aview.{TUI, GUI}

object maedn extends App {
  val controller = new Controller()

  // Safely handle command line arguments
  val safeArgs = Option(args).getOrElse(Array.empty[String])

  // Check command line arguments for interface choice
  val useGui = safeArgs.headOption match {
    case Some("--gui") | Some("-g") => true
    case Some("--tui") | Some("-t") => false
    case Some("--help") | Some("-h") =>
      println("Usage: maedn [--gui|-g] [--tui|-t] [--help|-h]")
      println("  --gui, -g    Use graphical user interface (default)")
      println("  --tui, -t    Use text user interface")
      println("  --help, -h   Show this help")
      sys.exit(0)
    case _ => true // Default to GUI
  }

  if (useGui) {
    println("Starting GUI...")
    try {
      val gui = new GUI(controller)
      println("GUI created successfully")
      gui.visible = true
      println("GUI set to visible")

      // Keep the main thread alive for GUI applications
      while (gui.visible) {
        Thread.sleep(100)
      }
    } catch {
      case e: Exception =>
        println(s"Error starting GUI: ${e.getMessage}")
        e.printStackTrace()
        sys.exit(1)
    }
  } else {
    println("Starting TUI...")
    val tui = new TUI(controller)
    tui.run()
  }
}
