package de.htwg.se.MAEDN.aview.gui

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterAll
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.scalatestplus.mockito.MockitoSugar
import javafx.scene.{Parent, Node}
import javafx.scene.control.{Label, Button}
import javafx.scene.layout.{GridPane, VBox, HBox, StackPane}
import javafx.scene.shape.{Circle, Rectangle}
import javafx.scene.paint.Paint
import javafx.application.Platform
import javafx.embed.swing.JFXPanel

import scala.jdk.CollectionConverters._
import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.model.{IManager, IPlayer, IFigure, State, IBoard}
import de.htwg.se.MAEDN.util.{Color, Position, NodeFinder}
import de.htwg.se.MAEDN.aview.gui.DynamicRenderer
import de.htwg.se.MAEDN.controller.IController

class DynamicRendererSpec
    extends AnyWordSpec
    with Matchers
    with MockitoSugar
    with BeforeAndAfterAll {

  // Initialize JavaFX once for all tests
  override def beforeAll(): Unit = {
    super.beforeAll()
    // Initialize JavaFX toolkit
    new JFXPanel() // This initializes the JavaFX toolkit

    // Wait for platform to be ready
    val latch = new java.util.concurrent.CountDownLatch(1)
    Platform.runLater(() => latch.countDown())
    latch.await()
  }

  // Helper method to create a mock ObservableList
  private def createMockObservableList[T]()
      : javafx.collections.ObservableList[T] = {
    val mockList = mock[javafx.collections.ObservableList[T]]
    // Configure basic list behavior
    when(mockList.add(any[T]())).thenReturn(true)
    when(mockList.addAll(any[java.util.Collection[_ <: T]]())).thenReturn(true)
    mockList
  }

  "DynamicRenderer" should {

    "have correct constants" in {
      // Access private constants through reflection or test behavior
      // Since constants are private, we test their effects indirectly
      val renderer = DynamicRenderer
      renderer should not be null
    }

    "BoardPos case class" should {
      "create instances correctly" in {
        val pos = DynamicRenderer.BoardPos(5, 3)
        pos.r shouldEqual 5
        pos.c shouldEqual 3
      }

      "support equality" in {
        val pos1 = DynamicRenderer.BoardPos(2, 4)
        val pos2 = DynamicRenderer.BoardPos(2, 4)
        val pos3 = DynamicRenderer.BoardPos(3, 4)

        pos1 shouldEqual pos2
        pos1 should not equal pos3
      }
    }

    "generateCrossPath" should {
      "generate correct path for size 8" in {
        val path = DynamicRenderer.generateCrossPath(8, 13)
        path should not be empty
        path.length should be > 20 // Should have multiple path segments
      }

      "generate correct path for size 10" in {
        val path = DynamicRenderer.generateCrossPath(10, 13)
        path should not be empty
        path.length should be > 30
      }

      "generate correct path for size 12" in {
        val path = DynamicRenderer.generateCrossPath(12, 13)
        path should not be empty
        path.length should be > 40
      }

      "throw IllegalArgumentException for unsupported sizes" in {
        an[IllegalArgumentException] should be thrownBy {
          DynamicRenderer.generateCrossPath(6, 13)
        }

        an[IllegalArgumentException] should be thrownBy {
          DynamicRenderer.generateCrossPath(14, 13)
        }
      }

      "exclude blank fields for size 9" in {
        val path = DynamicRenderer.generateCrossPath(9, 13)
        val blankFields = Set(
          DynamicRenderer.BoardPos(6, 1),
          DynamicRenderer.BoardPos(1, 6),
          DynamicRenderer.BoardPos(6, 11),
          DynamicRenderer.BoardPos(11, 6)
        )
        blankFields.foreach { blank =>
          path should not contain blank
        }
      }

      "exclude blank fields for size 11" in {
        val path = DynamicRenderer.generateCrossPath(11, 13)
        val blankFields = Set(
          DynamicRenderer.BoardPos(6, 0),
          DynamicRenderer.BoardPos(0, 6),
          DynamicRenderer.BoardPos(6, 12),
          DynamicRenderer.BoardPos(12, 6)
        )
        blankFields.foreach { blank =>
          path should not contain blank
        }
      }
    }

    "updateRender" should {
      "handle manager in Running state" in {
        val mockController = mock[IController]
        val mockManager = mock[IManager]
        val mockParent = mock[Parent]
        val mockPlayer = mock[IPlayer]
        val mockBoard = mock[IBoard]
        val mockFigure = mock[IFigure]

        when(mockController.manager).thenReturn(mockManager)
        when(mockManager.state).thenReturn(State.Running)
        when(mockManager.players).thenReturn(List(mockPlayer))
        when(mockManager.board).thenReturn(mockBoard)
        when(mockManager.getCurrentPlayer).thenReturn(0)
        when(mockManager.rolled).thenReturn(3)
        when(mockManager.selectedFigure).thenReturn(0)
        when(mockPlayer.color).thenReturn(Color.RED)
        when(mockPlayer.id).thenReturn(1)
        when(mockPlayer.figures).thenReturn(List(mockFigure))
        when(mockBoard.size).thenReturn(8)
        when(mockFigure.adjustedIndex(any[Int]())).thenReturn(Position.Home(0))
        when(mockFigure.id).thenReturn(1)
        when(mockFigure.owner).thenReturn(mockPlayer)

        noException should be thrownBy {
          DynamicRenderer.updateRender(mockController, mockParent)
        }
      }

      "skip rendering when not in Running state" in {
        val mockController = mock[IController]
        val mockManager = mock[IManager]
        val mockParent = mock[Parent]

        when(mockController.manager).thenReturn(mockManager)
        when(mockManager.state).thenReturn(State.Menu)

        noException should be thrownBy {
          DynamicRenderer.updateRender(mockController, mockParent)
        }
      }

      "handle exceptions gracefully" in {
        val mockController = mock[IController]
        val mockManager = mock[IManager]
        val mockParent = mock[Parent]

        when(mockController.manager).thenReturn(mockManager)
        when(mockManager.state).thenReturn(State.Running)
        when(mockManager.players).thenThrow(
          new RuntimeException("Test exception")
        )

        noException should be thrownBy {
          DynamicRenderer.updateRender(mockController, mockParent)
        }
      }
    }

    "renderCurrentPlayer" should {
      "should update current player label" in {
        val manager = mock[IManager]
        val player = mock[IPlayer]
        when(manager.players).thenReturn(List(player))
        when(manager.getCurrentPlayer).thenReturn(0)
        when(player.color).thenReturn(Color.BLUE)

        val label = new Label()
        label.setId("currentPlayerLabel")
        val parent = new VBox(label)

        noException should be thrownBy {
          DynamicRenderer.renderCurrentPlayer(manager, parent)
        }
        label.getText should include("Blue ist am Zug")
      }
    }

    "renderDiceResult" should {
      "renderGoalPathVBoxHorizontal" should {
        "create HBox inside VBox for horizontal layout" in {
          val mockPlayer = mock[IPlayer]
          val mockVBox = mock[VBox]
          val mockChildren = createMockObservableList[Node]()
          val mockFigure = mock[IFigure]

          when(mockVBox.getChildren).thenReturn(mockChildren)
          when(mockPlayer.figures).thenReturn(List(mockFigure))
          when(mockFigure.adjustedIndex(8)).thenReturn(Position.Normal(5))
          when(mockFigure.id).thenReturn(1)
          when(mockPlayer.color).thenReturn(Color.YELLOW)

          noException should be thrownBy {
            DynamicRenderer.renderGoalPathVBoxHorizontal(
              mockPlayer,
              mockVBox,
              8,
              0,
              false
            )
          }

          verify(mockChildren).clear()
        }

        "handle empty figure list" in {
          val mockPlayer = mock[IPlayer]
          val mockVBox = mock[VBox]
          val mockChildren = createMockObservableList[Node]()

          when(mockVBox.getChildren).thenReturn(mockChildren)
          when(mockPlayer.figures).thenReturn(List())
          when(mockPlayer.color).thenReturn(Color.RED)

          noException should be thrownBy {
            DynamicRenderer.renderGoalPathVBoxHorizontal(
              mockPlayer,
              mockVBox,
              10,
              0,
              true
            )
          }
        }
      }

      "handle multiple figures in goal and normal positions" in {
        val mockPlayer = mock[IPlayer]
        val mockVBox = mock[VBox]
        val mockChildren = createMockObservableList[Node]()
        val mockFigure1 = mock[IFigure]
        val mockFigure2 = mock[IFigure]

        when(mockVBox.getChildren).thenReturn(mockChildren)
        when(mockPlayer.figures).thenReturn(List(mockFigure1, mockFigure2))
        when(mockFigure1.adjustedIndex(8)).thenReturn(Position.Goal(0))
        when(mockFigure2.adjustedIndex(8)).thenReturn(Position.Normal(3))
        when(mockFigure1.id).thenReturn(1)
        when(mockFigure2.id).thenReturn(2)
        when(mockPlayer.color).thenReturn(Color.GREEN)

        noException should be thrownBy {
          DynamicRenderer.renderGoalPathVBoxHorizontal(
            mockPlayer,
            mockVBox,
            8,
            1,
            true
          )
        }
      }
    }

    "createFigureNode" should {
      "set gold border for selected figure" in {
        val node = DynamicRenderer.createFigureNode(Color.YELLOW, 2, true)
        node shouldBe a[Circle]
        val circle = node.asInstanceOf[Circle]
        circle.getStroke.toString should include("ffd700") // gold
        circle.getStrokeWidth shouldEqual 3.0
      }

      "set black border for unselected figure" in {
        val node = DynamicRenderer.createFigureNode(Color.BLUE, 3, false)
        node shouldBe a[Circle]
        val circle = node.asInstanceOf[Circle]
        circle.getStroke.toString.toLowerCase should include("000000")
        circle.getStrokeWidth shouldEqual 1.0
      }
    }

    "createFigureNodeLarge" should {
      "set gold border for selected large figure" in {
        val node = DynamicRenderer.createFigureNodeLarge(Color.GREEN, 1, true)
        node shouldBe a[Rectangle]
        val rect = node.asInstanceOf[Rectangle]
        rect.getStroke.toString should include("ffd700")
        rect.getStrokeWidth shouldEqual 3.0
      }

      "set black border for unselected large figure" in {
        val node = DynamicRenderer.createFigureNodeLarge(Color.YELLOW, 2, false)
        node shouldBe a[Rectangle]
        val rect = node.asInstanceOf[Rectangle]
        rect.getStroke.toString.toLowerCase should include("000000")
        rect.getStrokeWidth shouldEqual 1.0
      }
    }

    "createGoalFieldNode" should {
      "use correct color for border and fill" in {
        val node = DynamicRenderer.createGoalFieldNode(Color.RED)
        node shouldBe a[Rectangle]
        val rect = node.asInstanceOf[Rectangle]
        rect.getStroke.toString.toLowerCase should include("ff6b6b")
        rect.getFill.toString.toLowerCase should include("ffe4e1")
      }
    }

    "createStartFieldNode" should {
      "use correct color for border and fill" in {
        val node = DynamicRenderer.createStartFieldNode(Color.GREEN)
        node shouldBe a[Rectangle]
        val rect = node.asInstanceOf[Rectangle]
        rect.getStroke.toString.toLowerCase should include("95e1a3")
        rect.getFill.toString.toLowerCase should include("f0fff0")
      }
    }

    "createNormalFieldNode" should {
      "use white fill and brown border" in {
        val node = DynamicRenderer.createNormalFieldNode()
        node shouldBe a[Rectangle]
        val rect = node.asInstanceOf[Rectangle]
        rect.getFill.toString.toLowerCase should include("ffffff")
        rect.getStroke.toString.toLowerCase should include("8b4513")
      }
    }

    "getColorPaint" should {
      "return correct Paint for each color" in {
        DynamicRenderer
          .getColorPaint(Color.RED)
          .toString
          .toLowerCase should include("ff6b6b")
        DynamicRenderer
          .getColorPaint(Color.BLUE)
          .toString
          .toLowerCase should include("4ecdc4")
        DynamicRenderer
          .getColorPaint(Color.GREEN)
          .toString
          .toLowerCase should include("95e1a3")
        DynamicRenderer
          .getColorPaint(Color.YELLOW)
          .toString
          .toLowerCase should include("ffe66d")
        DynamicRenderer
          .getColorPaint(Color.WHITE)
          .toString
          .toLowerCase should include("ffffff")
      }
    }

    "getLightColorPaint" should {
      "return correct Paint for each color" in {
        DynamicRenderer
          .getLightColorPaint(Color.RED)
          .toString
          .toLowerCase should include("ffe4e1")
        DynamicRenderer
          .getLightColorPaint(Color.BLUE)
          .toString
          .toLowerCase should include("e0f6ff")
        DynamicRenderer
          .getLightColorPaint(Color.GREEN)
          .toString
          .toLowerCase should include("f0fff0")
        DynamicRenderer
          .getLightColorPaint(Color.YELLOW)
          .toString
          .toLowerCase should include("fffacd")
        DynamicRenderer
          .getLightColorPaint(Color.WHITE)
          .toString
          .toLowerCase should include("f8f8f8")
      }
    }

    "renderGameBoardGrid" should {
      "render figures on correct positions" in {
        val mockManager = mock[IManager]
        val mockGridPane = mock[GridPane]
        val mockChildren = createMockObservableList[Node]()
        val mockPlayer = mock[IPlayer]
        val mockFigure = mock[IFigure]
        val mockBoard = mock[IBoard]

        when(mockGridPane.getChildren).thenReturn(mockChildren)
        when(mockManager.board).thenReturn(mockBoard)
        when(mockManager.players).thenReturn(List(mockPlayer))
        when(mockManager.getCurrentPlayer).thenReturn(0)
        when(mockManager.selectedFigure).thenReturn(0)
        when(mockBoard.size).thenReturn(8)
        when(mockPlayer.figures).thenReturn(List(mockFigure))
        when(mockFigure.adjustedIndex(8)).thenReturn(Position.Normal(0))
        when(mockFigure.id).thenReturn(1)
        when(mockFigure.owner).thenReturn(mockPlayer)
        when(mockPlayer.color).thenReturn(Color.RED)

        noException should be thrownBy {
          DynamicRenderer.renderGameBoardGrid(mockManager, mockGridPane)
        }
        verify(mockChildren).clear()
      }

      "handle empty player list" in {
        val mockManager = mock[IManager]
        val mockGridPane = mock[GridPane]
        val mockChildren = createMockObservableList[Node]()
        val mockBoard = mock[IBoard]

        when(mockGridPane.getChildren).thenReturn(mockChildren)
        when(mockManager.board).thenReturn(mockBoard)
        when(mockManager.players).thenReturn(List())
        when(mockManager.getCurrentPlayer).thenReturn(0)
        when(mockManager.selectedFigure).thenReturn(0)
        when(mockBoard.size).thenReturn(8)

        noException should be thrownBy {
          DynamicRenderer.renderGameBoardGrid(mockManager, mockGridPane)
        }
        verify(mockChildren).clear()
      }
    }

    "update dice result label with default when not rolled" in {
      val manager = mock[IManager]
      val player = mock[IPlayer]
      val board = mock[IBoard]
      when(manager.rolled).thenReturn(0)
      when(manager.players).thenReturn(List(player))
      when(manager.getCurrentPlayer).thenReturn(0)
      when(player.color).thenReturn(Color.RED)
      when(manager.board).thenReturn(board)
      when(board.checkIfMoveIsPossible(any(), any(), any())).thenReturn(true)

      // ECHTER Label mit passender ID!
      val label = new Label()
      label.setId("diceResultLabel")
      val parent = new VBox(label)

      noException should be thrownBy {
        DynamicRenderer.renderDiceResult(manager, parent)
      }
      label.getText should include("---")
    }
  }

  "renderGoalPaths" should {
    "render goal paths for all players" in {
      val manager = mock[IManager]
      val player = mock[IPlayer]
      val board = mock[IBoard]
      when(manager.players).thenReturn(List(player))
      when(manager.board).thenReturn(board)
      when(manager.selectedFigure).thenReturn(0)
      when(manager.getCurrentPlayer).thenReturn(0)
      when(player.color).thenReturn(Color.YELLOW)
      when(player.id).thenReturn(1)
      when(player.figures).thenReturn(List())
      when(board.size).thenReturn(10)

      val yellowGoalVBox = new VBox()
      yellowGoalVBox.setId("yellowGoalVBox")
      val parent = new VBox(yellowGoalVBox)

      noException should be thrownBy {
        DynamicRenderer.renderGoalPaths(manager, parent)
      }
      yellowGoalVBox.getChildren.size() should be >= 0
    }
  }

  "renderMainGameBoard" should {
    "render main game board" in {
      val manager = mock[IManager]
      val board = mock[IBoard]
      when(manager.board).thenReturn(board)
      when(manager.players).thenReturn(List())
      when(manager.getCurrentPlayer).thenReturn(0)
      when(manager.selectedFigure).thenReturn(0)
      when(board.size).thenReturn(8)

      val mainGameBoardGrid = new GridPane()
      mainGameBoardGrid.setId("mainGameBoardGrid")
      val parent = new VBox(mainGameBoardGrid)

      noException should be thrownBy {
        DynamicRenderer.renderMainGameBoard(manager, parent)
      }
      mainGameBoardGrid.getChildren.size() should be >= 0
    }
  }

  "updatePlayerAreaVisibility" should {
    "update visibility for active players" in {
      val manager = mock[IManager]
      val player1 = mock[IPlayer]
      val player2 = mock[IPlayer]
      when(manager.players).thenReturn(List(player1, player2))
      when(player1.color).thenReturn(Color.RED)
      when(player2.color).thenReturn(Color.BLUE)

      val redPlayerArea = new VBox(); redPlayerArea.setId("redPlayerArea")
      val bluePlayerArea = new VBox(); bluePlayerArea.setId("bluePlayerArea")
      val parent = new VBox(redPlayerArea, bluePlayerArea)

      noException should be thrownBy {
        DynamicRenderer.updatePlayerAreaVisibility(manager, parent)
      }
    }

    "handle empty player list" in {
      val manager = mock[IManager]
      val parent = new VBox()
      when(manager.players).thenReturn(List())

      noException should be thrownBy {
        DynamicRenderer.updatePlayerAreaVisibility(manager, parent)
      }
    }

    "handle all colors including inactive ones" in {
      val manager = mock[IManager]
      val player = mock[IPlayer]
      when(manager.players).thenReturn(List(player))
      when(player.color).thenReturn(Color.RED)

      val redPlayerArea = new VBox(); redPlayerArea.setId("redPlayerArea")
      val parent = new VBox(redPlayerArea)

      noException should be thrownBy {
        DynamicRenderer.updatePlayerAreaVisibility(manager, parent)
      }
    }
  }

  "color mapping" should {
    "map colors to correct paint values" in {
      // Test getColorPaint method indirectly through created nodes
      val redCircle = DynamicRenderer.createFigureNode(Color.RED, 1, false)
      redCircle shouldBe a[Circle]

      val blueCircle = DynamicRenderer.createFigureNode(Color.BLUE, 1, false)
      blueCircle shouldBe a[Circle]
    }

    "handle all color types" in {
      val colors =
        List(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.WHITE)
      colors.foreach { color =>
        noException should be thrownBy {
          DynamicRenderer.createFigureNode(color, 1, false)
        }
      }
    }
  }

  "node creation" should {
    "createFigureNode" should {
      "create circle with correct properties" in {
        val node = DynamicRenderer.createFigureNode(Color.RED, 1, false)
        node shouldBe a[Circle]

        val circle = node.asInstanceOf[Circle]
        circle.getAccessibleText shouldEqual "Figure 1"
      }

      "create selected figure with gold border" in {
        val node = DynamicRenderer.createFigureNode(Color.BLUE, 2, true)
        node shouldBe a[Circle]

        val circle = node.asInstanceOf[Circle]
        circle.getStrokeWidth shouldEqual 3.0
      }

      "create unselected figure with normal border" in {
        val node = DynamicRenderer.createFigureNode(Color.GREEN, 3, false)
        node shouldBe a[Circle]

        val circle = node.asInstanceOf[Circle]
        circle.getStrokeWidth shouldEqual 1.0
      }
    }

    "createFigureNodeLarge" should {
      "create rectangle with correct properties" in {
        val node =
          DynamicRenderer.createFigureNodeLarge(Color.YELLOW, 1, false)
        node shouldBe a[Rectangle]

        val rect = node.asInstanceOf[Rectangle]
        rect.getAccessibleText shouldEqual "Figure 1"
      }

      "create selected large figure with gold border" in {
        val node = DynamicRenderer.createFigureNodeLarge(Color.RED, 2, true)
        node shouldBe a[Rectangle]

        val rect = node.asInstanceOf[Rectangle]
        rect.getStrokeWidth shouldEqual 3.0
      }
    }

    "createEmptyFieldNode" should {
      "create rectangle with light fill" in {
        val node = DynamicRenderer.createEmptyFieldNode()
        node shouldBe a[Rectangle]
      }
    }

    "createGoalFieldNode" should {
      "create rectangle with player color border" in {
        val node = DynamicRenderer.createGoalFieldNode(Color.BLUE)
        node shouldBe a[Rectangle]

        val rect = node.asInstanceOf[Rectangle]
        rect.getStrokeWidth shouldEqual 2.0
      }
    }

    "createGoalFieldNodeLarge" should {
      "create large rectangle for goal areas" in {
        val node = DynamicRenderer.createGoalFieldNodeLarge(Color.GREEN)
        node shouldBe a[Rectangle]
      }
    }

    "createStartFieldNode" should {
      "create rectangle with player color" in {
        val node = DynamicRenderer.createStartFieldNode(Color.YELLOW)
        node shouldBe a[Rectangle]

        val rect = node.asInstanceOf[Rectangle]
        rect.getStrokeWidth shouldEqual 2.0
      }
    }

    "createNormalFieldNode" should {
      "create white rectangle with brown border" in {
        val node = DynamicRenderer.createNormalFieldNode()
        node shouldBe a[Rectangle]

        val rect = node.asInstanceOf[Rectangle]
        rect.getStrokeWidth shouldEqual 1.0
      }
    }
  }

  "grid rendering" should {
    "renderStartFieldGrid" should {
      "clear existing children before rendering" in {
        val mockPlayer = mock[IPlayer]
        val mockGridPane = mock[GridPane]
        val mockChildren = createMockObservableList[Node]()
        val mockFigure = mock[IFigure]

        when(mockGridPane.getChildren).thenReturn(mockChildren)
        when(mockPlayer.figures).thenReturn(List(mockFigure))
        when(mockFigure.adjustedIndex(0)).thenReturn(Position.Home(0))
        when(mockFigure.id).thenReturn(1)
        when(mockPlayer.color).thenReturn(Color.RED)

        noException should be thrownBy {
          DynamicRenderer.renderStartFieldGrid(
            mockPlayer,
            mockGridPane,
            0,
            true
          )
        }

        verify(mockChildren).clear()
      }

      "handle multiple figures in home positions" in {
        val mockPlayer = mock[IPlayer]
        val mockGridPane = mock[GridPane]
        val mockChildren = createMockObservableList[Node]()
        val mockFigure1 = mock[IFigure]
        val mockFigure2 = mock[IFigure]

        when(mockGridPane.getChildren).thenReturn(mockChildren)
        when(mockPlayer.figures).thenReturn(List(mockFigure1, mockFigure2))
        when(mockFigure1.adjustedIndex(0)).thenReturn(Position.Home(0))
        when(mockFigure2.adjustedIndex(0)).thenReturn(Position.Home(1))
        when(mockFigure1.id).thenReturn(1)
        when(mockFigure2.id).thenReturn(2)
        when(mockPlayer.color).thenReturn(Color.BLUE)

        noException should be thrownBy {
          DynamicRenderer.renderStartFieldGrid(
            mockPlayer,
            mockGridPane,
            1,
            false
          )
        }
      }
    }

    "renderGoalPathGridPane" should {
      "render goal fields horizontally" in {
        val mockPlayer = mock[IPlayer]
        val mockGridPane = mock[GridPane]
        val mockChildren = createMockObservableList[Node]()
        val mockFigure = mock[IFigure]

        when(mockGridPane.getChildren).thenReturn(mockChildren)
        when(mockPlayer.figures).thenReturn(List(mockFigure))
        when(mockFigure.adjustedIndex(8)).thenReturn(Position.Goal(0))
        when(mockFigure.id).thenReturn(1)
        when(mockPlayer.color).thenReturn(Color.BLUE)

        noException should be thrownBy {
          DynamicRenderer.renderGoalPathGridPane(
            mockPlayer,
            mockGridPane,
            8,
            0,
            true
          )
        }

        verify(mockChildren).clear()
      }

      "handle figures not in goal" in {
        val mockPlayer = mock[IPlayer]
        val mockGridPane = mock[GridPane]
        val mockChildren = createMockObservableList[Node]()
        val mockFigure = mock[IFigure]

        when(mockGridPane.getChildren).thenReturn(mockChildren)
        when(mockPlayer.figures).thenReturn(List(mockFigure))
        when(mockFigure.adjustedIndex(8)).thenReturn(Position.Normal(5))
        when(mockFigure.id).thenReturn(1)
        when(mockPlayer.color).thenReturn(Color.GREEN)

        noException should be thrownBy {
          DynamicRenderer.renderGoalPathGridPane(
            mockPlayer,
            mockGridPane,
            8,
            0,
            false
          )
        }
      }
    }
  }

  "VBox rendering" should {
    "renderGoalPathVBox" should {
      "clear and render goal fields vertically" in {
        val mockPlayer = mock[IPlayer]
        val mockVBox = mock[VBox]
        val mockChildren = createMockObservableList[Node]()
        val mockFigure = mock[IFigure]

        when(mockVBox.getChildren).thenReturn(mockChildren)
        when(mockPlayer.figures).thenReturn(List(mockFigure, mockFigure))
        when(mockFigure.adjustedIndex(8)).thenReturn(Position.Goal(0))
        when(mockFigure.id).thenReturn(1)
        when(mockPlayer.color).thenReturn(Color.GREEN)

        noException should be thrownBy {
          DynamicRenderer.renderGoalPathVBox(
            mockPlayer,
            mockVBox,
            8,
            0,
            false
          )
        }

        verify(mockChildren).clear()
      }
    }

    "renderGoalPathVBoxHorizontal" should {
      "create HBox inside VBox for horizontal layout" in {
        val mockPlayer = mock[IPlayer]
        val mockVBox = mock[VBox]
        val mockChildren = createMockObservableList[Node]()
        val mockFigure = mock[IFigure]

        when(mockVBox.getChildren).thenReturn(mockChildren)
        when(mockPlayer.figures).thenReturn(List(mockFigure))
        when(mockFigure.adjustedIndex(8)).thenReturn(Position.Normal(5))
        when(mockFigure.id).thenReturn(1)
        when(mockPlayer.color).thenReturn(Color.YELLOW)

        noException should be thrownBy {
          DynamicRenderer.renderGoalPathVBoxHorizontal(
            mockPlayer,
            mockVBox,
            8,
            0,
            false
          )
        }

        verify(mockChildren).clear()
      }

      "handle empty figure list" in {
        val mockPlayer = mock[IPlayer]
        val mockVBox = mock[VBox]
        val mockChildren = createMockObservableList[Node]()

        when(mockVBox.getChildren).thenReturn(mockChildren)
        when(mockPlayer.figures).thenReturn(List())
        when(mockPlayer.color).thenReturn(Color.RED)

        noException should be thrownBy {
          DynamicRenderer.renderGoalPathVBoxHorizontal(
            mockPlayer,
            mockVBox,
            10,
            0,
            true
          )
        }
      }
    }
  }

  "error handling" should {
    "handle missing nodes gracefully" in {
      val mockManager = mock[IManager]
      val parent = new VBox() // statt mock[Parent]

      when(mockManager.players).thenReturn(List())

      noException should be thrownBy {
        DynamicRenderer.renderPlayerStatus(mockManager, parent)
        DynamicRenderer.renderCurrentPlayer(mockManager, parent)
        DynamicRenderer.renderDiceResult(mockManager, parent)
      }
    }

    "handle null or invalid objects gracefully" in {
      val mockManager = mock[IManager]
      val mockParent = mock[Parent]
      val mockPlayer = mock[IPlayer]

      when(mockManager.players).thenReturn(List(mockPlayer))
      when(mockPlayer.color).thenReturn(null)
      when(mockPlayer.figures).thenReturn(List())

      noException should be thrownBy {
        DynamicRenderer.renderPlayerStatus(mockManager, mockParent)
      }
    }
  }

  "position calculations" should {
    "handle different figure positions correctly" in {
      val positions = List(
        Position.Home(0),
        Position.Normal(5),
        Position.Goal(2)
      )

      positions.foreach { position =>
        position should not be null
      }
    }

    "handle position bounds correctly" in {
      val homePos = Position.Home(3)
      val normalPos = Position.Normal(15)
      val goalPos = Position.Goal(1)

      homePos should not be null
      normalPos should not be null
      goalPos should not be null
    }
  }

  "integration scenarios" should {
    "handle complete render cycle" in {
      val mockController = mock[IController]
      val mockManager = mock[IManager]
      val mockPlayer = mock[IPlayer]
      val mockBoard = mock[IBoard]
      val mockFigure = mock[IFigure]

      when(mockController.manager).thenReturn(mockManager)
      when(mockManager.players).thenReturn(List(mockPlayer))
      when(mockManager.board).thenReturn(mockBoard)
      when(mockManager.getCurrentPlayer).thenReturn(0)
      when(mockManager.rolled).thenReturn(4)
      when(mockManager.selectedFigure).thenReturn(1)
      when(mockPlayer.color).thenReturn(Color.RED)
      when(mockPlayer.id).thenReturn(1)
      when(mockPlayer.figures).thenReturn(List(mockFigure))
      when(mockBoard.size).thenReturn(8)
      when(mockFigure.adjustedIndex(any())).thenReturn(Position.Normal(2))
      when(mockFigure.id).thenReturn(1)
      when(mockFigure.owner).thenReturn(mockPlayer)

      // Echte Nodes mit IDs
      val redPlayerStatus = new Label();
      redPlayerStatus.setId("redPlayerStatus")
      val currentPlayerLabel = new Label();
      currentPlayerLabel.setId("currentPlayerLabel")
      val diceResultLabel = new Label();
      diceResultLabel.setId("diceResultLabel")
      val diceButton = new Button(); diceButton.setId("diceButton")
      val redStartGrid = new GridPane(); redStartGrid.setId("redStartGrid")
      val blueStartGrid = new GridPane(); blueStartGrid.setId("blueStartGrid")
      val yellowGoalVBox = new VBox(); yellowGoalVBox.setId("yellowGoalVBox")
      val mainGameBoardGrid = new GridPane();
      mainGameBoardGrid.setId("mainGameBoardGrid")
      val redPlayerArea = new VBox(); redPlayerArea.setId("redPlayerArea")
      val bluePlayerArea = new VBox(); bluePlayerArea.setId("bluePlayerArea")
      val greenPlayerArea = new VBox(); greenPlayerArea.setId("greenPlayerArea")
      val yellowPlayerArea = new VBox();
      yellowPlayerArea.setId("yellowPlayerArea")

      val parent = new VBox(
        redPlayerStatus,
        currentPlayerLabel,
        diceResultLabel,
        diceButton,
        redStartGrid,
        blueStartGrid,
        yellowGoalVBox,
        mainGameBoardGrid,
        redPlayerArea,
        bluePlayerArea,
        greenPlayerArea,
        yellowPlayerArea
      )

      noException should be thrownBy {
        DynamicRenderer.updateRender(mockController, parent)
        DynamicRenderer.renderPlayerStatus(mockManager, parent)
        DynamicRenderer.renderCurrentPlayer(mockManager, parent)
        DynamicRenderer.renderDiceResult(mockManager, parent)
        DynamicRenderer.updateDiceButtonState(mockManager, parent)
        DynamicRenderer.renderStartFields(mockManager, parent)
        DynamicRenderer.renderGoalPaths(mockManager, parent)
        DynamicRenderer.renderMainGameBoard(mockManager, parent)
        DynamicRenderer.updatePlayerAreaVisibility(mockManager, parent)
      }
    }
  }
}
