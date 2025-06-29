package de.htwg.se.MAEDN.model.strategy

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util._
import de.htwg.se.MAEDN.model.strategy.KickFigureStrategy

class KickFigureStrategySpec extends AnyWordSpec with Matchers {

  "A KickFigureStrategy" when {
    val strategy = new KickFigureStrategy
    val boardSize = 4

    val redPlayer = Player(0, List.empty, Color.RED)
    val bluePlayer = Player(1, List.empty, Color.BLUE)

    "calling moveFigure" should {

      "kick an enemy figure back to home if there is a collision" in {
        val redFigure = Figure(0, redPlayer, 0, 4)
        val blueFigure = Figure(1, bluePlayer, 12, 4)

        val result = strategy.moveFigure(
          redFigure,
          List(redFigure, blueFigure),
          boardSize,
          rolled = 0
        )

        val kickedFigure = result.find(_.owner == bluePlayer).get
        kickedFigure.index shouldBe -1

        val redUnchanged = result.find(_.owner == redPlayer).get
        redUnchanged.index shouldBe 0
      }

      "not change figures if there is no collision" in {
        val redFigure = Figure(0, redPlayer, 0, 4)
        val blueFar = Figure(1, bluePlayer, 13, 4)

        val result = strategy.moveFigure(
          redFigure,
          List(redFigure, blueFar),
          boardSize,
          rolled = 0
        )

        result.map(_.index) should contain theSameElementsAs Seq(0, 13)
      }
    }

    "calling canMove" should {

      "return true if a collision is possible" in {
        val redFigure = Figure(0, redPlayer, 0, 4)
        val blueFigure = Figure(1, bluePlayer, 12, 4)

        val result = strategy.canMove(
          redFigure,
          List(redFigure, blueFigure),
          boardSize,
          rolled = 0
        )

        result shouldBe true
      }

      "return false if no collisions are possible" in {
        val redFigure = Figure(0, redPlayer, 0, 4)
        val blueFar = Figure(1, bluePlayer, 13, 4)

        val result = strategy.canMove(
          redFigure,
          List(redFigure, blueFar),
          boardSize,
          rolled = 0
        )

        result shouldBe false
      }

      "not kick a figure if there is no enemy collision (e.g., same color or no collision)" in {
        val redFigure = Figure(0, redPlayer, 0, 4)
        val anotherRedFigure = Figure(2, redPlayer, 0, 4) // ✅ hinzugefügt

        val result = strategy.moveFigure(
          redFigure,
          List(redFigure, anotherRedFigure),
          boardSize,
          rolled = 0
        )

        result.map(_.index) should contain theSameElementsAs Seq(0, 0)
      }
    }
  }
}
