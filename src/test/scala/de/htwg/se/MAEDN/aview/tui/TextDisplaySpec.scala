package de.htwg.se.MAEDN.aview.tui

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util._
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.aview.tui.TextDisplay // Fixed import path
import scala.io.AnsiColor
import de.htwg.se.MAEDN.controller.controllerImp.Controller

class TextDisplaySpec extends AnyWordSpec with Matchers {

  def stripAnsi(s: String): String = s.replaceAll("\u001B\\[[;\\d]*m", "")

  // Helper to create a mock IState for testing
  class MockState(
      val state: State,
      val moves: Int = 0,
      val selectedFigure: Int = -1,
      val rolled: Int = 0,
      val currentPlayer: Int = 0,
      val playerCount: Int = 2,
      val boardSize: Int = 4,
      val figureCount: Int = 4
  ) extends IState {
    def getPlayerCount: Int = playerCount
    def getBoardSize: Int = boardSize
    def getFigureCount: Int = figureCount
    def getCurrentPlayer: Int = currentPlayer
    def handle(input: String): IState = this
    def printState(): String = ""

    // Stub-Implementierungen für alle abstrakten Mitglieder:
    val controller: de.htwg.se.MAEDN.controller.IController = null
    def decreaseBoardSize(): scala.util.Try[de.htwg.se.MAEDN.model.IManager] =
      ???
    def decreaseFigures(): scala.util.Try[de.htwg.se.MAEDN.model.IManager] = ???
    def getPlayers: List[de.htwg.se.MAEDN.model.Player] = Nil
    def increaseBoardSize(): scala.util.Try[de.htwg.se.MAEDN.model.IManager] =
      ???
    def increaseFigures(): scala.util.Try[de.htwg.se.MAEDN.model.IManager] = ???
    def moveDown(): scala.util.Try[de.htwg.se.MAEDN.model.IManager] = ???
    def moveFigure(): scala.util.Try[de.htwg.se.MAEDN.model.IManager] = ???
    def moveUp(): scala.util.Try[de.htwg.se.MAEDN.model.IManager] = ???
    def playDice(): scala.util.Try[de.htwg.se.MAEDN.model.IManager] = ???
    def playNext(): scala.util.Try[de.htwg.se.MAEDN.model.IManager] = ???
    def quitGame(): scala.util.Try[de.htwg.se.MAEDN.model.IManager] = ???
    def startGame(): scala.util.Try[de.htwg.se.MAEDN.model.IManager] = ???
  }

  "TextDisplay" should {

    "render 'N ' for a field with no figure and no start field" in {
      val size = 4
      val board = Board(size)

      // Create a single player to avoid empty list
      val player = Player(1, List.empty[Figure], Color.RED)
      val players = List(player)

      val result = TextDisplay.printBoard(
        board = board,
        selectedFigure = -1,
        currentPlayerIndex = -1,
        players = players
      )

      // Check for neutral field representation
      val containsNeutralField = result.linesIterator.exists(_.contains("N "))
      containsNeutralField shouldBe true
    }

    "render Home and Goal sections for all player colors" in {
      val size = 4
      val board = Board(size)

      // Create players with different colors
      val players = List(
        Player(1, List.empty[Figure], Color.RED),
        Player(2, List.empty[Figure], Color.BLUE),
        Player(3, List.empty[Figure], Color.YELLOW),
        Player(4, List.empty[Figure], Color.GREEN),
        Player(5, List.empty[Figure], Color.WHITE)
      )

      val result = TextDisplay.printBoard(
        board = board,
        selectedFigure = -1,
        currentPlayerIndex = -1,
        players = players
      )

      // Check that each color appears in home and goal sections
      // Fixed: Using uppercase color names to match actual output
      players.foreach { player =>
        val colorName = player.color.toString.toUpperCase
        result should include(s"$colorName Home:")
        result should include(s"$colorName Goal:")
      }
    }

    "include 'N ' for empty main track positions without start fields" in {
      val size = 4
      val board = Board(size)
      val players = List.empty[Player] // No players

      val result = TextDisplay.printBoard(
        board = board,
        selectedFigure = -1,
        currentPlayerIndex = -1,
        players = players
      )

      result should include("N ")
    }

    "return ANSI code to clear terminal" in {
      TextDisplay.clearTerminal() shouldBe "\u001b[2J\u001b[H"
    }

    "render the correct title color in printCover (green for running)" in {
      val state = new MockState(State.Running, playerCount = 2, figureCount = 4)
      val result = TextDisplay.printCover(state)
      result should include("Mensch ärger dich nicht")
      result should include("\u001b[32m") // GREEN
    }

    "render yellow title for config state" in {
      val state = new MockState(State.Config, playerCount = 2, figureCount = 4)
      val result = TextDisplay.printCover(state)
      result should include("\u001b[33m") // YELLOW
    }

    "render a flat board with correct length" in {
      val board = Board(4)
      val result = TextDisplay.printFlatBoard(board)
      result should include("? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ?")
    }

    "render config screen with values" in {
      val result = TextDisplay.printConfig(2, 4, 5)
      result should include("2")
      result should include("4")
      result should include("5")
      result should include("Press n to start a new game")
    }

    "render a board with home, main, and goal sections" in {
      val player1 = Player(1, Nil, Color.RED)
      val player2 = Player(2, Nil, Color.BLUE)

      // Create figures with the required figureCount parameter
      val figures1 = List.tabulate(4)(i => Figure(i, player1, -1, 4)) // Home
      val figures2 = List.tabulate(4)(i => Figure(i, player2, -1, 4)) // Home

      val red = Player(1, figures1, Color.RED)
      val blue = Player(2, figures2, Color.BLUE)

      // Fix figure ownership
      val fixedRed = red.copy(figures = red.figures.map(_.copy(owner = red)))
      val fixedBlue =
        blue.copy(figures = blue.figures.map(_.copy(owner = blue)))

      val board = Board(4)
      val raw = TextDisplay.printBoard(
        board,
        selectedFigure = 1,
        currentPlayerIndex = 0,
        players = List(fixedRed, fixedBlue)
      )
      val result = stripAnsi(raw)

      result should include("Home Benches")
      result should include("RED Home") // Fixed: uppercase
      result should include("BLUE Home") // Fixed: uppercase
      result should include("Main Track")
      result should include("Goal Lanes")
      result should include(">F2<") // selectedFigure = 1 + 1
    }

    "highlight figure on main track when selected" in {
      val boardSize = 4
      val player = Player(1, Nil, Color.RED)

      val figure = Figure(1, player, 0, 4) // index 0 → Normal(0)
      val red = Player(1, List(figure), Color.RED)
      val fixedRed = red.copy(figures = red.figures.map(_.copy(owner = red)))

      val board = Board(boardSize)

      val result = stripAnsi(
        TextDisplay.printBoard(
          board,
          selectedFigure = 0,
          currentPlayerIndex = 0,
          players = List(fixedRed)
        )
      )

      result should include(">F1<")
    }

    "highlight figure in goal area when selected" in {
      val boardSize = 4
      val dummyPlayer = Player(1, Nil, Color.RED)

      val f0 = Figure(0, dummyPlayer, -1, 4)
      val f1 = Figure(1, dummyPlayer, boardSize * 4 + 1, 4)
      val red = Player(1, List(f0, f1), Color.RED)
      val fixedPlayer = red.copy(figures = red.figures.map(_.copy(owner = red)))

      val board = Board(boardSize)
      val result = stripAnsi(
        TextDisplay.printBoard(
          board,
          selectedFigure = 0,
          currentPlayerIndex = 0,
          players = List(fixedPlayer)
        )
      )

      result should include(">F1<")
    }

    "render unselected figure on main track as plain F{id}" in {
      val boardSize = 4
      val player = Player(1, Nil, Color.RED)
      val figure = Figure(2, player, 0, 4) // index 0 → Position.Normal(0)
      val red = player.copy(figures = List(figure))
      val fixedRed = red.copy(figures = red.figures.map(_.copy(owner = red)))

      val board = Board(boardSize)

      val result = stripAnsi(
        TextDisplay.printBoard(
          board,
          selectedFigure = -1, // not selected
          currentPlayerIndex = 0,
          players = List(fixedRed)
        )
      )

      result should include("F2")
      result should not include (">F2<") // explicitly not selected
    }

    "render figure normally in goal area when not selected" in {
      val boardSize = 4
      val dummyPlayer = Player(1, Nil, Color.RED)

      val f0 = Figure(1, dummyPlayer, boardSize * 4, 4)
      val f1 = Figure(2, dummyPlayer, -1, 4)
      val red = Player(1, List(f0, f1), Color.RED)
      val fixedPlayer = red.copy(figures = red.figures.map(_.copy(owner = red)))

      val board = Board(boardSize)
      val result = stripAnsi(
        TextDisplay.printBoard(
          board,
          selectedFigure = 1,
          currentPlayerIndex = 0,
          players = List(fixedPlayer)
        )
      )

      result should include("F1")
      result should not include (">F1<")
    }

    "TextDisplay.printBoard" should {
      "render sections for a manually created WHITE player" in {
        val size = 4
        val board = Board(size)

        val whitePlayer = Player(
          id = 5,
          figures = List(Figure(1, null, -1, 4)), // Dummy, will be fixed
          color = Color.WHITE
        )

        val fixedFigure = whitePlayer.figures.head.copy(owner = whitePlayer)
        val fixedPlayer = whitePlayer.copy(figures = List(fixedFigure))

        val result = TextDisplay.printBoard(
          board = board,
          selectedFigure = -1,
          currentPlayerIndex = -1,
          players = List(fixedPlayer)
        )

        result should include("WHITE Home:") // Fixed: uppercase
        result should include("WHITE Goal:") // Fixed: uppercase
      }
    }

    "render 'N ' using var board2 and assert only" in {
      var board2 = Board(4)
      val players = Nil

      val result = TextDisplay.printBoard(
        board = board2,
        selectedFigure = -1,
        currentPlayerIndex = -1,
        players = players
      )

      assert(result.contains("N "))
    }

  }
}
