package de.htwg.se.MAEDN.aview

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.State
import de.htwg.se.MAEDN.util.{Event, Observer}
import de.htwg.se.MAEDN.controller.command._
import javafx.application.Platform
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.{Parent, Scene}
import javafx.scene.image.ImageView
import javafx.scene.input.{KeyEvent, KeyCode}
import javafx.stage.Stage

// zentrale GUI, die Scene je nach State wechselt
class GUI(controller: Controller, stage: Stage) extends Observer {
  controller.add(this)
  updateScene(controller.manager.state)

  private def updateScene(state: State): Unit = {
    val (resourcePath, controllerFactory) = state match {
      case State.Menu =>
        ("/view/MenuView.fxml", () => new MenuController(controller))
      case State.Config =>
        ("/view/ConfigView.fxml", () => new ConfigController(controller))
      case State.Running =>
        ("/view/GameView.fxml", () => new GameController(controller))
    }

    Platform.runLater(new Runnable {
      override def run(): Unit = {
        try {
          val loader = new FXMLLoader(getClass.getResource(resourcePath))
          loader.setController(controllerFactory())
          val root: Parent = loader.load()

          val newScene = new Scene(root, 800, 600)

          // Globale Tastatur-Events für die ganze Scene
          newScene.setOnKeyPressed((event: KeyEvent) => {
            handleGlobalKeyEvent(event, state)
          })

          stage.setScene(newScene)
          stage.sizeToScene()
          stage.centerOnScreen()
          stage.setTitle(s"MAEDN - ${state.toString}")
          stage.setResizable(false)
          stage.show()

          // Focus auf die Scene setzen, damit KeyEvents funktionieren
          newScene.getRoot.requestFocus()

        } catch {
          case e: Exception =>
            println(s"Error loading FXML: ${e.getMessage}")
            e.printStackTrace()
        }
      }
    })
  }

  private def handleGlobalKeyEvent(event: KeyEvent, state: State): Unit = {
    val command: Option[Command] = (event.getCode, state) match {
      // Menu State
      case (KeyCode.N, State.Menu) | (KeyCode.ENTER, State.Menu) |
          (KeyCode.SPACE, State.Menu) =>
        Some(StartGameCommand(controller))
      case (KeyCode.Q, State.Menu) =>
        Some(QuitGameCommand(controller))

      // Config State
      case (KeyCode.W, State.Config) =>
        Some(MoveUpCommand(controller))
      case (KeyCode.S, State.Config) =>
        Some(MoveDownCommand(controller))
      case (KeyCode.E, State.Config) =>
        Some(IncreaseFiguresCommand(controller))
      case (KeyCode.D, State.Config) =>
        Some(DecreaseFiguresCommand(controller))
      case (KeyCode.R, State.Config) =>
        Some(IncreaseBoardSizeCommand(controller))
      case (KeyCode.F, State.Config) =>
        Some(DecreaseBoardSizeCommand(controller))
      case (KeyCode.N, State.Config) | (KeyCode.ENTER, State.Config) |
          (KeyCode.SPACE, State.Config) =>
        Some(StartGameCommand(controller))
      case (KeyCode.Q, State.Config) | (KeyCode.ESCAPE, State.Config) =>
        Some(QuitGameCommand(controller))

      // Running State
      case (KeyCode.X, State.Running) =>
        Some(PlayNextCommand(controller))
      case (KeyCode.W, State.Running) =>
        Some(MoveUpCommand(controller))
      case (KeyCode.S, State.Running) =>
        Some(MoveDownCommand(controller))
      case (KeyCode.U, State.Running) =>
        Some(UndoCommand(controller))
      case (KeyCode.I, State.Running) =>
        Some(RedoCommand(controller))
      case (KeyCode.Q, State.Running) | (KeyCode.ESCAPE, State.Running) =>
        Some(QuitGameCommand(controller))

      case _ => None
    }

    command.foreach { cmd =>
      controller.executeCommand(cmd)
    }
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
class MenuController(controller: Controller) {
  @FXML private var menuImage: ImageView = _

  def initialize(): Unit = {
    println("MenuController initialized, image: " + menuImage)

    // Wenn das Bild nicht angezeigt wird, versuchen Sie einen anderen Pfad
    if (menuImage != null) {
      try {
        val imageUrl = getClass.getResource("/images/menu_background.png")
        if (imageUrl != null) {
          val image = new javafx.scene.image.Image(imageUrl.toString)
          menuImage.setImage(image)
        } else {
          println(
            "Warning: Menu image not found at /images/menu_background.png"
          )
        }
      } catch {
        case e: Exception =>
          println(s"Error loading menu image: ${e.getMessage}")
      }
    }
  }

  @FXML
  def onStartGame(): Unit = {
    controller.executeCommand(StartGameCommand(controller))
  }

  @FXML
  def onQuitGame(): Unit = {
    controller.executeCommand(QuitGameCommand(controller))
  }
}

// ===========================
// ConfigView.fxml → ConfigController
// ===========================
class ConfigController(controller: Controller) {
  def initialize(): Unit = {
    println("ConfigController initialized")
  }

  @FXML
  def onStartGame(): Unit = {
    controller.executeCommand(StartGameCommand(controller))
  }

  @FXML
  def onBackToMenu(): Unit = {
    controller.executeCommand(QuitGameCommand(controller))
  }

  @FXML
  def onIncreasePlayer(): Unit = {
    controller.executeCommand(MoveUpCommand(controller))
  }

  @FXML
  def onDecreasePlayer(): Unit = {
    controller.executeCommand(MoveDownCommand(controller))
  }

  @FXML
  def onIncreaseFigures(): Unit = {
    controller.executeCommand(IncreaseFiguresCommand(controller))
  }

  @FXML
  def onDecreaseFigures(): Unit = {
    controller.executeCommand(DecreaseFiguresCommand(controller))
  }

  @FXML
  def onIncreaseBoardSize(): Unit = {
    controller.executeCommand(IncreaseBoardSizeCommand(controller))
  }

  @FXML
  def onDecreaseBoardSize(): Unit = {
    controller.executeCommand(DecreaseBoardSizeCommand(controller))
  }
}

// ===========================
// GameView.fxml → GameController
// ===========================
class GameController(controller: Controller) {
  def initialize(): Unit = {
    println("GameController initialized")
  }

  @FXML
  def onPlayNext(): Unit = {
    controller.executeCommand(PlayNextCommand(controller))
  }

  @FXML
  def onMoveUp(): Unit = {
    controller.executeCommand(MoveUpCommand(controller))
  }

  @FXML
  def onMoveDown(): Unit = {
    controller.executeCommand(MoveDownCommand(controller))
  }

  @FXML
  def onUndo(): Unit = {
    controller.executeCommand(UndoCommand(controller))
  }

  @FXML
  def onRedo(): Unit = {
    controller.executeCommand(RedoCommand(controller))
  }

  @FXML
  def onBackToMenu(): Unit = {
    controller.executeCommand(QuitGameCommand(controller))
  }
}
