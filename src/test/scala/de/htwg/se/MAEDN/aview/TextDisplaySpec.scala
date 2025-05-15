package de.htwg.se.MAEDN.aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.model.states._
import de.htwg.se.MAEDN.util._
import de.htwg.se.MAEDN.controller._

class TextDisplaySpec extends AnyWordSpec with Matchers {

  def stripAnsi(s: String): String = s.replaceAll("\u001B\\[[;\\d]*m", "")

  "TextDisplay" should {

    "return ANSI code to clear terminal" in {
      TextDisplay.clearTerminal() shouldBe "\u001b[2J\u001b[H"
    }

    "render the correct title color in printCover (green for running)" in {
      val controller = new Controller()
      val state = new RunningState(controller, 0, Board(4), PlayerFactory.createPlayers(2, 4))
      val result = TextDisplay.printCover(state)
      result should include("Mensch ärger dich nicht")
      result should include("\u001b[32m") // GREEN
    }

    "render yellow title for config state" in {
      val controller = new Controller()
      val state = new ConfigState(controller, 0, Board(4), PlayerFactory.createPlayers(2, 4))
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
      val raw = TextDisplay.printBoard(board, selectedFigure = 1, currentPlayerIndex = 0, players = List(red, blue))
      val result = stripAnsi(raw)

      result should include("Home Benches")
      result should include("RED Home")
      result should include("BLUE Home")
      result should include("Main Track")
      result should include("Goal Lanes")
      result should include(">F2<") // selectedFigure = 1 + 1
    }

    "color all player types correctly" in {
      val red = Player(1, List(Figure(0, null, -1)), Color.RED)
      val blue = Player(2, List(Figure(0, null, -1)), Color.BLUE)
      val yellow = Player(3, List(Figure(0, null, -1)), Color.YELLOW)
      val green = Player(4, List(Figure(0, null, -1)), Color.GREEN)
      val white = Player(5, List(Figure(0, null, -1)), Color.WHITE)

      val board = Board(4)
      val result = TextDisplay.printBoard(board, players = List(red, blue, yellow, green, white))

      result should include("\u001b[31m") // RED
      result should include("\u001b[34m") // BLUE
      result should include("\u001b[33m") // YELLOW
      result should include("\u001b[32m") // GREEN
      result should include("\u001b[37m") // WHITE
    }

    "show H when figure is not at home" in {
        // Erst leeren Player erzeugen
        val dummyPlayer = Player(1, Nil, Color.RED)

        // Dann Figuren mit echtem Player als owner
        val figures = List(Figure(0, dummyPlayer, 3)) // 3 ≠ -1 → kein Home

        // Jetzt Player mit diesen Figuren neu zusammensetzen
        val red = Player(1, figures, Color.RED)

        val board = Board(4)
        val result = stripAnsi(TextDisplay.printBoard(board, players = List(red)))
        result should include("H ")
    }

    "highlight figure on main track when selected" in {
        val boardSize = 4
        val player = Player(1, Nil, Color.RED)

        val figure = Figure(1, player, 0) // index 0 → Normal(0)
        val red = Player(1, List(figure), Color.RED)

        val board = Board(boardSize)

        val result = stripAnsi(TextDisplay.printBoard(
            board,
            selectedFigure = 0,      // +1 == 1 == fig.id
            currentPlayerIndex = 0,  // player.id - 1 == 0
            players = List(red)
        ))

        result should include(">F1<")
    }

    "highlight figure in goal area when selected" in {
        val boardSize = 4

        // Erst dummyPlayer erzeugen
        val dummyPlayer = Player(1, Nil, Color.RED)

        // Dann Figuren mit owner = dummyPlayer
        val f0 = Figure(0, dummyPlayer, -1)
        val f1 = Figure(1, dummyPlayer, boardSize * 4 + 1) // Goal(1)

        // Jetzt Figuren dem Player geben
        val red = Player(1, List(f0, f1), Color.RED)

        // UND: owner in Figures aktualisieren, sonst ist's nur "halbrichtig"
        val fixedFigures = red.figures.map(f => f.copy(owner = red))
        val fixedPlayer = red.copy(figures = fixedFigures)

        val board = Board(boardSize)

        val result = stripAnsi(TextDisplay.printBoard(
            board,
            selectedFigure = 0,
            currentPlayerIndex = 0,
            players = List(fixedPlayer)
        ))

        result should include(">F1<")
    }

    "render figure normally in goal area when not selected" in {
        val boardSize = 4
        val dummyPlayer = Player(1, Nil, Color.RED)

        val f0 = Figure(1, dummyPlayer, boardSize * 4) // Goal(0), id = 1
        val f1 = Figure(2, dummyPlayer, -1)            // Dummy

        val red = Player(1, List(f0, f1), Color.RED)
        val fixedFigures = red.figures.map(f => f.copy(owner = red))
        val fixedPlayer = red.copy(figures = fixedFigures)

        val board = Board(boardSize)

        val result = stripAnsi(TextDisplay.printBoard(
            board,
            selectedFigure = 1,      // +1 = 2 → trifft F1, aber NICHT F0
            currentPlayerIndex = 0,
            players = List(fixedPlayer)
        ))

        result should include("F1")     // ohne >
        result should not include(">F1<")
    }








  }
}
