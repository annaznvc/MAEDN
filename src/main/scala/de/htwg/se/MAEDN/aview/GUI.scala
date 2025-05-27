package de.htwg.se.MAEDN.aview

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.{State, Figure}
import de.htwg.se.MAEDN.util.{Event, Observer}
import de.htwg.se.MAEDN.controller.command._

import scalafx.application.Platform
import javafx.fxml.FXMLLoader
import scalafx.scene.{Parent, Scene}
import scalafx.scene.input.{KeyEvent, KeyCode}
import scalafx.stage.Stage
import scalafx.Includes._
import de.htwg.se.MAEDN.util.Position

import javafx.{fxml => jfxf}
import scalafx.scene.layout.GridPane
import scalafx.scene.layout.StackPane
import scalafx.scene.layout.ColumnConstraints
import scalafx.scene.layout.RowConstraints

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
        ("/view/RunningView.fxml", () => new GameController(controller))
    }

    Platform.runLater {
      try {
        // JavaFX-FXMLLoader direkt mit Location und Controller
        val loader = new FXMLLoader(getClass.getResource(resourcePath))
        loader.setController(controllerFactory())
        // gibt javafx.scene.Parent zurück, wandelt durch `Includes._` in scalafx.scene.Parent
        // 1. Lade das JavaFX-Parent
        val jfxRoot: javafx.scene.Parent = loader.load()
// 2. Nutze die implizite Conversion (Import scalafx.Includes._),
//    um daraus ein scalafx.scene.Parent zu machen
        val root: Parent = jfxRoot

        val newScene = new Scene(root, 800, 600)

        // Globale Tastatur-Events für die ganze Scene
        newScene.onKeyPressed = (event: KeyEvent) => {
          handleGlobalKeyEvent(event, state)
        }

        stage.scene = newScene
        stage.sizeToScene()
        stage.centerOnScreen()
        stage.title = s"MAEDN - ${state.toString}"
        stage.resizable = false
        stage.show()

        // Focus auf die Scene setzen, damit KeyEvents funktionieren
        newScene.root().requestFocus()

      } catch {
        case e: Exception =>
          println(s"Error loading FXML: ${e.getMessage}")
          e.printStackTrace()
      }
    }
  }

  private def handleGlobalKeyEvent(event: KeyEvent, state: State): Unit = {
    val command: Option[Command] = event.code match {
      case KeyCode.N | KeyCode.Enter | KeyCode.Space =>
        Some(StartGameCommand(controller))
      case KeyCode.Q | KeyCode.Escape => Some(QuitGameCommand(controller))
      case KeyCode.W                  => Some(MoveUpCommand(controller))
      case KeyCode.S                  => Some(MoveDownCommand(controller))
      case KeyCode.E => Some(IncreaseFiguresCommand(controller))
      case KeyCode.D => Some(DecreaseFiguresCommand(controller))
      case KeyCode.R => Some(IncreaseBoardSizeCommand(controller))
      case KeyCode.F => Some(DecreaseBoardSizeCommand(controller))
      case KeyCode.X => Some(PlayNextCommand(controller))
      case KeyCode.U => Some(UndoCommand(controller))
      case KeyCode.I => Some(RedoCommand(controller))
      case _         => None
    }

    command.foreach(controller.executeCommand)
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
  @jfxf.FXML
  private var menuImage: javafx.scene.image.ImageView = _

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

  @jfxf.FXML
  def onStartGame(): Unit = {
    controller.executeCommand(StartGameCommand(controller))
  }

  @jfxf.FXML
  def onQuitGame(): Unit = {
    controller.executeCommand(QuitGameCommand(controller))
  }
}

// ===========================
// ConfigView.fxml → ConfigController
// ===========================
class ConfigController(controller: Controller) extends Observer {

  // FXML Labels for dynamic updates
  @jfxf.FXML
  private var playerCountLabel: javafx.scene.control.Label = _
  @jfxf.FXML
  private var figureCountLabel: javafx.scene.control.Label = _
  @jfxf.FXML
  private var boardSizeLabel: javafx.scene.control.Label = _
  @jfxf.FXML
  private var currentConfigLabel: javafx.scene.control.Label = _
  @jfxf.FXML
  private var backgroundImage: javafx.scene.image.ImageView = _

  private var isRegistered = false

  def initialize(): Unit = {
    println("ConfigController initialized")

    // Load background image
    if (backgroundImage != null) {
      try {
        val imageUrl = getClass.getResource("/images/Background.png")
        if (imageUrl != null) {
          val image = new javafx.scene.image.Image(imageUrl.toString)
          backgroundImage.setImage(image)
        } else {
          println(
            "Warning: Background image not found at /images/Background.png"
          )
        }
      } catch {
        case e: Exception =>
          println(s"Error loading background image: ${e.getMessage}")
      }
    }

    // Subscribe to controller events to update labels
    if (!isRegistered) {
      controller.add(this)
      isRegistered = true
      println("ConfigController registered as observer")
    }

    // Initial update of labels - force immediate update
    Platform.runLater(() => updateLabels())
  }

  private def updateLabels(): Unit = {
    try {
      println(
        s"Updating labels - PlayerCount: ${controller.manager.getPlayerCount}, FigureCount: ${controller.manager.getFigureCount}, BoardSize: ${controller.manager.getBoardSize}"
      )

      if (playerCountLabel != null) {
        playerCountLabel.setText(controller.manager.getPlayerCount.toString)
        println(
          s"PlayerCount label updated to: ${controller.manager.getPlayerCount}"
        )
      } else {
        println("playerCountLabel is null!")
      }

      if (figureCountLabel != null) {
        figureCountLabel.setText(controller.manager.getFigureCount.toString)
        println(
          s"FigureCount label updated to: ${controller.manager.getFigureCount}"
        )
      } else {
        println("figureCountLabel is null!")
      }

      if (boardSizeLabel != null) {
        boardSizeLabel.setText(controller.manager.getBoardSize.toString)
        println(
          s"BoardSize label updated to: ${controller.manager.getBoardSize}"
        )
      } else {
        println("boardSizeLabel is null!")
      }

      if (currentConfigLabel != null) {
        val config =
          s"Current: ${controller.manager.getPlayerCount} Players, ${controller.manager.getFigureCount} Figures, ${controller.manager.getBoardSize}x${controller.manager.getBoardSize} Board"
        currentConfigLabel.setText(config)
        println(s"CurrentConfig label updated to: $config")
      } else {
        println("currentConfigLabel is null!")
      }
    } catch {
      case e: Exception =>
        println(s"Error updating labels: ${e.getMessage}")
        e.printStackTrace()
    }
  }

  override def processEvent(event: Event): Unit = {
    println(s"ConfigController received event: $event")
    event match {
      case Event.ConfigEvent =>
        Platform.runLater(() => {
          println("Processing ConfigEvent - updating labels")
          updateLabels()
        })
      case _ =>
        println(s"Ignoring event: $event")
    }
  }

  // Cleanup method - sollte aufgerufen werden wenn der Controller nicht mehr gebraucht wird
  def cleanup(): Unit = {
    if (isRegistered) {
      controller.remove(this)
      isRegistered = false
      println("ConfigController unregistered as observer")
    }
  }

  @jfxf.FXML
  def onStartGame(): Unit = {
    cleanup() // Cleanup before leaving
    controller.executeCommand(StartGameCommand(controller))
  }

  @jfxf.FXML
  def onBackToMenu(): Unit = {
    cleanup() // Cleanup before leaving
    controller.executeCommand(QuitGameCommand(controller))
  }

  @jfxf.FXML
  def onIncreasePlayer(): Unit = {
    println("Increase player button clicked")
    controller.executeCommand(MoveUpCommand(controller))
  }

  @jfxf.FXML
  def onDecreasePlayer(): Unit = {
    println("Decrease player button clicked")
    controller.executeCommand(MoveDownCommand(controller))
  }

  @jfxf.FXML
  def onIncreaseFigures(): Unit = {
    println("Increase figures button clicked")
    controller.executeCommand(IncreaseFiguresCommand(controller))
  }

  @jfxf.FXML
  def onDecreaseFigures(): Unit = {
    println("Decrease figures button clicked")
    controller.executeCommand(DecreaseFiguresCommand(controller))
  }

  @jfxf.FXML
  def onIncreaseBoardSize(): Unit = {
    println("Increase board size button clicked")
    controller.executeCommand(IncreaseBoardSizeCommand(controller))
  }

  @jfxf.FXML
  def onDecreaseBoardSize(): Unit = {
    println("Decrease board size button clicked")
    controller.executeCommand(DecreaseBoardSizeCommand(controller))
  }
}

// ===========================
// RunningView.fxml → GameController
// ===========================
class GameController(controller: Controller) {
  @jfxf.FXML
  var gameBoard: javafx.scene.layout.GridPane = _

  def initialize(): Unit = {
    println("GameController initialized")
    val figures = controller.manager.players.flatMap(_.figures)
    renderCrossBoard(gameBoard, controller.manager.board.size, figures)
  }

  private case class Pos(r: Int, c: Int)

  // Erzeugt einen zentralen Kreuzpfad für beliebige size (8-12 empfohlen)
  private def generateCrossPath(size: Int, gridSize: Int = 13): Seq[Pos] = {
    val offset = (gridSize - size) / 2
    val half = size / 2
    val path = scala.collection.mutable.ArrayBuffer.empty[Pos]

    // --- Teil 1: Start unten, nach oben, nach links, Ecke ---
    val startRow1 = (size match {
      case 8  => 10
      case 10 => 11
      case 12 => 12
      case _ =>
        throw new IllegalArgumentException("Nur size 8, 10, 12 erlaubt!")
    })
    val startCol1 = 5
    path += Pos(startRow1, startCol1)
    for (i <- 1 until half) path += Pos(startRow1 - i, startCol1)
    val leftRow1 = startRow1 - (half - 1)
    for (i <- 1 to (half - 1)) path += Pos(leftRow1, startCol1 - i)
    val cornerRow1 = leftRow1 - 1
    val cornerCol1 = startCol1 - (half - 1)
    path += Pos(cornerRow1, cornerCol1)

    // --- Teil 2: Neues Startfeld (eine Zeile über letzter Ecke, gleiche Spalte) ---
    val startRow2 = cornerRow1 - 1
    val startCol2 = cornerCol1
    path += Pos(startRow2, startCol2)
    for (i <- 1 to (half - 1)) path += Pos(startRow2, startCol2 + i)
    val rightCol2 = startCol2 + (half - 1)

    // --- Teil 3: Nach oben in derselben Spalte (negative Richtung!) ---
    val startRow3 = startRow2
    val startCol3 = rightCol2 // <- statt rightCol2 + 1
    for (i <- 1 to (half - 1)) path += Pos(startRow3 - i, startCol3)
    // --- Teil 4: Abschlussfeld, eine Spalte weiter rechts ---
    path += Pos(startRow3 - (half - 1), startCol3 + 1)

    // --- Teil 5: Neues Startfeld (gleiche Zeile wie Abschlussfeld, Spalte +1) ---
    val startRow4 = startRow3 - (half - 1)
    val startCol4 = startCol3 + 2
    path += Pos(startRow4, startCol4) // <-- Das fehlte!

    // Jetzt nach unten auffüllen (zunehmender Zeilenindex)
    for (i <- 1 to (half - 1)) {
      path += Pos(startRow4 + i, startCol4)
    }

    // --- Teil 6: Nach rechts auffüllen (gleiche Zeile, aufsteigender Spaltenindex) ---
    val rightRow3 = startRow4 + (half - 1)
    val rightCol3 = startCol4
    for (i <- 1 to (half - 1)) {
      path += Pos(rightRow3, rightCol3 + i)
    }

    // --- Teil 7: Abschlussfeld, eine Zeile weiter unten ---
    path += Pos(rightRow3 + 1, rightCol3 + (half - 1))

    // --- Teil 8: Neues Startfeld (eine Zeile weiter unten, gleiche Spalte) ---
    val startRow5 =
      rightRow3 + 2 // +1 für Abschlussfeld, +1 für neues Startfeld
    val startCol5 = rightCol3 + (half - 1)
    path += Pos(startRow5, startCol5)

    // Nach links auffüllen (gleiche Zeile, abnehmender Spaltenindex)
    for (i <- 1 to (half - 1)) {
      path += Pos(startRow5, startCol5 - i)
    }

    // --- Teil 9: Nach unten auffüllen (gleiche Spalte, zunehmender Zeilenindex) ---
    val downStartRow = startRow5
    val downCol = startCol5 - (half - 1)
    for (i <- 1 to (half - 1)) {
      path += Pos(downStartRow + i, downCol)
    }

    // --- Teil 10: Nur einen Schritt nach links am Ende ---
    val finalRow = downStartRow + (half - 1)
    val finalColStart = downCol
    path += Pos(finalRow, finalColStart - 1)

    path.toSeq
  }

  def renderCrossBoard(
      grid: javafx.scene.layout.GridPane,
      size: Int,
      figures: Seq[Figure]
  ): Unit = {
    val gridSize = 13
    grid.getChildren.clear()
    // KEINE Constraints mehr setzen, das macht jetzt die FXML!

    val path = generateCrossPath(size, gridSize)

    for ((pos, idx) <- path.zipWithIndex) {
      val stack = new javafx.scene.layout.StackPane()
      stack.setPrefSize(32, 32)
      stack.setStyle(
        "-fx-background-color: #FFFACD; -fx-border-color: #8B4513;"
      )

      // Figur auf diesem Feld?
      figures.find(_.adjustedIndex(size) == Position.Normal(idx)).foreach {
        fig =>
          val circle = new scalafx.scene.shape.Circle {
            radius = 12
            fill = scalafx.scene.paint.Color.web(fig.owner.color.toString)
          }
          stack.getChildren.add(circle)
      }

      grid.add(stack, pos.c, pos.r)
    }
  }

  @jfxf.FXML
  def rollDice(): Unit = {
    controller.executeCommand(PlayNextCommand(controller))
  }
}
