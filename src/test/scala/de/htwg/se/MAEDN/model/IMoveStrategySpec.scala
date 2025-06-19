package de.htwg.se.MAEDN.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model.strategy.NormalMoveStrategy
import de.htwg.se.MAEDN.util.Color
import strategy.NormalMoveStrategy

class IMoveStrategySpec extends AnyWordSpec with Matchers {

  val player = Player(0, Nil, Color.RED)

  "isOnGoal (from IMoveStrategy)" should {
    "return true if index is inside goal range" in {
      val strategy = new NormalMoveStrategy
      val size = 4
      val goalCount = 4
      val fig = Figure(0, player, index = 13) // 16 - 4 = 12 → true

      strategy.isOnGoal(fig, goalCount, size) shouldBe true
    }

    "return false if index is below goal range" in {
      val strategy = new NormalMoveStrategy
      val size = 4
      val goalCount = 4
      val fig = Figure(0, player, index = 10)

      strategy.isOnGoal(fig, goalCount, size) shouldBe false
    }
  }

  "collidingFigure (from IMoveStrategy)" should {
    "find another figure on the same adjusted position" in {
      val strategy = new NormalMoveStrategy
      val fig1 = Figure(0, player, index = 3)
      val fig2 = Figure(1, player, index = 3)
      val figures = List(fig1, fig2)

      strategy.collidingFigure(fig1, figures, size = 4) shouldBe Some(fig2)
    }

    "return None if no collision" in {
      val strategy = new NormalMoveStrategy
      val fig1 = Figure(0, player, index = 3)
      val fig2 = Figure(1, player, index = 7)
      val figures = List(fig1, fig2)

      strategy.collidingFigure(fig1, figures, size = 4) shouldBe None
    }
  }

  "isOnLastPossibleField (from IMoveStrategy)" should {

    "return true if figure is on the last main track field" in {
      val strategy = new NormalMoveStrategy
      val size = 4
      val goalCount = 4
      val fig = Figure(0, player, index = size * 4 - 1) // = 15

      strategy.isOnLastPossibleField(
        fig,
        List(fig),
        goalCount,
        size
      ) shouldBe true
    }

    "return true if figure is on the last goal field with correct goal sequence" in {
      val strategy = new NormalMoveStrategy
      val size = 4
      val goalCount = 4
      val goalStart = size * 4 - size // = 12

      val fig0 = Figure(0, player, index = goalStart + 0)
      val fig1 = Figure(1, player, index = goalStart + 1)
      val fig2 = Figure(2, player, index = goalStart + 2)
      val fig3 = Figure(3, player, index = goalStart + 3)

      val figures = List(fig0, fig1, fig2, fig3)

      strategy.isOnLastPossibleField(
        fig3,
        figures,
        goalCount,
        size
      ) shouldBe true
    }

    "return false if figure is not in goal and not on last main track field" in {
      val strategy = new NormalMoveStrategy
      val size = 4
      val goalCount = 4
      val fig = Figure(0, player, index = 10)

      strategy.isOnLastPossibleField(
        fig,
        List(fig),
        goalCount,
        size
      ) shouldBe false
    }

    "return false if not all goal fields before last are filled" in {
      val strategy = new NormalMoveStrategy
      val size = 4
      val goalCount = 4
      val goalStart = size * 4 - size // = 12

      val fig0 = Figure(0, player, index = goalStart + 0) // 12
      // Lücke bei 13
      val fig2 = Figure(1, player, index = goalStart + 2) // 14
      val fig3 = Figure(2, player, index = goalStart + 3) // 15 – wird getestet

      // ❗ fig3 darf nicht auf 15 (lastFieldIndex)! Sonst greift: index == 15 ⇒ true
      val testFig = fig3.copy(index =
        16
      ) // außerhalb Hauptfeld, zählt als Goal, aber nicht lastField

      val figures = List(fig0, fig2, testFig)

      strategy.isOnLastPossibleField(
        testFig,
        figures,
        goalCount,
        size
      ) shouldBe false
    }

  }
}
