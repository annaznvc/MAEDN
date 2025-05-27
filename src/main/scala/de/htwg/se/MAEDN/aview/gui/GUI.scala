// filepath: d:\AIN3\SE\MAEDN\src\main\scala\de\htwg\se\MAEDN\aview\gui\GUI.scala
package de.htwg.se.MAEDN.aview.gui

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.State
import de.htwg.se.MAEDN.util.{Event, Observer}
import de.htwg.se.MAEDN.controller.command._
import de.htwg.se.MAEDN.aview.gui.FXMLManager

import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control.{Alert, ButtonType}
import scalafx.stage.Stage
import scalafx.scene.layout.Pane
import scalafx.scene.media.MediaPlayer
import scala.compiletime.uninitialized
import scalafx.application.JFXApp3

class GUI(controller: Controller, stage: Stage = new Stage()) extends Observer {

  controller.add(this)

  val actionManager = new ActionManager(controller)
  val designManager = new DesignManager()

  // Scene management
  var currentScene: Scene = uninitialized
  var sceneCache: Map[String, Scene] = Map.empty
  var overlayVisible: Boolean = false

  // Audio components (for future enhancement)
  var mediaPlayer: Option[MediaPlayer] = None

  // Initialize GUI
  initialize()

  private def initialize(): Unit = {
    Platform.runLater {
      switchToScene("menu")
    }
  }

  private def switchToScene(sceneId: String): Unit = {
    val state = controller.manager.state
    val scene = sceneCache.getOrElse(sceneId, createScene(sceneId, state))
    sceneCache += (sceneId -> scene)
    currentScene = scene
    stage.scene = scene
  }

  private def createScene(sceneId: String, state: State): Scene = {
    val fxmlPath = s"/fxml/${sceneId}.fxml"
    FXMLManager.createSceneFromFXML(
      fxmlPath,
      actionManager = actionManager
    ) match {
      case Some(scene) =>
        scene
      case None =>
        sceneId match {
          case "menu"    => designManager.createMainScene(state)
          case "config"  => designManager.createConfigScene(state)
          case "running" => designManager.createGameScene(state)
          case _         => designManager.createMainScene(state)
        }
    }
  }

  private def showExitConfirmation(): Unit = {
    val alert = new Alert(Alert.AlertType.Confirmation)
    alert.title = "Exit Confirmation"
    alert.headerText = "Are you sure you want to exit?"
    alert.contentText = "Any unsaved progress will be lost."
    val result = alert.showAndWait()
    if (result.isDefined && result.get == ButtonType.OK) {
      Platform.exit()
      System.exit(0)
    }
  }

  private def showOverlay(overlayType: String): Unit = {
    overlayVisible = true
    // Create and show overlay based on type
    // This could be a pause menu, settings dialog, etc.
  }

  private def hideOverlay(): Unit = {
    overlayVisible = false
    // Hide any visible overlays
  }

  private def updateView(): Unit = {
    val state = controller.manager.state
    val sceneType = state match {
      case State.Menu    => "menu"
      case State.Config  => "config"
      case State.Running => "running"
    }
    sceneCache -= sceneType
    switchToScene(sceneType)
  }

  // Handle events from the controller
  override def processEvent(event: Event): Unit = {
    Platform.runLater {
      event match {
        case Event.StartGameEvent | Event.MoveFigureEvent(_) |
            Event.ChangeSelectedFigureEvent(_) =>
          updateView()
        case Event.ConfigEvent =>
          updateView()
        case Event.PlayDiceEvent(rolled) =>
          updateView()
          showStatusMessage(
            if (rolled == 6)
              "You rolled a 6! Use 'W'/'S' to select a figure and press 'X' to move."
            else
              s"You rolled a $rolled!"
          )
        case Event.PlayNextEvent(id) =>
          updateView()
          showStatusMessage(s"Player ${id + 1}'s turn!")
        case Event.UndoEvent | Event.RedoEvent =>
          updateView()
          showStatusMessage(s"${event.toString} executed!")
        case Event.BackToMenuEvent =>
          updateView()
        case Event.ErrorEvent(message) =>
          updateView()
          showErrorMessage(s"Error: $message")
          designManager.updateDesignForEvent(event, currentScene)
        case Event.QuitGameEvent =>
          Platform.exit()
        case _ =>
        // No-op fallback
      }
    }
  }

  private def showStatusMessage(message: String): Unit = {
    // Find and update status text in footer using ScalaFX delegate properties
    // The lookup returns a JavaFX Node which we need to cast
    Option(currentScene.delegate.lookup(".status-text")).foreach { jfxNode =>
      if (jfxNode.isInstanceOf[javafx.scene.text.Text]) {
        jfxNode.asInstanceOf[javafx.scene.text.Text].setText(message)
      }
    }
  }

  private def showErrorMessage(message: String): Unit = {
    showStatusMessage(message)
    // TODO: Add error dialog or temporary error notification
  }
}
