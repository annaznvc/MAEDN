package de.htwg.se.MAEDN.aview

import scala.swing._
import scala.swing.event._
import java.awt.{Color, Font, Graphics2D, RenderingHints, Dimension}
import java.awt.geom.{Ellipse2D, Rectangle2D}
import javax.swing.{Timer, SwingUtilities}
import java.awt.event.{ActionListener, ActionEvent}

import de.htwg.se.MAEDN.util.{Event, Observer, Position}
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.controller.command._
import de.htwg.se.MAEDN.model.{State, Player}

class GUI(controller: Controller) extends MainFrame with Observer {
  controller.add(this)

  private val menuImage: Option[Image] =
    try {
      Some(
        javax.imageio.ImageIO.read(
          getClass.getResource("/images/MenuStateCover.png")
        )
      )
    } catch {
      case e: Throwable =>
        println("Could not load menu image: " + e.getMessage)
        None
    }

  // Feste, kleinere Fenstergröße definieren
  private val windowSize = new Dimension(800, 600) // Kleinere Größe
  this.size = windowSize
  this.preferredSize = windowSize

  // Colors
  private val backgroundColor = new Color(245, 245, 245)
  private val redColor = new Color(220, 53, 69)
  private val blueColor = new Color(0, 123, 255)
  private val yellowColor = new Color(255, 193, 7)
  private val greenColor = new Color(40, 167, 69)
  private val whiteColor = new Color(255, 255, 255)
  private val blackColor = new Color(33, 37, 41)

  // Layout components
  private val titleLabel = new Label("") {
    preferredSize = new Dimension(0, 8) // dezenter Abstand
    visible = false
  }

  private val statusLabel = new Label("Welcome to the game!") {
    font = new Font("Arial", Font.PLAIN, 14)
    horizontalAlignment = Alignment.Center
  }

  private val gamePanel: GameBoardPanel = {
    val panel = new GameBoardPanel()
    // Panel soll die gesamte Fenstergröße nutzen
    panel.preferredSize = windowSize
    panel.minimumSize = windowSize
    panel.maximumSize = windowSize
    panel
  }

  private val controlPanel = new ControlPanel()

  // Main layout
  contents = gamePanel
  pack()
  centerOnScreen()

  println(s"Fenstergröße: ${size.width}x${size.height}")
  println(s"Panelgröße: ${gamePanel.size.width}x${gamePanel.size.height}")

  centerOnScreen()

  // Window settings
  title = "Mensch ärgere dich nicht"
  centerOnScreen()
  resizable = false

  // Key bindings - Listen to the game panel for key events
  listenTo(gamePanel.keys)
  gamePanel.focusable = true
  gamePanel.requestFocusInWindow()

  reactions += { case KeyPressed(_, key, _, _) =>
    key match {
      case Key.X => controller.executeCommand(PlayNextCommand(controller))
      case Key.W => controller.executeCommand(MoveUpCommand(controller))
      case Key.S => controller.executeCommand(MoveDownCommand(controller))
      case Key.E =>
        controller.executeCommand(IncreaseFiguresCommand(controller))
      case Key.D =>
        controller.executeCommand(DecreaseFiguresCommand(controller))
      case Key.R =>
        controller.executeCommand(IncreaseBoardSizeCommand(controller))
      case Key.F =>
        controller.executeCommand(DecreaseBoardSizeCommand(controller))
      case Key.Q     => controller.executeCommand(QuitGameCommand(controller))
      case Key.N     => controller.executeCommand(StartGameCommand(controller))
      case Key.U     => controller.executeCommand(UndoCommand(controller))
      case Key.I     => controller.executeCommand(RedoCommand(controller))
      case Key.Space => controller.executeCommand(StartGameCommand(controller))
      case _         =>
    }
  }

  // Game board panel
  class GameBoardPanel extends Panel {

    private val configBackground: Option[Image] =
      try {
        Some(
          javax.imageio.ImageIO.read(
            new java.io.File("src/main/resources/images/ConfigStateCover.jpeg")
          )
        )
      } catch {
        case e: Throwable =>
          println("Could not load config background: " + e.getMessage)
          None
      }

    background = whiteColor
    override def paintComponent(g: Graphics2D): Unit = {
      super.paintComponent(g)

      controller.manager.state match {
        case State.Config =>
          // Hintergrundbild zeichnen
          configBackground.foreach { img =>
            val imgWidth = img.getWidth(null)
            val imgHeight = img.getHeight(null)

            val scaleX = size.width.toDouble / imgWidth
            val scaleY = size.height.toDouble / imgHeight
            val scale = Math.min(scaleX, scaleY)

            val scaledWidth = (imgWidth * scale).toInt
            val scaledHeight = (imgHeight * scale).toInt

            val x = (size.width - scaledWidth) / 2
            val y = (size.height - scaledHeight) / 2

            val avgColor = getAverageColorFromImage(img)
            g.setColor(avgColor)
            g.fillRect(0, 0, size.width, size.height)

            g.drawImage(img, x, y, scaledWidth, scaledHeight, null)

          }

          // Schriftfarbe und Stil
          g.setColor(new Color(255, 235, 190)) // beige
          g.setFont(new Font("Georgia", Font.BOLD, 36))

          // Zahlen aus dem Manager
          val players = controller.manager.getPlayerCount.toString
          val figures = controller.manager.getFigureCount.toString
          val boardSize = controller.manager.getBoardSize.toString

          // Positionen (x, y) grob zentriert zwischen den Pfeilen – bei Bedarf leicht anpassen
          g.drawString(players, 200, 310)
          g.drawString(figures, 385, 300)
          g.drawString(boardSize, 560, 310)

        case State.Menu =>
          drawMenuScreen(g)

        case State.Running =>
          drawGameBoard(g)
      }
    }

    private def drawMenuScreen(g: Graphics2D): Unit = {
      val width = size.width
      val height = size.height

      // Fülle zunächst den gesamten Hintergrund
      g.setColor(backgroundColor)
      g.fillRect(0, 0, width, height)

      menuImage.foreach { img =>
        val imgWidth = img.getWidth(null)
        val imgHeight = img.getHeight(null)

        // Berechne beide Skalierungsfaktoren
        val scaleX = width.toDouble / imgWidth
        val scaleY = height.toDouble / imgHeight

        // Nutze den kleineren Faktor, damit das ganze Bild sichtbar ist
        val scale = Math.min(scaleX, scaleY)

        // Berechne die finalen Dimensionen
        val scaledWidth = (imgWidth * scale).toInt
        val scaledHeight = (imgHeight * scale).toInt

        // Zentriere das Bild
        val x = (width - scaledWidth) / 2
        val y = (height - scaledHeight) / 2

        // Optional: Fülle den Hintergrund hinter dem Bild
        // mit einer passenden Farbe
        val avgColor = getAverageColorFromImage(img)
        g.setColor(avgColor)
        g.fillRect(0, 0, width, height)

        // Zeichne das Bild
        g.drawImage(img, x, y, scaledWidth, scaledHeight, null)
      }
    }

// Hilfsmethode um die durchschnittliche Farbe des Bildes zu ermitteln
    private def getAverageColorFromImage(img: Image): Color = {
      val bufferedImage = img match {
        case bi: java.awt.image.BufferedImage => bi
        case _ => {
          val bi = new java.awt.image.BufferedImage(
            img.getWidth(null),
            img.getHeight(null),
            java.awt.image.BufferedImage.TYPE_INT_ARGB
          )
          val g = bi.createGraphics()
          g.drawImage(img, 0, 0, size.width, size.height, null)
          g.dispose()
          bi
        }
      }

      var sumR, sumG, sumB = 0L
      var count = 0

      // Sample nur einige Pixel für bessere Performance
      val sampleStep = 10
      for (y <- 0 until bufferedImage.getHeight by sampleStep) {
        for (x <- 0 until bufferedImage.getWidth by sampleStep) {
          val rgb = bufferedImage.getRGB(x, y)
          sumR += (rgb >> 16) & 0xff
          sumG += (rgb >> 8) & 0xff
          sumB += rgb & 0xff
          count += 1
        }
      }

      new Color(
        (sumR / count).toInt,
        (sumG / count).toInt,
        (sumB / count).toInt
      )
    }

    private def drawConfigScreen(g: Graphics2D): Unit = {
      val width = size.width
      val height = size.height

      g.setColor(backgroundColor)
      g.fillRect(0, 0, width, height)

      g.setColor(blackColor)
      g.setFont(new Font("Arial", Font.BOLD, 16))

      val centerX = width / 2
      val startY = height / 3

      // Players
      g.setColor(redColor)
      g.drawString("Players", centerX - 100, startY)
      g.drawString("↑ W", centerX - 100, startY + 20)
      g.setFont(new Font("Arial", Font.BOLD, 24))
      g.drawString(
        controller.manager.getPlayerCount.toString,
        centerX - 95,
        startY + 50
      )
      g.setFont(new Font("Arial", Font.BOLD, 16))
      g.drawString("↓ S", centerX - 100, startY + 70)

      // Figures
      g.setColor(greenColor)
      g.drawString("Figures", centerX - 20, startY)
      g.drawString("↑ E", centerX - 15, startY + 20)
      g.setFont(new Font("Arial", Font.BOLD, 24))
      g.drawString(
        controller.manager.getFigureCount.toString,
        centerX - 10,
        startY + 50
      )
      g.setFont(new Font("Arial", Font.BOLD, 16))
      g.drawString("↓ D", centerX - 15, startY + 70)

      // Board Size
      g.setColor(blueColor)
      g.drawString("Board Size", centerX + 60, startY)
      g.drawString("↑ R", centerX + 75, startY + 20)
      g.setFont(new Font("Arial", Font.BOLD, 24))
      g.drawString(
        controller.manager.getBoardSize.toString,
        centerX + 80,
        startY + 50
      )
      g.setFont(new Font("Arial", Font.BOLD, 16))
      g.drawString("↓ F", centerX + 75, startY + 70)

      // Start button
      g.setColor(blackColor)
      g.setFont(new Font("Arial", Font.BOLD, 18))
      val startMessage = "Press Space to start game"
      val startX = (width - g.getFontMetrics.stringWidth(startMessage)) / 2
      g.drawString(startMessage, startX, startY + 120)
    }

    private def drawGameBoard(g: Graphics2D): Unit = {
      val width = size.width
      val height = size.height
      val board = controller.manager.board
      val players = controller.manager.players
      val selectedFigure = controller.manager.selectedFigure
      val currentPlayer = controller.manager.getCurrentPlayer

      g.setColor(whiteColor)
      g.fillRect(0, 0, width, height)

      // Draw home areas - Positionen an kleineres Fenster angepasst
      drawHomeAreas(g, players, selectedFigure, currentPlayer)

      // Draw main track
      drawMainTrack(g, board.size, players, selectedFigure, currentPlayer)

      // Draw goal areas
      drawGoalAreas(g, players, selectedFigure, currentPlayer)

      // Draw game info
      drawGameInfo(g, currentPlayer, controller.manager.rolled, selectedFigure)
    }

    private def drawHomeAreas(
        g: Graphics2D,
        players: List[Player],
        selectedFigure: Int,
        currentPlayer: Int
    ): Unit = {
      // Angepasste Positionen für kleineres Fenster
      val homePositions = Array(
        (30, 30), // Red (top-left)
        (350, 30), // Blue (top-right)
        (350, 280), // Yellow (bottom-right)
        (30, 280) // Green (bottom-left)
      )

      players.zipWithIndex.foreach { case (player, idx) =>
        if (idx < homePositions.length) {
          val (x, y) = homePositions(idx)
          val playerColor = getPlayerColor(player.color)

          // Draw home area background
          g.setColor(playerColor)
          g.fillRoundRect(x, y, 120, 80, 10, 10)
          g.setColor(blackColor)
          g.drawRoundRect(x, y, 120, 80, 10, 10)

          // Draw figures in home
          player.figures.zipWithIndex.foreach { case (figure, figIdx) =>
            figure.adjustedIndex(controller.manager.board.size) match {
              case Position.Home(_) =>
                val figX = x + 20 + (figIdx % 2) * 40
                val figY = y + 20 + (figIdx / 2) * 30

                val isSelected =
                  player.id - 1 == currentPlayer && figure.id == selectedFigure + 1
                drawFigure(g, figX, figY, playerColor, figure.id, isSelected)
              case _ =>
            }
          }

          // Draw player label
          g.setColor(blackColor)
          g.setFont(new Font("Arial", Font.BOLD, 12))
          g.drawString(player.color.toString, x + 5, y + 15)
        }
      }
    }

    private def drawMainTrack(
        g: Graphics2D,
        boardSize: Int,
        players: List[Player],
        selectedFigure: Int,
        currentPlayer: Int
    ): Unit = {
      val centerX = size.width / 2
      val centerY = size.height / 2
      val trackRadius = 120 // Kleinerer Radius für kleineres Fenster
      val totalFields = boardSize * 4

      for (i <- 0 until totalFields) {
        val angle = (i * 360.0 / totalFields) * Math.PI / 180.0
        val x = (centerX + trackRadius * Math.cos(angle) - 12).toInt
        val y = (centerY + trackRadius * Math.sin(angle) - 12).toInt

        // Check if this is a start position
        val isStartPosition = players.exists(_.startPosition(boardSize) == i)
        val startPlayer = players.find(_.startPosition(boardSize) == i)

        if (isStartPosition && startPlayer.isDefined) {
          g.setColor(getPlayerColor(startPlayer.get.color))
          g.fillOval(x, y, 24, 24) // Kleinere Felder
        } else {
          g.setColor(Color.LIGHT_GRAY)
          g.fillOval(x, y, 24, 24)
        }

        g.setColor(blackColor)
        g.drawOval(x, y, 24, 24)

        // Check if there's a figure on this position
        players.flatMap(_.figures).foreach { figure =>
          figure.adjustedIndex(boardSize) match {
            case Position.Normal(pos) if pos == i =>
              val isSelected =
                figure.owner.id - 1 == currentPlayer && figure.id == selectedFigure + 1
              drawFigure(
                g,
                x + 12,
                y + 12,
                getPlayerColor(figure.owner.color),
                figure.id,
                isSelected
              )
            case _ =>
          }
        }
      }
    }

    private def drawGoalAreas(
        g: Graphics2D,
        players: List[Player],
        selectedFigure: Int,
        currentPlayer: Int
    ): Unit = {
      // Angepasste Positionen für kleineres Fenster
      val goalPositions = Array(
        (180, 180), // Red
        (320, 180), // Blue
        (320, 240), // Yellow
        (180, 240) // Green
      )

      players.zipWithIndex.foreach { case (player, idx) =>
        if (idx < goalPositions.length) {
          val (startX, startY) = goalPositions(idx)
          val playerColor = getPlayerColor(player.color)

          // Draw goal lane
          for (i <- 0 until player.figures.size) {
            val x = startX + (i % 2) * 25
            val y = startY + (i / 2) * 25

            g.setColor(playerColor.brighter())
            g.fillOval(x, y, 20, 20)
            g.setColor(blackColor)
            g.drawOval(x, y, 20, 20)

            // Check if there's a figure in this goal position
            player.figures.foreach { figure =>
              figure.adjustedIndex(controller.manager.board.size) match {
                case Position.Goal(goalIndex) if goalIndex == i =>
                  val isSelected =
                    player.id - 1 == currentPlayer && figure.id == selectedFigure + 1
                  drawFigure(
                    g,
                    x + 10,
                    y + 10,
                    playerColor,
                    figure.id,
                    isSelected
                  )
                case _ =>
              }
            }
          }
        }
      }
    }

    private def drawGameInfo(
        g: Graphics2D,
        currentPlayer: Int,
        rolled: Int,
        selectedFigure: Int
    ): Unit = {
      g.setColor(blackColor)
      g.setFont(new Font("Arial", Font.BOLD, 14))

      val info =
        s"Player ${currentPlayer + 1} | Selected: ${selectedFigure + 1} | Rolled: $rolled"
      g.drawString(info, 10, size.height - 10)
    }

    private def drawFigure(
        g: Graphics2D,
        x: Int,
        y: Int,
        color: Color,
        id: Int,
        isSelected: Boolean
    ): Unit = {
      if (isSelected) {
        g.setColor(Color.WHITE)
        g.fillOval(x - 12, y - 12, 24, 24)
        g.setColor(Color.BLACK)
        g.drawOval(x - 12, y - 12, 24, 24)
      }

      g.setColor(color)
      g.fillOval(x - 8, y - 8, 16, 16)
      g.setColor(blackColor)
      g.drawOval(x - 8, y - 8, 16, 16)

      g.setColor(Color.WHITE)
      g.setFont(new Font("Arial", Font.BOLD, 10))
      val fm = g.getFontMetrics
      val textWidth = fm.stringWidth(id.toString)
      g.drawString(id.toString, x - textWidth / 2, y + 4)
    }

    private def getPlayerColor(color: de.htwg.se.MAEDN.util.Color): Color =
      color match {
        case de.htwg.se.MAEDN.util.Color.RED    => redColor
        case de.htwg.se.MAEDN.util.Color.BLUE   => blueColor
        case de.htwg.se.MAEDN.util.Color.YELLOW => yellowColor
        case de.htwg.se.MAEDN.util.Color.GREEN  => greenColor
        case de.htwg.se.MAEDN.util.Color.WHITE  => whiteColor
      }
  }

  // Control panel with buttons
  class ControlPanel extends FlowPanel {
    background = backgroundColor

    private val playButton = new Button("Play Next (X)") {
      reactions += { case ButtonClicked(_) =>
        controller.executeCommand(PlayNextCommand(controller))
      }
    }

    private val upButton = new Button("Move Up (W)") {
      reactions += { case ButtonClicked(_) =>
        controller.executeCommand(MoveUpCommand(controller))
      }
    }

    private val downButton = new Button("Move Down (S)") {
      reactions += { case ButtonClicked(_) =>
        controller.executeCommand(MoveDownCommand(controller))
      }
    }

    private val newGameButton = new Button("New Game (N)") {
      reactions += { case ButtonClicked(_) =>
        controller.executeCommand(StartGameCommand(controller))
      }
    }

    private val undoButton = new Button("Undo (U)") {
      reactions += { case ButtonClicked(_) =>
        controller.executeCommand(UndoCommand(controller))
      }
    }

    private val redoButton = new Button("Redo (I)") {
      reactions += { case ButtonClicked(_) =>
        controller.executeCommand(RedoCommand(controller))
      }
    }

    private val quitButton = new Button("Quit (Q)") {
      reactions += { case ButtonClicked(_) =>
        controller.executeCommand(QuitGameCommand(controller))
      }
    }

    contents += playButton
    contents += upButton
    contents += downButton
    contents += newGameButton
    contents += undoButton
    contents += redoButton
    contents += quitButton
  }

  // Observer implementation
  override def processEvent(event: Event): Unit = {
    SwingUtilities.invokeLater(new Runnable {
      def run(): Unit = {
        event match {
          case Event.StartGameEvent =>
            statusLabel.text = "Game started!"
            titleLabel.foreground = greenColor

          case Event.ConfigEvent =>
            statusLabel.text = "Configure your game settings"
            titleLabel.foreground = yellowColor

          case Event.PlayNextEvent(playerId) =>
            statusLabel.text = s"Player ${playerId + 1}'s turn!"

          case Event.PlayDiceEvent(rolled) =>
            statusLabel.text = if (rolled == 6) {
              s"You rolled a $rolled! Select a figure and move."
            } else {
              s"You rolled a $rolled!"
            }

          case Event.MoveFigureEvent(figureId) =>
            statusLabel.text = s"Figure $figureId moved!"

          case Event.ChangeSelectedFigureEvent(figureId) =>
            statusLabel.text = s"Selected figure ${figureId + 1}"

          case Event.UndoEvent =>
            statusLabel.text = "Undo executed!"

          case Event.RedoEvent =>
            statusLabel.text = "Redo executed!"

          case Event.BackToMenuEvent =>
            statusLabel.text = "Back to main menu"
            titleLabel.foreground = redColor

          case Event.ErrorEvent(message) =>
            statusLabel.text = s"Error: $message"
            statusLabel.foreground = redColor

          case Event.QuitGameEvent =>
            dispose()
            sys.exit(0)

          case _ =>
        }

        // Reset status label color if it was red
        if (
          statusLabel.foreground == redColor && !event
            .isInstanceOf[Event.ErrorEvent]
        ) {
          statusLabel.foreground = blackColor
        }

        gamePanel.repaint()
      }
    })
  }
}
