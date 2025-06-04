package de.htwg.se.MAEDN

import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.aview.gui.GUI
import de.htwg.se.MAEDN.aview.tui.TUI

object App {
  val controller: IController = IController()

  def main(args: Array[String]): Unit = {
    new Thread(new Runnable {
      override def run(): Unit = {
        val tui: TUI = new TUI(controller)
        tui.run()
      }
    }).start()
    val gui: GUI = new GUI(controller)
    gui.main(args)
  }
}
