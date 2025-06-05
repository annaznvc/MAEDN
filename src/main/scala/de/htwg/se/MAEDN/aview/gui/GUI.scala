// filepath: d:\AIN3\SE\MAEDN\src\main\scala\de\htwg\se\MAEDN\aview\gui\GUI.scala
package de.htwg.se.MAEDN.aview.gui

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.State
import de.htwg.se.MAEDN.util.{Event, Observer}
import de.htwg.se.MAEDN.controller.command._
import de.htwg.se.MAEDN.aview.gui.DynamicRenderer

import javafx.scene.input.KeyEvent
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.Label

import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control.{Alert, ButtonType}
import scalafx.stage.Stage
import scalafx.scene.layout.Pane
import scalafx.scene.media.MediaPlayer
import scala.compiletime.uninitialized
import scalafx.application.JFXApp3
import scala.annotation.switch
import scalafx.scene.layout.StackPane
import scalafx.Includes._
import scalafx.scene.Node
import scala.collection.mutable.Map

class GUI(controller: IController) extends JFXApp3 with Observer {

  controller.add(this)

  val actionManager = new ActionManager(controller)
  var currentSceneContent: Parent = uninitialized
  var sceneCache: Map[String, Parent] = Map.empty
  var overlayVisible: Boolean = false
  val rootPane = new StackPane() // Placeholder for root pane

  // Audio components (for future enhancement)
  var mediaPlayer: Option[MediaPlayer] = None
  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Mensch aergere Dich Nicht"
      fullScreen = true
      fullScreenExitHint = ""
      resizable = false
      scene = new Scene(rootPane, 800, 600) {
        rootPane.setFocusTraversable(false)
        onKeyPressed = (event: KeyEvent) => {
          // Handle key events using ActionManager
          actionManager.handleKeyEvent(event)
        }
      }
    }
    // Initialize with the menu scene
    switchToScene("Menu")
    stage.show()
  }

  def switchToScene(sceneKey: String): Unit = {
    val sceneContent =
      sceneCache.getOrElse(sceneKey, createSceneContent(sceneKey))
    sceneCache += (sceneKey -> sceneContent)
    currentSceneContent = sceneContent

    Platform.runLater {
      rootPane.children.clear()
      rootPane.children.add(sceneContent)
      sceneContent.requestFocus()

      // Trigger initial render for running state
      if (sceneKey == "Running") {
        updateRender()
      }
    }
  }

  def createSceneContent(sceneKey: String): Parent = {
    val sceneId = sceneKey.toLowerCase // "menu", "config", "running"
    val fxmlPath = s"/fxml/${sceneId}.fxml"
    val loader = new FXMLLoader(getClass.getResource(fxmlPath))
    loader.setController(actionManager)
    loader.load[Parent]()
  }

  def showExitConfirmation(): Unit = {
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

  // Handle events from the controller
  override def processEvent(event: Event): Unit = {
    Platform.runLater {
      event match {
        case Event.StartGameEvent | Event.MoveFigureEvent(_) |
            Event.ChangeSelectedFigureEvent(_) =>
          switchToScene("Running")
          updateRender() // Update GUI after game state changes
        case Event.ConfigEvent =>
          switchToScene("Config")
          updateConfigRender() // Update configuration labels when config changes
        case Event.PlayDiceEvent(rolled) =>
          updateRender() // Update GUI to show dice result
          showStatusMessage(
            if (rolled == 6)
              "You rolled a 6! Use 'W'/'S' to select a figure and press 'X' to move."
            else
              s"You rolled a $rolled!"
          )
        case Event.PlayNextEvent(id) =>
          updateRender() // Update GUI to show current player
          showStatusMessage(s"Player ${id + 1}'s turn!")
        case Event.UndoEvent | Event.RedoEvent =>
          updateRender() // Update GUI after undo/redo
          showStatusMessage(s"${event.toString} executed!")
        case Event.BackToMenuEvent =>
          switchToScene("Menu")
        case Event.WinEvent(playerId) =>
          showWinDialog(playerId)
        case Event.ErrorEvent(message) =>
          showErrorMessage(s"Error: $message")
        case Event.QuitGameEvent =>
          Platform.exit()
        case _ =>
        // No-op fallback
      }
    }
  }

  def updateRender(): Unit = {
    if (currentSceneContent != null) {
      DynamicRenderer.updateRender(controller, currentSceneContent)
    }
  }
  def updateConfigRender(): Unit = {
    if (currentSceneContent != null) {
      val manager = controller.manager

      // Update player count label
      Option(currentSceneContent.lookup("#playerCountLabel")).foreach { node =>
        if (node.isInstanceOf[Label]) {
          node.asInstanceOf[Label].setText(manager.getPlayerCount.toString)
        }
      }

      // Update figure count label
      Option(currentSceneContent.lookup("#figureCountLabel")).foreach { node =>
        if (node.isInstanceOf[Label]) {
          node.asInstanceOf[Label].setText(manager.getFigureCount.toString)
        }
      }

      // Update board size label
      Option(currentSceneContent.lookup("#boardSizeLabel")).foreach { node =>
        if (node.isInstanceOf[Label]) {
          node.asInstanceOf[Label].setText(manager.getBoardSize.toString)
        }
      }
    }
  }

  def showStatusMessage(message: String): Unit = {
    // Find and update status text in footer using ScalaFX delegate properties
    // The lookup returns a JavaFX Node which we need to cast
    Option(rootPane.scene.value.delegate.lookup(".status-text")).foreach {
      jfxNode =>
        if (jfxNode.isInstanceOf[javafx.scene.text.Text]) {
          jfxNode.asInstanceOf[javafx.scene.text.Text].setText(message)
        }
    }
  }

  def showErrorMessage(message: String): Unit = {
    showStatusMessage(message)
    // TODO: Add error dialog or temporary error notification
  }

  def showOverlay(overlayType: String): Unit = {
    overlayVisible = true
    // Create and show overlay based on type
    // This could be a pause menu, settings dialog, etc.
  }

  def hideOverlay(): Unit = {
    overlayVisible = false
    // Hide any visible overlays
  }

  def showWinDialog(playerId: Int): Unit = {
    val winner = controller.manager.players(playerId)
    val playerName = s"Player ${playerId + 1}"
    val playerColor = winner.color.toString

    // Create overlay content
    val overlayPane = new StackPane() {
      style = "-fx-background-color: rgba(0, 0, 0, 0.7);"

      val contentPane = new StackPane() {
        style =
          "-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 30; -fx-border-radius: 10;"
        maxWidth = 400
        maxHeight = 200

        val winLabel = new scalafx.scene.control.Label {
          text =
            s"🎉 Congratulations! 🎉\n\n$playerName ($playerColor) has won the game!\n\nPress ENTER to return to the main menu."
          style = "-fx-font-size: 16; -fx-text-alignment: center;"
          wrapText = true
        }

        children = winLabel
      }

      children = contentPane

      // Handle key events to close overlay
      onKeyPressed = (event: KeyEvent) => {
        import javafx.scene.input.KeyCode
        if (event.getCode == KeyCode.ENTER || event.getCode == KeyCode.ESCAPE) {
          hideWinOverlay()
          switchToScene("Menu")
        }
      }

      focusTraversable = true
    }

    // Add overlay to root pane
    Platform.runLater {
      rootPane.children.add(overlayPane)
      overlayPane.requestFocus()
      overlayVisible = true
    }
  }

  def hideWinOverlay(): Unit = {
    Platform.runLater {
      rootPane.children.removeIf(node =>
        node.style.value.contains("rgba(0, 0, 0, 0.7)")
      )
      overlayVisible = false
    }
  }

  override def main(args: Array[String]): Unit = super.main(args)

}
