package de.htwg.se.MAEDN

import de.htwg.se.MAEDN.controller.{Controller, IController}
import de.htwg.se.MAEDN.aview.tui.{TUI, ITUI}
import de.htwg.se.MAEDN.aview.gui.{GUI, IGUI}

object App {
  val controller: IController = new Controller()

  def main(args: Array[String]): Unit = {
    new Thread(new Runnable {
      override def run(): Unit = {
        val tui: ITUI = new TUI(controller)
        tui.run()
      }
    }).start()
    val gui: IGUI = new GUI(controller)
    gui.main(args)
  }
}
