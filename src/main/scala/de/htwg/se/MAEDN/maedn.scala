package de.htwg.se.MAEDN

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.aview.GUI
import javafx.application.Application
import javafx.stage.Stage

class Main extends Application {
  override def start(stage: Stage): Unit = {
    val controller = new Controller()
    new GUI(controller, stage)
  }
}

object Main {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[Main], args *)
  }
}
