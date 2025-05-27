package de.htwg.se.MAEDN.aview.gui

import de.htwg.se.MAEDN.model.{Board, Player, State}
import de.htwg.se.MAEDN.util.{Color, Event}

import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout._
import scalafx.scene.paint.{Color => SFXColor}
import scalafx.scene.text.{Font, FontWeight, Text}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.effect.DropShadow
import scalafx.Includes._
import javafx.{scene => jfxs}

class DesignManager {

  private val PRIMARY_COLOR = "#2C3E50"
  private val SECONDARY_COLOR = "#3498DB"
  private val ACCENT_COLOR = "#E74C3C"
  private val SUCCESS_COLOR = "#27AE60"
  private val WARNING_COLOR = "#F39C12"
  private val BACKGROUND_COLOR = "#ECF0F1"
  private val TEXT_COLOR = "#2C3E50"

  // Enhanced scene creation with FXML support
  def createMainScene(state: State): Scene = {
    val scene = tryCreateSceneFromFXML(state).getOrElse {
      // Fallback to programmatic creation
      val root = createMainLayout(state)
      new Scene(root, 800, 600)
    }
    applyTheme(scene, state)
    scene
  }

  def createConfigScene(state: State): Scene = {
    val sceneContent = FXMLManager.loadFXML("/fxml/ConfigView.fxml").getOrElse {
      // Fallback to programmatic creation
      createConfigView()
    }
    val sfxScene = sceneContent match {
      case sfxParent: scalafx.scene.Parent =>
        new Scene(sfxParent, 800, 600)
      case jfxParent: javafx.scene.Parent =>
        new Scene(
          new scalafx.scene.layout.Pane() {
            delegate.getChildren.add(jfxParent)
          },
          800,
          600
        )
      case _ =>
        throw new IllegalArgumentException("Unsupported scene content type")
    }
    applyTheme(sfxScene, state)
    sfxScene
  }

  def createGameScene(state: State): Scene = {
    val sceneContent =
      FXMLManager.loadFXML("/fxml/RunningView.fxml").getOrElse {
        // Fallback to programmatic creation
        createGameView()
      }
    val sfxScene = sceneContent match {
      case sfxParent: scalafx.scene.Parent =>
        new Scene(sfxParent, 800, 600)
      case jfxParent: javafx.scene.Parent =>
        new Scene(
          new scalafx.scene.layout.Pane() {
            delegate.getChildren.add(jfxParent)
          },
          800,
          600
        )
      case _ =>
        throw new IllegalArgumentException("Unsupported scene content type")
    }
    applyTheme(sfxScene, state)
    sfxScene
  }

  private def tryCreateSceneFromFXML(state: State): Option[Scene] = {
    val fxmlPath = state match {
      case State.Menu    => "/fxml/MenuView.fxml"
      case State.Config  => "/fxml/ConfigView.fxml"
      case State.Running => "/fxml/RunningView.fxml"
      case _             => return None
    }

    FXMLManager.loadFXML(fxmlPath).map { jfxPane =>
      // Create ScalaFX Scene with JavaFX Pane
      new Scene(jfxPane, 800, 600)
    }
  }

  private def createMainLayout(state: State): BorderPane = {
    new BorderPane {
      // Header
      top = createHeader(state)

      // Center content based on state
      center = state match {
        case State.Menu    => createMenuView()
        case State.Config  => createConfigView()
        case State.Running => createGameView()
      }

      // Footer with controls
      bottom = createFooter()
    }
  }

  private def createHeader(state: State): VBox = {
    new VBox {
      alignment = Pos.Center
      padding = Insets(20)

      children = Seq(
        new Text("Mensch ärgere dich nicht") {
          font = Font.font("Arial", FontWeight.Bold, 28)
          // Dynamic title color based on state
          val titleColor = state match {
            case State.Menu    => ACCENT_COLOR
            case State.Config  => WARNING_COLOR
            case State.Running => SUCCESS_COLOR
          }
          fill = SFXColor.web(titleColor)

          // Add drop shadow effect
          effect = new DropShadow {
            radius = 5.0
            offsetX = 3.0
            offsetY = 3.0
          }
        },
        new Text(getStateDescription(state)) {
          font = Font.font("Arial", 14)
          fill = SFXColor.web(TEXT_COLOR)
        }
      )
    }
  }

  private def createMenuView(): VBox = {
    new VBox(20) {
      alignment = Pos.Center
      padding = Insets(40)

      children = Seq(
        createStyledButton("Start New Game", SUCCESS_COLOR),
        createStyledButton("Configuration", WARNING_COLOR),
        createStyledButton("Quit Game", ACCENT_COLOR)
      )
    }
  }

  private def createConfigView(): VBox = {
    new VBox(15) {
      alignment = Pos.Center
      padding = Insets(30)

      children = Seq(
        // Player count configuration
        createConfigSection("Players", 2, 4),
        createConfigSection("Figures", 2, 6),
        createConfigSection("Board Size", 4, 12),
        new Separator(),
        createStyledButton("Back to Menu", SECONDARY_COLOR),
        createStyledButton("Start Game", SUCCESS_COLOR)
      )
    }
  }

  private def createGameView(): VBox = {
    new VBox(10) {
      alignment = Pos.Center
      padding = Insets(20)

      children = Seq(
        // Game board area
        createBoardArea(),
        // Game controls
        createGameControls()
      )
    }
  }

  private def createConfigSection(title: String, min: Int, max: Int): HBox = {
    new HBox(10) {
      alignment = Pos.Center

      val sectionLabel = title + ":"
      val valueText = min.toString

      children = Seq(
        new Label(sectionLabel) {
          font = Font.font("Arial", FontWeight.Bold, 14)
        },
        createSmallButton("-"),
        new Label(valueText) {
          font = Font.font("Arial", FontWeight.Bold, 16)
        },
        createSmallButton("+")
      )
    }
  }

  private def createBoardArea(): Region = {
    new Pane {
      prefWidth = 400
      prefHeight = 400
      style =
        s"-fx-background-color: white; -fx-border-color: $PRIMARY_COLOR; -fx-border-width: 2;"

      // TODO: Add dynamic board rendering based on game state
      children = Seq(
        new Text("Game Board Area") {
          font = Font.font("Arial", 16)
          fill = SFXColor.web(TEXT_COLOR)
          x = 150
          y = 200
        }
      )
    }
  }

  private def createGameControls(): HBox = {
    new HBox(10) {
      alignment = Pos.Center

      children = Seq(
        createStyledButton("Move Up (W)", SECONDARY_COLOR),
        createStyledButton("Move Down (S)", SECONDARY_COLOR),
        createStyledButton("Play Next (X)", SUCCESS_COLOR),
        createStyledButton("Undo (U)", WARNING_COLOR),
        createStyledButton("Redo (I)", WARNING_COLOR)
      )
    }
  }

  private def createFooter(): HBox = {
    new HBox {
      alignment = Pos.Center
      padding = Insets(10)
      style = s"-fx-background-color: $PRIMARY_COLOR;"

      children = Seq(
        new Text("Ready") {
          font = Font.font("Arial", 12)
          fill = SFXColor.White
          styleClass += "status-text"
        }
      )
    }
  }

  private def createStyledButton(btnText: String, color: String): Button = {
    new Button(btnText) {
      prefWidth = 200
      prefHeight = 40
      font = Font.font("Arial", FontWeight.Bold, 14)
      style = s"""
        -fx-background-color: $color;
        -fx-text-fill: white;
        -fx-background-radius: 5;
        -fx-border-radius: 5;
        -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 2, 0, 1, 1);
      """

      // Hover effect
      onMouseEntered = _ => {
        style = s"""
          -fx-background-color: derive($color, -10%);
          -fx-text-fill: white;
          -fx-background-radius: 5;
          -fx-border-radius: 5;
          -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 3, 0, 2, 2);
        """
      }

      onMouseExited = _ => {
        style = s"""
          -fx-background-color: $color;
          -fx-text-fill: white;
          -fx-background-radius: 5;
          -fx-border-radius: 5;
          -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 2, 0, 1, 1);
        """
      }

      // Set ID for lookup by CSS selector
      id = btnText.replaceAll("\\s+", "").toLowerCase
    }
  }

  private def createSmallButton(btnText: String): Button = {
    new Button(btnText) {
      prefWidth = 30
      prefHeight = 30
      font = Font.font("Arial", FontWeight.Bold, 12)
      style = s"""
        -fx-background-color: $SECONDARY_COLOR;
        -fx-text-fill: white;
        -fx-background-radius: 15;
        -fx-border-radius: 15;
      """
    }
  }

  private def applyTheme(scene: Scene, state: State): Unit = {
    scene.root.value.setStyle(s"-fx-background-color: $BACKGROUND_COLOR;")
  }

  private def getStateDescription(state: State): String = {
    state match {
      case State.Menu    => "Welcome to the game! Choose an option below."
      case State.Config  => "Configure your game settings"
      case State.Running => "Game in progress"
    }
  }

  // Dynamic color mapping for player colors
  def getPlayerColor(color: Color): String = {
    color match {
      case Color.RED    => "#E74C3C"
      case Color.BLUE   => "#3498DB"
      case Color.YELLOW => "#F1C40F"
      case Color.GREEN  => "#27AE60"
      case Color.WHITE  => "#BDC3C7"
    }
  }

  // Update UI based on game events
  def updateDesignForEvent(event: Event, scene: Scene): Unit = {
    // TODO: Implement dynamic UI updates based on events
    event match {
      case Event.ErrorEvent(message) =>
        // Show error styling
        scene.root.value.setStyle(
          s"-fx-background-color: derive($ACCENT_COLOR, 90%);"
        )
      case Event.StartGameEvent =>
        // Apply game theme
        scene.root.value.setStyle(
          s"-fx-background-color: derive($SUCCESS_COLOR, 90%);"
        )
      case _ =>
        // Default styling
        scene.root.value.setStyle(s"-fx-background-color: $BACKGROUND_COLOR;")
    }
  }
}
