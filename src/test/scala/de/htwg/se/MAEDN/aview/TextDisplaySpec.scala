package de.htwg.se.MAEDN.aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.model.states._
import de.htwg.se.MAEDN.util._
import de.htwg.se.MAEDN.controller._
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.aview.TextDisplay
import scala.io.AnsiColor
import de.htwg.se.MAEDN.model.PlayerFactory
import de.htwg.se.MAEDN.model.Board

class TextDisplaySpec extends AnyWordSpec with Matchers {

  def stripAnsi(s: String): String = s.replaceAll("\u001B\\[[;\\d]*m", "")

  "TextDisplay" should {

    "render 'N ' for a field with no figure and no start field" in {
      val size = 4
      val board = Board(size)

      // Spieler mit Startfeld z. B. auf Position 0 → wir testen eine andere, z. B. 7
      val players = PlayerFactory(1, 1) // RED

      val result = TextDisplay.printBoard(
        board = board,
        selectedFigure = -1,
        currentPlayerIndex = -1,
        players = players
      )

      // Suche nach dem neutralen Feld
      val containsNeutralField = result.linesIterator.exists(_.contains("N "))
      containsNeutralField shouldBe true
    }

    "render Home and Goal sections for all five player colors" in {
      val size = 4
      val board = Board(size)
      val players = PlayerFactory(5, 1)

      val result = TextDisplay.printBoard(
        board = board,
        selectedFigure = -1,
        currentPlayerIndex = -1,
        players = players
      )

      // Hier prüfen wir auf "RED Home:", "BLUE Goal:" usw.
      Color.values.foreach { color =>
        val colorName = color.toString // ergibt "RED", "BLUE", ...
        result should include(s"$colorName Home:")
        result should include(s"$colorName Goal:")
      }
    }

    "include 'N ' for empty main track positions without start fields" in {
      val size = 4
      val board = Board(size)
      val players =
        List
          .empty[Player] // keine Spieler → keine Figuren, keine Startpositionen

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
      val controller = new Controller()
      val state = new RunningState(controller, 0, Board(4), PlayerFactory(2, 4))
      val result = TextDisplay.printCover(state)
      result should include("Mensch ärger dich nicht")
      result should include("\u001b[32m") // GREEN
    }

    "render yellow title for config state" in {
      val controller = new Controller()
      val state = new ConfigState(controller, 0, Board(4), PlayerFactory(2, 4))
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
      result should include("Press [Space] to start a new game")
    }

    "render a board with home, main, and goal sections" in {
      val player1 = Player(1, Nil, Color.RED)
      val player2 = Player(2, Nil, Color.BLUE)

      val figures1 = List.tabulate(4)(i => Figure(i, player1, -1)) // Home
      val figures2 = List.tabulate(4)(i => Figure(i, player2, -1)) // Home

      val red = Player(1, figures1, Color.RED)
      val blue = Player(2, figures2, Color.BLUE)

      val board = Board(4)
      val raw = TextDisplay.printBoard(
        board,
        selectedFigure = 1,
        currentPlayerIndex = 0,
        players = List(red, blue)
      )
      val result = stripAnsi(raw)

      result should include("Home Benches")
      result should include("RED Home")
      result should include("BLUE Home")
      result should include("Main Track")
      result should include("Goal Lanes")
      result should include(">F2<") // selectedFigure = 1 + 1
    }

    "highlight figure on main track when selected" in {
      val boardSize = 4
      val player = Player(1, Nil, Color.RED)

      val figure = Figure(1, player, 0) // index 0 → Normal(0)
      val red = Player(1, List(figure), Color.RED)

      val board = Board(boardSize)

      val result = stripAnsi(
        TextDisplay.printBoard(
          board,
          selectedFigure = 0,
          currentPlayerIndex = 0,
          players = List(red)
        )
      )

      result should include(">F1<")
    }

    "highlight figure in goal area when selected" in {
      val boardSize = 4
      val dummyPlayer = Player(1, Nil, Color.RED)

      val f0 = Figure(0, dummyPlayer, -1)
      val f1 = Figure(1, dummyPlayer, boardSize * 4 + 1)
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
      val figure = Figure(2, player, 0) // index 0 → Position.Normal(0)
      val red = player.copy(figures = List(figure))

      val board = Board(boardSize)

      val result = stripAnsi(
        TextDisplay.printBoard(
          board,
          selectedFigure = -1, // nicht ausgewählt
          currentPlayerIndex = 0,
          players = List(red)
        )
      )

      result should include("F2") // Zeile 88
      result should not include (">F2<") // explizit nicht selektiert
    }

    "render figure normally in goal area when not selected" in {
      val boardSize = 4
      val dummyPlayer = Player(1, Nil, Color.RED)

      val f0 = Figure(1, dummyPlayer, boardSize * 4)
      val f1 = Figure(2, dummyPlayer, -1)
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
          figures = List(Figure(1, null, -1)), // Dummy
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

        result should include("WHITE Home:")
        result should include("WHITE Goal:")
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
