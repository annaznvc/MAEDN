package de.htwg.se.MAEDN.model.strategy

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util._
import ToBoardStrategy

class ToBoardStrategySpec extends AnyWordSpec with Matchers {

  "A ToBoardStrategy" when {
    val strategy = new ToBoardStrategy
    val boardSize = 4

    val redPlayer = Player(0, List.empty, Color.RED)
    val bluePlayer = Player(1, List.empty, Color.BLUE)

    "calling moveFigure" should {

      "place figure on start field if rolled == 6 and no collision" in {
        val redFigure = Figure(0, redPlayer, -1) // Home
        val result = strategy.moveFigure(
          redFigure,
          List(redFigure),
          boardSize,
          rolled = 6
        )

        val moved = result.find(_.id == redFigure.id).get
        moved.index shouldBe 0 // Zeile f.copy(index = 0) wird ausgeführt ✅
      }

      "not move figure if rolled != 6" in {
        val redFigure = Figure(0, redPlayer, -1)
        val result = strategy.moveFigure(
          redFigure,
          List(redFigure),
          boardSize,
          rolled = 5
        )

        result.map(_.index) should contain only -1 // Rückgabe unverändert ✅
      }

      "not move figure if own figure is blocking start field" in {
        val redFigure = Figure(0, redPlayer, -1)
        val blocker = Figure(1, redPlayer, 0) // gleiche Farbe, steht auf Start

        val result = strategy.moveFigure(
          redFigure,
          List(redFigure, blocker),
          boardSize,
          rolled = 6
        )

        result.map(_.index) should contain allElementsOf Seq(
          -1,
          0
        ) // nicht bewegt ✅
      }
    }

    "calling canMove" should {

      "return true if rolled == 6 and start field is free" in {
        val redFigure = Figure(0, redPlayer, -1)
        val result = strategy.canMove(
          redFigure,
          List(redFigure),
          boardSize,
          rolled = 6
        )

        result shouldBe true
      }

      "return false if rolled != 6" in {
        val redFigure = Figure(0, redPlayer, -1)
        val result = strategy.canMove(
          redFigure,
          List(redFigure),
          boardSize,
          rolled = 5
        )

        result shouldBe false
      }

      "return false if own figure is on start field" in {
        val redFigure = Figure(0, redPlayer, -1)
        val blocker = Figure(1, redPlayer, 0)

        val result = strategy.canMove(
          redFigure,
          List(redFigure, blocker),
          boardSize,
          rolled = 6
        )

        result shouldBe false
      }

      "leave other figures unchanged when moving one to start field" in {
        val redFigure = Figure(0, redPlayer, -1) // wird bewegt
        val redOther = Figure(1, redPlayer, 5) // bleibt wie er ist

        val result = strategy.moveFigure(
          redFigure,
          List(redFigure, redOther),
          boardSize,
          rolled = 6
        )

        val moved = result.find(_.id == redFigure.id).get
        moved.index shouldBe 0 // bewegt

        val untouched = result.find(_.id == redOther.id).get
        untouched.index shouldBe 5 // ➜ else-Zweig wurde durchlaufen ✅
      }

    }
  }
}
