package de.htwg.se.MAEDN.aview.gui

import de.htwg.se.MAEDN.util.{NodeFinder, Color, Position}
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.controller.IController

import javafx.scene.{Parent, Node}
import javafx.scene.control.Label
import javafx.scene.layout.{GridPane, VBox, HBox, StackPane}
import javafx.scene.shape.{Circle, Rectangle}
import javafx.scene.paint.Paint
import javafx.geometry.{Insets, Pos}
import javafx.application.Platform

/** Dynamic GUI renderer for Mensch ärgere dich nicht game. Updates all GUI
  * elements based on the current game state.
  */
object DynamicRenderer {

  private val FIGURE_SIZE = 20.0
  private val BOARD_CELL_SIZE = 30.0

  /** Position case class for board coordinates */
  case class BoardPos(r: Int, c: Int)

  /** Generates a central cross path for any size (8-12 recommended) */
  private def generateCrossPath(
      size: Int,
      gridSize: Int = 13
  ): Seq[BoardPos] = {
    val offset = (gridSize - size) / 2
    val normalizedSize = if (size % 2 == 1) size + 1 else size
    val half = normalizedSize / 2
    val path = scala.collection.mutable.ArrayBuffer.empty[BoardPos]
    val segmentFields = half - 1

    // The four blank fields for size 9 and 11
    val blankFields: Set[BoardPos] = size match {
      case 9 =>
        Set(BoardPos(6, 1), BoardPos(1, 6), BoardPos(6, 11), BoardPos(11, 6))
      case 11 =>
        Set(BoardPos(6, 0), BoardPos(0, 6), BoardPos(6, 12), BoardPos(12, 6))
      case _ => Set.empty
    }

    def add(pos: BoardPos): Unit = if (!blankFields.contains(pos)) path += pos

    // --- Part 1: Start bottom, go up, go left, corner ---
    val startRow1 = (normalizedSize match {
      case 8  => 10
      case 10 => 11
      case 12 => 12
      case _ =>
        throw new IllegalArgumentException(
          "Only size 8, 10, 12 (or 9 like 10, 11 like 12) allowed!"
        )
    })
    val startCol1 = 5
    add(BoardPos(startRow1, startCol1))
    for (i <- 1 to segmentFields) add(BoardPos(startRow1 - i, startCol1))
    val leftRow1 = startRow1 - segmentFields
    for (i <- 1 to segmentFields) add(BoardPos(leftRow1, startCol1 - i))

    // Connection field/corner
    val cornerRow1 = leftRow1 - 1
    val cornerCol1 = startCol1 - segmentFields
    add(BoardPos(cornerRow1, cornerCol1))

    // --- Part 2: New start field ---
    val startRow2 = cornerRow1 - 1
    val startCol2 = cornerCol1
    add(BoardPos(startRow2, startCol2))
    for (i <- 1 to segmentFields) add(BoardPos(startRow2, startCol2 + i))
    val rightCol2 = startCol2 + segmentFields

    // --- Part 3: Go up in the same column ---
    val startRow3 = startRow2
    val startCol3 = rightCol2
    for (i <- 1 to segmentFields) add(BoardPos(startRow3 - i, startCol3))

    add(BoardPos(startRow3 - segmentFields, startCol3 + 1))

    // --- Part 4: New start field ---
    val startRow4 = startRow3 - segmentFields
    val startCol4 = startCol3 + 2
    add(BoardPos(startRow4, startCol4))
    for (i <- 1 to segmentFields) add(BoardPos(startRow4 + i, startCol4))

    // --- Part 5: Fill to the right ---
    val rightRow3 = startRow4 + segmentFields
    val rightCol3 = startCol4
    for (i <- 1 to segmentFields) add(BoardPos(rightRow3, rightCol3 + i))

    add(BoardPos(rightRow3 + 1, rightCol3 + segmentFields))

    // --- Part 6: New start field ---
    val startRow5 = rightRow3 + 2
    val startCol5 = rightCol3 + segmentFields
    add(BoardPos(startRow5, startCol5))
    for (i <- 1 to segmentFields) add(BoardPos(startRow5, startCol5 - i))

    // --- Part 7: Fill downwards ---
    val downStartRow = startRow5
    val downCol = startCol5 - segmentFields
    for (i <- 1 to segmentFields) add(BoardPos(downStartRow + i, downCol))

    // --- Part 8: Final field to the left ---
    val finalRow = downStartRow + segmentFields
    val finalColStart = downCol
    add(BoardPos(finalRow, finalColStart - 1))

    path.toSeq
  }

  /** Updates the entire GUI rendering based on current controller state
    */
  def updateRender(controller: IController, sceneRoot: Parent): Unit = {
    Platform.runLater(() => {
      try {
        val manager = controller.manager
        if (manager.state == State.Running) {
          renderPlayerStatus(manager, sceneRoot)
          renderCurrentPlayer(manager, sceneRoot)
          renderDiceResult(manager, sceneRoot)
          renderStartFields(manager, sceneRoot)
          renderGoalPaths(manager, sceneRoot)
          renderMainGameBoard(manager, sceneRoot)
          updatePlayerAreaVisibility(manager, sceneRoot)
        }
      } catch {
        case e: Exception =>
          println(s"Error updating GUI render: ${e.getMessage}")
          e.printStackTrace()
      }
    })
  }

  /** Renders player status information (figures in goal)
    */
  private def renderPlayerStatus(manager: IManager, sceneRoot: Parent): Unit = {
    manager.players.foreach { player =>
      val statusId = player.color match {
        case Color.RED    => "redPlayerStatus"
        case Color.BLUE   => "bluePlayerStatus"
        case Color.GREEN  => "greenPlayerStatus"
        case Color.YELLOW => "yellowPlayerStatus"
        case _            => ""
      }

      if (statusId.nonEmpty) {
        NodeFinder.findNodeById(sceneRoot, statusId) match {
          case Some(label: Label) =>
            val figuresInGoal = player.figures.count { figure =>
              figure.adjustedIndex(manager.board.size) match {
                case Position.Goal(_) => true
                case _                => false
              }
            }
            label.setText(s"$figuresInGoal/${player.figures.size} im Ziel")
          case _ => // Node not found or wrong type
        }
      }
    }
  }

  /** Renders current player indicator
    */
  private def renderCurrentPlayer(
      manager: IManager,
      sceneRoot: Parent
  ): Unit = {
    // Update current player label
    NodeFinder.findNodeById(sceneRoot, "currentPlayerLabel") match {
      case Some(label: Label) =>
        val currentPlayer = manager.players(manager.getCurrentPlayer)
        val colorName = currentPlayer.color.toString.toLowerCase.capitalize
        label.setText(s"$colorName ist am Zug")
      case _ =>
    }

    // Update turn indicator color
    NodeFinder.findNodeById(sceneRoot, "turnIndicator") match {
      case Some(circle: Circle) =>
        val currentColor = manager.players(manager.getCurrentPlayer).color
        circle.setFill(getColorPaint(currentColor))
      case _ =>
    }
  }

  /** Renders dice result and updates dice button state
    */
  private def renderDiceResult(manager: IManager, sceneRoot: Parent): Unit = {
    // Update dice result label
    NodeFinder.findNodeById(sceneRoot, "diceResultLabel") match {
      case Some(label: Label) =>
        if (manager.rolled > 0) {
          label.setText(manager.rolled.toString)
        } else {
          label.setText("---")
        }
      case _ =>
    }

    // Update dice button state
    updateDiceButtonState(manager, sceneRoot)
  }

  /** Updates the dice button enabled/disabled state based on whether player can
    * make moves
    */
  private def updateDiceButtonState(
      manager: IManager,
      sceneRoot: Parent
  ): Unit = {
    NodeFinder.findNodeById(sceneRoot, "diceButton") match {
      case Some(button: javafx.scene.control.Button) =>
        val currentPlayer = manager.players(manager.getCurrentPlayer)
        val canPlayerMove = manager.board.checkIfMoveIsPossible(
          manager.players.flatMap(_.figures),
          manager.rolled,
          currentPlayer.color
        )

        // Disable button if rolled > 0 and player cannot move
        // Enable button if rolled == 0 (ready to roll dice)
        val shouldEnable = manager.rolled == 0 || canPlayerMove
        button.setDisable(!shouldEnable)
      case _ =>
    }
  }

  /** Renders start fields for all players
    */
  private def renderStartFields(manager: IManager, sceneRoot: Parent): Unit = {
    manager.players.foreach { player =>
      val gridId = player.color match {
        case Color.RED    => "redStartGridPane"
        case Color.BLUE   => "blueStartGridPane"
        case Color.GREEN  => "greenStartGridPane"
        case Color.YELLOW => "yellowStartGridPane"
        case _            => ""
      }

      if (gridId.nonEmpty) {
        NodeFinder.findNodeById(sceneRoot, gridId) match {
          case Some(gridPane: GridPane) =>
            renderStartFieldGrid(
              player,
              gridPane,
              manager.selectedFigure,
              manager.getCurrentPlayer == player.id - 1
            )
          case _ => // Node not found or wrong type
        }
      }
    }
  }

  /** Renders goal paths for all players
    */
  private def renderGoalPaths(manager: IManager, sceneRoot: Parent): Unit = {
    manager.players.foreach { player =>
      val pathId = player.color match {
        case Color.RED    => "redGoalGridPane"
        case Color.BLUE   => "blueGoalGridPane"
        case Color.GREEN  => "greenGoalGridPane"
        case Color.YELLOW => "yellowGoalGridPane"
        case _            => ""
      }

      if (pathId.nonEmpty) {
        NodeFinder.findNodeById(sceneRoot, pathId) match {
          case Some(gridPane: GridPane) =>
            renderGoalPathGridPane(
              player,
              gridPane,
              manager.board.size,
              manager.selectedFigure,
              manager.getCurrentPlayer == player.id - 1
            )
          case Some(vbox: VBox) =>
            // Fallback for VBox containers - convert to horizontal layout
            renderGoalPathVBoxHorizontal(
              player,
              vbox,
              manager.board.size,
              manager.selectedFigure,
              manager.getCurrentPlayer == player.id - 1
            )
          case _ => // Node not found or wrong type
        }
      }
    }
  }

  /** Renders the main game board
    */
  private def renderMainGameBoard(
      manager: IManager,
      sceneRoot: Parent
  ): Unit = {
    NodeFinder.findNodeById(sceneRoot, "gameBoardGridPane") match {
      case Some(gridPane: GridPane) =>
        renderGameBoardGrid(manager, gridPane)
      case _ => // Node not found or wrong type
    }
  }

  /** Updates visibility of player areas based on number of active players
    */
  private def updatePlayerAreaVisibility(
      manager: IManager,
      sceneRoot: Parent
  ): Unit = {
    val allColors = List(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW)
    val activeColors = manager.players.map(_.color).toSet

    allColors.foreach { color =>
      val isActive = activeColors.contains(color)

      // Update start VBox visibility (contains the start fields)
      val startVBoxId = color match {
        case Color.RED    => "redStartVBox"
        case Color.BLUE   => "blueStartVBox"
        case Color.GREEN  => "greenStartVBox"
        case Color.YELLOW => "yellowStartVBox"
        case Color.WHITE  => ""
      }

      if (startVBoxId.nonEmpty) {
        NodeFinder.findNodeById(sceneRoot, startVBoxId).foreach { node =>
          node.setVisible(isActive)
          node.setManaged(isActive)
        }
      }

      // Update goal VBox visibility (contains the goal fields)
      val goalVBoxId = color match {
        case Color.RED    => "redGoalVBox"
        case Color.BLUE   => "blueGoalVBox"
        case Color.GREEN  => "greenGoalVBox"
        case Color.YELLOW => "yellowGoalVBox"
        case Color.WHITE  => ""
      }

      if (goalVBoxId.nonEmpty) {
        NodeFinder.findNodeById(sceneRoot, goalVBoxId).foreach { node =>
          node.setVisible(isActive)
          node.setManaged(isActive)
        }
      }

      // Update status HBox visibility (contains player status info)
      val statusHBoxId = color match {
        case Color.RED    => "redStatusHBox"
        case Color.BLUE   => "blueStatusHBox"
        case Color.GREEN  => "greenStatusHBox"
        case Color.YELLOW => "yellowStatusHBox"
        case Color.WHITE  => ""
      }

      if (statusHBoxId.nonEmpty) {
        NodeFinder.findNodeById(sceneRoot, statusHBoxId).foreach { node =>
          node.setVisible(isActive)
          node.setManaged(isActive)
        }
      }
    }
  }

  /** Renders a start field grid for a specific player
    */
  private def renderStartFieldGrid(
      player: IPlayer,
      gridPane: GridPane,
      selectedFigure: Int,
      isCurrentPlayer: Boolean
  ): Unit = {
    gridPane.getChildren.clear()

    // Create horizontal 1x4 grid for 4 figures
    player.figures.zipWithIndex.foreach { case (figure, index) =>
      val row = 0 // All figures in the same row for horizontal layout
      val col = index // Each figure in a different column

      figure.adjustedIndex(0) match {
        case Position.Home(_) =>
          val figureNode = createFigureNode(
            player.color,
            figure.id,
            isSelected = isCurrentPlayer && figure.id == selectedFigure + 1
          )
          gridPane.add(figureNode, col, row)
        case _ =>
          // Add empty space
          val emptyNode = createEmptyFieldNode()
          gridPane.add(emptyNode, col, row)
      }
    }
  }

  /** Renders a goal path VBox for a specific player
    */
  private def renderGoalPathVBox(
      player: IPlayer,
      vbox: VBox,
      boardSize: Int,
      selectedFigure: Int,
      isCurrentPlayer: Boolean
  ): Unit = {
    vbox.getChildren
      .clear() // Create goal fields (typically 4 fields leading to the finish)
    (0 until player.figures.size).foreach { goalIndex =>
      val figureInGoal = player.figures.find { figure =>
        figure.adjustedIndex(boardSize) == Position.Goal(goalIndex)
      }

      val fieldNode = figureInGoal match {
        case Some(figure) =>
          createFigureNodeLarge(
            player.color,
            figure.id,
            isSelected = isCurrentPlayer && figure.id == selectedFigure + 1
          )
        case None =>
          createGoalFieldNodeLarge(player.color)
      }

      vbox.getChildren.add(fieldNode)
    }
  }

  /** Renders a goal path GridPane for a specific player (horizontal layout)
    */
  private def renderGoalPathGridPane(
      player: IPlayer,
      gridPane: GridPane,
      boardSize: Int,
      selectedFigure: Int,
      isCurrentPlayer: Boolean
  ): Unit = {
    gridPane.getChildren.clear()

    // Create goal fields horizontally (typically 4 fields leading to the finish)
    (0 until player.figures.size).foreach { goalIndex =>
      val figureInGoal = player.figures.find { figure =>
        figure.adjustedIndex(boardSize) == Position.Goal(goalIndex)
      }

      val fieldNode = figureInGoal match {
        case Some(figure) =>
          createFigureNode(
            player.color,
            figure.id,
            isSelected = isCurrentPlayer && figure.id == selectedFigure + 1
          )
        case None =>
          createGoalFieldNode(player.color)
      }

      gridPane.add(
        fieldNode,
        goalIndex,
        0
      ) // Add horizontally (col=goalIndex, row=0)
    }
  }

  /** Renders a goal path VBox with horizontal arrangement
    */
  private def renderGoalPathVBoxHorizontal(
      player: IPlayer,
      vbox: VBox,
      boardSize: Int,
      selectedFigure: Int,
      isCurrentPlayer: Boolean
  ): Unit = {
    vbox.getChildren.clear()

    // Create an HBox to arrange goal fields horizontally within the VBox
    val hbox = new HBox()
    hbox.setSpacing(2)
    hbox.setAlignment(Pos.CENTER)

    // Create goal fields (typically 4 fields leading to the finish)
    (0 until player.figures.size).foreach { goalIndex =>
      val figureInGoal = player.figures.find { figure =>
        figure.adjustedIndex(boardSize) == Position.Goal(goalIndex)
      }

      val fieldNode = figureInGoal match {
        case Some(figure) =>
          createFigureNode(
            player.color,
            figure.id,
            isSelected = isCurrentPlayer && figure.id == selectedFigure + 1
          )
        case None =>
          createGoalFieldNode(player.color)
      }

      hbox.getChildren.add(fieldNode)
    }

    vbox.getChildren.add(hbox)
  }

  /** Renders the main game board grid using cross-path layout
    */
  private def renderGameBoardGrid(
      manager: IManager,
      gridPane: GridPane
  ): Unit = {
    gridPane.getChildren.clear()

    // Remove any existing gap settings to let it fit naturally
    gridPane.setHgap(0)
    gridPane.setVgap(0)

    val boardSize = manager.board.size
    val gridSize = 13

    val crossPath = generateCrossPath(boardSize, gridSize)

    crossPath.zipWithIndex.foreach { case (pos, fieldIndex) =>
      val stackPane = new StackPane()
      stackPane.setPrefSize(BOARD_CELL_SIZE, BOARD_CELL_SIZE)

      // Find figure on this field
      val figureOnField = manager.players.flatMap(_.figures).find { figure =>
        figure.adjustedIndex(boardSize) == Position.Normal(fieldIndex)
      }

      val fieldNode = figureOnField match {
        case Some(figure) =>
          createFigureNode(
            figure.owner.color,
            figure.id,
            isSelected = manager.getCurrentPlayer == figure.owner.id - 1 &&
              figure.id == manager.selectedFigure + 1
          )
        case None =>
          // Check if this is a start field (every 1/4 of the board)
          val totalFields = boardSize * 4
          val quarterSize = totalFields / 4
          val isStartField = fieldIndex % quarterSize == 0
          if (isStartField) {
            val playerIndex = fieldIndex / quarterSize
            val startPlayer = if (playerIndex < manager.players.length) {
              Some(manager.players(playerIndex))
            } else {
              None
            }
            startPlayer
              .map(player => createStartFieldNode(player.color))
              .getOrElse(createNormalFieldNode())
          } else {
            createNormalFieldNode()
          }
      }

      stackPane.getChildren.add(fieldNode)
      gridPane.add(stackPane, pos.c, pos.r)
    }
  }

  /** Creates a figure node (circle representing a game piece)
    */
  private def createFigureNode(
      color: Color,
      figureId: Int,
      isSelected: Boolean
  ): Node = {
    val circle = new Circle(FIGURE_SIZE / 2)
    circle.setFill(getColorPaint(color))

    if (isSelected) {
      circle.setStroke(Paint.valueOf("#FFD700")) // Gold border for selected
      circle.setStrokeWidth(3.0)
    } else {
      circle.setStroke(Paint.valueOf("#000000"))
      circle.setStrokeWidth(1.0)
    }

    // Add figure ID as tooltip or accessible text
    circle.setAccessibleText(s"Figure $figureId")
    circle
  }

  /** Creates a larger figure node for goal areas (horizontal rectangle with
    * circle)
    */
  private def createFigureNodeLarge(
      color: Color,
      figureId: Int,
      isSelected: Boolean
  ): Node = {
    val rect = new Rectangle(BOARD_CELL_SIZE * 1.5, BOARD_CELL_SIZE)
    rect.setFill(getColorPaint(color))

    if (isSelected) {
      rect.setStroke(Paint.valueOf("#FFD700")) // Gold border for selected
      rect.setStrokeWidth(3.0)
    } else {
      rect.setStroke(Paint.valueOf("#000000"))
      rect.setStrokeWidth(1.0)
    }

    // Add figure ID as tooltip or accessible text
    rect.setAccessibleText(s"Figure $figureId")
    rect
  }

  /** Creates an empty field node
    */
  private def createEmptyFieldNode(): Node = {
    val rect = new Rectangle(FIGURE_SIZE, FIGURE_SIZE)
    rect.setFill(Paint.valueOf("#F0F0F0"))
    rect.setStroke(Paint.valueOf("#CCCCCC"))
    rect.setStrokeWidth(1.0)
    rect
  }

  /** Creates a goal field node (empty goal slot)
    */
  private def createGoalFieldNode(color: Color): Node = {
    val rect = new Rectangle(FIGURE_SIZE, FIGURE_SIZE)
    rect.setFill(getLightColorPaint(color))
    rect.setStroke(getColorPaint(color))
    rect.setStrokeWidth(2.0)
    rect
  }

  /** Creates a larger goal field node (horizontal rectangle for goal areas)
    */
  private def createGoalFieldNodeLarge(color: Color): Node = {
    val rect = new Rectangle(BOARD_CELL_SIZE * 1.5, BOARD_CELL_SIZE)
    rect.setFill(getLightColorPaint(color))
    rect.setStroke(getColorPaint(color))
    rect.setStrokeWidth(2.0)
    rect
  }

  /** Creates a start field node
    */
  private def createStartFieldNode(color: Color): Node = {
    val rect = new Rectangle(BOARD_CELL_SIZE, BOARD_CELL_SIZE)
    rect.setFill(getLightColorPaint(color))
    rect.setStroke(getColorPaint(color))
    rect.setStrokeWidth(2.0)
    rect
  }

  /** Creates a normal field node
    */
  private def createNormalFieldNode(): Node = {
    val rect = new Rectangle(BOARD_CELL_SIZE, BOARD_CELL_SIZE)
    rect.setFill(Paint.valueOf("#FFFFFF"))
    rect.setStroke(Paint.valueOf("#8B4513"))
    rect.setStrokeWidth(1.0)
    rect
  }

  /** Gets the paint color for a player color
    */
  private def getColorPaint(color: Color): Paint = color match {
    case Color.RED    => Paint.valueOf("#FF6B6B")
    case Color.BLUE   => Paint.valueOf("#4ECDC4")
    case Color.GREEN  => Paint.valueOf("#95E1A3")
    case Color.YELLOW => Paint.valueOf("#FFE66D")
    case Color.WHITE  => Paint.valueOf("#FFFFFF")
  }

  /** Gets a lighter version of the color for backgrounds
    */
  private def getLightColorPaint(color: Color): Paint = color match {
    case Color.RED    => Paint.valueOf("#FFE4E1")
    case Color.BLUE   => Paint.valueOf("#E0F6FF")
    case Color.GREEN  => Paint.valueOf("#F0FFF0")
    case Color.YELLOW => Paint.valueOf("#FFFACD")
    case Color.WHITE  => Paint.valueOf("#F8F8F8")
  }
}
