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

  // Colors
  private val backgroundColor = new Color(245, 245, 245)
  private val redColor = new Color(220, 53, 69)
  private val blueColor = new Color(0, 123, 255)
  private val yellowColor = new Color(255, 193, 7)
  private val greenColor = new Color(40, 167, 69)
  private val whiteColor = new Color(255, 255, 255)
  private val blackColor = new Color(33, 37, 41)

  // Layout components
  private val titleLabel = new Label("Mensch ärgere dich nicht") {
    font = new Font("Arial", Font.BOLD, 24)
    foreground = redColor
  }

  private val statusLabel = new Label("Welcome to the game!") {
    font = new Font("Arial", Font.PLAIN, 14)
    horizontalAlignment = Alignment.Center
  }

  private val gamePanel = new GameBoardPanel()
  private val controlPanel = new ControlPanel()

  // Main layout
  contents = new BorderPanel {
    background = backgroundColor
    layout(titleLabel) = BorderPanel.Position.North
    layout(gamePanel) = BorderPanel.Position.Center
    layout(controlPanel) = BorderPanel.Position.South
    layout(statusLabel) = BorderPanel.Position.South
  }

  // Window settings
  title = "Mensch ärgere dich nicht"
  size = new Dimension(800, 700)
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
    preferredSize = new Dimension(600, 500)
    background = whiteColor

    override def paintComponent(g: Graphics2D): Unit = {
      super.paintComponent(g)
      g.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
      )

      controller.manager.state match {
        case State.Menu    => drawMenuScreen(g)
        case State.Config  => drawConfigScreen(g)
        case State.Running => drawGameBoard(g)
      }
    }

    private def drawMenuScreen(g: Graphics2D): Unit = {
      val width = size.width
      val height = size.height

      g.setColor(backgroundColor)
      g.fillRect(0, 0, width, height)

      g.setColor(blackColor)
      g.setFont(new Font("Arial", Font.BOLD, 20))
      val message = "Press 'N' or Space to configure game"
      val fm = g.getFontMetrics
      val x = (width - fm.stringWidth(message)) / 2
      val y = height / 2
      g.drawString(message, x, y)

      g.setFont(new Font("Arial", Font.PLAIN, 14))
      val info =
        s"${controller.manager.getPlayerCount} players, ${controller.manager.getFigureCount} figures, ${controller.manager.getBoardSize}x${controller.manager.getBoardSize} board"
      val infoX = (width - g.getFontMetrics.stringWidth(info)) / 2
      g.drawString(info, infoX, y + 30)
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

      // Draw home areas
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
      val homePositions = Array(
        (50, 50), // Red (top-left)
        (450, 50), // Blue (top-right)
        (450, 350), // Yellow (bottom-right)
        (50, 350) // Green (bottom-left)
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
      val trackRadius = 150
      val totalFields = boardSize * 4

      for (i <- 0 until totalFields) {
        val angle = (i * 360.0 / totalFields) * Math.PI / 180.0
        val x = (centerX + trackRadius * Math.cos(angle) - 15).toInt
        val y = (centerY + trackRadius * Math.sin(angle) - 15).toInt

        // Check if this is a start position
        val isStartPosition = players.exists(_.startPosition(boardSize) == i)
        val startPlayer = players.find(_.startPosition(boardSize) == i)

        if (isStartPosition && startPlayer.isDefined) {
          g.setColor(getPlayerColor(startPlayer.get.color))
          g.fillOval(x, y, 30, 30)
        } else {
          g.setColor(Color.LIGHT_GRAY)
          g.fillOval(x, y, 30, 30)
        }

        g.setColor(blackColor)
        g.drawOval(x, y, 30, 30)

        // Check if there's a figure on this position
        players.flatMap(_.figures).foreach { figure =>
          figure.adjustedIndex(boardSize) match {
            case Position.Normal(pos) if pos == i =>
              val isSelected =
                figure.owner.id - 1 == currentPlayer && figure.id == selectedFigure + 1
              drawFigure(
                g,
                x + 15,
                y + 15,
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
      val goalPositions = Array(
        (200, 200), // Red
        (350, 200), // Blue
        (350, 280), // Yellow
        (200, 280) // Green
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
