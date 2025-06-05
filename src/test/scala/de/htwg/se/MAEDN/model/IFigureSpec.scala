package de.htwg.se.MAEDN.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.util.Position
import de.htwg.se.MAEDN.util.Color

class IFigureSpec extends AnyWordSpec with Matchers {
  "An IFigure" should {
    val player = IPlayer(1, Nil, Color.RED)
    val figure = IFigure(1, player, -1, 4)
    val other = IFigure(2, player, 0, 4)
    val pos: Position = Position.Home(0)

    "have id, owner, index and figureCount" in {
      figure.id shouldBe 1
      figure.owner shouldBe player
      figure.index shouldBe -1
      figure.figureCount shouldBe 4
    }

    "calculate adjustedIndex and newAdjustedIndex" in {
      noException should be thrownBy figure.adjustedIndex(8)
      noException should be thrownBy figure.newAdjustedIndex(8, 6)
    }

    "check if on board, on start, on goal" in {
      figure.isOnBoard shouldBe false
      figure.isOnStart shouldBe false
      figure.isOnGoal(8) shouldBe false
    }

    "check for collision" in {
      noException should be thrownBy figure.checkForCollision(other, 8)
      noException should be thrownBy figure.checkForPossibleCollision(
        other,
        8,
        Position.Home(0)
      )
    }

    "copy itself" in {
      val copy = figure.copy(id = 3)
      copy.id shouldBe 3
      copy.owner shouldBe player
      copy.index shouldBe -1
      copy.figureCount shouldBe 4
    }
  }
}
