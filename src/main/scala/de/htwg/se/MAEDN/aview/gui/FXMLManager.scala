package de.htwg.se.MAEDN.aview.gui

import javafx.fxml.FXMLLoader
import javafx.{scene => jfxs}
import javafx.scene.{Scene => JFXScene}
import javafx.scene.layout.{Pane => JFXPane}
import java.net.URL
import scala.util.{Try, Success, Failure}
import scalafx.scene.Scene
import scalafx.Includes._

object FXMLManager {

  def loadFXML(fxmlPath: String): Option[JFXPane] = {
    Try {
      val loader = new FXMLLoader()
      val url: URL = getClass.getResource(fxmlPath)
      if (url != null) {
        loader.setLocation(url)
        loader.load[JFXPane]()
      } else {
        null
      }
    } match {
      case Success(pane) if pane != null => Some(pane)
      case _                             => None
    }
  }
  def createSceneFromFXML(
      fxmlPath: String,
      width: Double = 800,
      height: Double = 600,
      actionManager: ActionManager
  ): Option[Scene] = {
    // Create ScalaFX scene directly
    loadFXML(fxmlPath).map(pane =>
      new Scene(pane, width, height) {
        onKeyPressed = (event) => {
          pane.setFocusTraversable(false)
          actionManager.handleKeyEvent(event)
        }
      }
    )
  }

  def isAvailable(fxmlPath: String): Boolean = {
    Option(getClass.getResource(fxmlPath)).isDefined
  }
}
