package de.htwg.se.MAEDN.aview.gui

import de.htwg.se.MAEDN.controller.Controller

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.stage.Stage
import scala.compiletime.uninitialized
import scalafx.scene.input.KeyCombination
import scalafx.scene.input.KeyEvent
import scalafx.Includes._

class MAEDNApplication(controller: Controller) extends JFXApp3 {

  private var guiInstance: GUI = uninitialized

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "MAEDN - Mensch Ärgere Dich Nicht"
      resizable = true
      fullScreen = true
      fullScreenExitHint = ""
      scene = new Scene {
        onKeyPressed = (event: KeyEvent) => {
          guiInstance.actionManager.handleKeyEvent(event)
        }
      }
      // Use fullScreenExitKey for proper syntax
      fullScreenExitKey = KeyCombination.keyCombination("ESCAPE")
    }

    // Create the GUI with our managed stage
    guiInstance = new GUI(controller, stage)

    // The GUI will handle scene creation and switching
  }

  override def stopApp(): Unit = {
    // Cleanup resources if needed
    if (guiInstance != null) {
      controller.remove(guiInstance)
    }
    super.stopApp()
  }
}
