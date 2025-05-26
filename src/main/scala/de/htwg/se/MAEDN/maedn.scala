package de.htwg.se.MAEDN

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.aview.GUI

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage

object Main extends JFXApp3 {
  override def start(): Unit = {
    stage = new PrimaryStage {
      title = "MAEDN"
      width = 800
      height = 600
      resizable = false
    }

    val controller = new Controller()
    new GUI(controller, stage)
  }
}
