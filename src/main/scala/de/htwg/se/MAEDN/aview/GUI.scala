package de.htwg.se.MAEDN.aview

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.State
import de.htwg.se.MAEDN.util.{Event, Observer}
import javafx.application.Platform
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.{Parent, Scene}
import javafx.scene.image.ImageView
import javafx.stage.Stage

// zentrale GUI, die Scene je nach State wechselt
class GUI(controller: Controller, stage: Stage) extends Observer {
  controller.add(this)
  updateScene(controller.manager.state)

  private def updateScene(state: State): Unit = {
    val resourcePath = state match {
      case State.Menu    => "/view/MenuView.fxml"
      case State.Config  => "/view/ConfigView.fxml"
      case State.Running => "/view/GameView.fxml"
    }

    val fxml = getClass.getResource(resourcePath)
    val root: Parent = FXMLLoader.load(fxml)

    Platform.runLater(new Runnable {
      override def run(): Unit = {
        val newScene = new Scene(root, 800, 600)
        stage.setScene(newScene)
        stage.sizeToScene()
        stage.centerOnScreen()
        stage.setTitle(s"MAEDN - ${state.toString}")
        stage.setResizable(false)
        stage.show()
      }
    })

  }

  override def processEvent(event: Event): Unit = {
    println(s"🔔 Event received: $event")
    println(s"📦 New manager state: ${controller.manager.state}")
    updateScene(controller.manager.state)
  }

}

// ===========================
// MenuView.fxml → MenuController
// ===========================
class MenuController {
  @FXML private var menuImage: ImageView = _

  def initialize(): Unit =
    println("MenuController initialized, image: " + menuImage)
}

// ===========================
// ConfigView.fxml → ConfigController
// ===========================
class ConfigController {
  def initialize(): Unit =
    println("ConfigController initialized")
}

// ===========================
// GameView.fxml → GameController
// ===========================
class GameController {
  def initialize(): Unit =
    println("GameController initialized")
}
