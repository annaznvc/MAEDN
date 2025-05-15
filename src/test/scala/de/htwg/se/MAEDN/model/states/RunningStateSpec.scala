package de.htwg.se.MAEDN.model.states

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util._
import de.htwg.se.MAEDN.controller.Controller

class RunningStateSpec extends AnyWordSpec with Matchers {

  "A RunningState" should {

    val controller = new Controller

    // Manuell korrekt verknüpfte Spieler mit Figuren
    val redPlayer = Player(0, Nil, Color.RED)
    val bluePlayer = Player(1, Nil, Color.BLUE)
    val redFigure = Figure(0, redPlayer, 0)
    val blueFigure = Figure(1, bluePlayer, 0)
    val red = redPlayer.copy(figures = List(redFigure))
    val blue = bluePlayer.copy(figures = List(blueFigure))
    val players = List(red, blue)

    val board = Board(4)

    "return updated state on moveUp" in {
      val state =
        RunningState(controller, 0, board, players, 0, selectedFigure = 0)
      val next = state.moveUp()

      next.selectedFigure shouldBe 1 % players.head.figures.size
    }

    "return updated state on moveDown" in {
      val state =
        RunningState(controller, 0, board, players, 0, selectedFigure = 0)
      val next = state.moveDown()

      next.selectedFigure shouldBe (players.head.figures.size - 1)
    }

    "roll the dice and update rolled value" in {
      val state = RunningState(controller, 0, board, players, 0, 0)
      val next = state.playDice()

      next.rolled should (be >= 1 and be <= 6)
    }

    "call PlayNextEvent and advance move counter when rolled == -1" in {
      val state = RunningState(
        controller,
        moves = 0,
        board = board,
        players = players,
        rolled = -1,
        selectedFigure = 0
      )

      val next = state.playNext()

      next.moves shouldBe 1
      next.rolled shouldBe 0
    }

    "transition to MenuState on quitGame" in {
      val state = RunningState(controller, 0, board, players, 0, 0)
      val next = state.quitGame()

      next shouldBe a[MenuState]
    }

    "call playDice when rolled == 0 (coverage!)" in {
      val redPlayer = Player(0, Nil, Color.RED)
      val redFigure = Figure(0, redPlayer, 0)
      val player = redPlayer.copy(figures = List(redFigure))
      val board = Board(4)
      val controller = new Controller
      val state = RunningState(
        controller = controller,
        moves = 0,
        board = board,
        players = List(player),
        rolled = 0,
        selectedFigure = 0
      )

      val next = state.playNext()

      // rolled war 0 → playDice wurde aufgerufen → rolled nun 1–6
      next.rolled should (be >= 1 and be <= 6)
    }

    "call board.moveFigure with rolled > 0 (coverage for line 64–66)" in {
      val redPlayer = Player(0, Nil, Color.RED)
      val figure = Figure(0, redPlayer, 0)
      val player = redPlayer.copy(figures = List(figure))
      val players = List(player)
      val board = Board(4)
      val controller = new Controller

      val state = RunningState(
        controller = controller,
        moves = 0,
        board = board,
        players = players,
        rolled = 1,
        selectedFigure = 0
      )

      val next = state.playNext()

      // Wir erwarten: move wurde versucht (ob erfolgreich oder nicht egal)
      // rolled wird -1 oder 0 gesetzt → moveFigure wurde ausgeführt
      next.rolled should (be <= 0)
    }

    "return unchanged state and enqueue InvalidMoveEvent if move is not possible (coverage for 68–71)" in {
      val redPlayer = Player(0, Nil, Color.RED)
      val redFigure = Figure(0, redPlayer, -1) // Figur ist noch Zuhause
      val redBlocker =
        Figure(1, redPlayer, 0) // steht auf Startposition (blockiert)
      val player = redPlayer.copy(figures = List(redFigure, redBlocker))
      val players = List(player)
      val board = Board(4)
      val controller = new Controller

      val state = RunningState(
        controller = controller,
        moves = 0,
        board = board,
        players = players,
        rolled = 6, // normalerweise darf man raus, aber Startfeld ist blockiert
        selectedFigure = 0
      )

      val next = state.playNext()

      // moveFigure wird aufgerufen, aber newFigures == figures ⇒ InvalidMoveEvent block
      next shouldBe state // copy(...) wird nicht aufgerufen → this zurückgegeben
    }

    "set rolled to 0 after successful move with rolled == 6 (coverage for line 83)" in {
      val redPlayer = Player(0, Nil, Color.RED)
      val figure = Figure(0, redPlayer, 0)
      val player = redPlayer.copy(figures = List(figure))
      val players = List(player)
      val board = Board(4)
      val controller = new Controller

      val state = RunningState(
        controller = controller,
        moves = 0,
        board = board,
        players = players,
        rolled = 6,
        selectedFigure = 0
      )

      val next = state.playNext()

      // Nach erfolgreichem Zug bei rolled == 6 wird rolled auf 0 gesetzt → Zeile 83 grün ✅
      next.rolled shouldBe 0
    }

  }
}
