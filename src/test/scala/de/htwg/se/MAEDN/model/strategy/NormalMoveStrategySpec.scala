package de.htwg.se.MAEDN.model.strategy

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util._

class NormalMoveStrategySpec extends AnyWordSpec with Matchers {

  "A NormalMoveStrategy" when {
    val strategy = new NormalMoveStrategy
    val boardSize = 4
    val figureCount = 4

    val redPlayer = Player(0, List.empty, Color.RED)
    val bluePlayer = Player(1, List.empty, Color.BLUE)

    "calling moveFigure" should {

      "move the figure forward if canMove is true" in {
        val redFigure = Figure(0, redPlayer, 0)
        val result = strategy.moveFigure(
          redFigure,
          List(redFigure),
          boardSize,
          rolled = 2
        )

        // Zeile 16 wird sicher aufgerufen
        result should contain(Figure(0, redPlayer, 2))
      }

      "not move the figure if canMove is false" in {
        val redFigure =
          Figure(0, redPlayer, boardSize * 4 + figureCount) // → OffBoard
        val result = strategy.moveFigure(
          redFigure,
          List(redFigure),
          boardSize,
          rolled = 1
        )

        // Zeile 20 wird erreicht
        result shouldBe List(redFigure)
      }
    }

    "calling canMove" should {

      "return false if an own figure is in the way" in {
        val redFigure = Figure(0, redPlayer, 0)
        val blockingRed = Figure(1, redPlayer, 2)

        val result = strategy.canMove(
          redFigure,
          List(redFigure, blockingRed),
          boardSize,
          rolled = 2
        )

        // Zeilen 32–38 werden durchlaufen
        result shouldBe false
      }

      "not allow moving off the board" in {
        val redFigure = Figure(0, redPlayer, boardSize * 4 + figureCount)

        val result = strategy.canMove(
          redFigure,
          List(redFigure),
          boardSize,
          rolled = 1
        )

        // Zeile 40
        result shouldBe false
      }

      "not allow moving from home" in {
        val redFigure = Figure(0, redPlayer, -1)

        val result = strategy.canMove(
          redFigure,
          List(redFigure),
          boardSize,
          rolled = 1
        )

        // Zeile 41
        result shouldBe false
      }

      "leave other figures unchanged when one figure moves" in {
        val redFigure = Figure(0, redPlayer, 0) // wird bewegt
        val redOther = Figure(1, redPlayer, 5) // bleibt wie er ist

        val result = strategy.moveFigure(
          redFigure,
          List(redFigure, redOther),
          boardSize,
          rolled = 2
        )

        // redFigure wird bewegt
        result should contain(Figure(0, redPlayer, 2))

        // redOther bleibt unverändert → else-Zweig wird aufgerufen ✅
        result should contain(redOther)
      }

      "allow goal move if steps < figures.size / 4" in {
        val redPlayer = PlayerFactory.createPlayers(1, 4).head
        val redFigure = redPlayer.figures.head
          .copy(index = 13) // damit index + 3 = 16 → Goal(0)

        val figures = redPlayer.figures.updated(0, redFigure)

        val result = strategy.canMove(
          redFigure,
          figures,
          boardSize,
          rolled = 3
        )

        result shouldBe true // ✅ Jetzt klappt’s und deckt Zeile 39 ab!
      }

    }
  }
}
