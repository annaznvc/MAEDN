package test

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import util.Dice

class DiceSpec extends AnyWordSpec with Matchers {

  "The Dice" should {

    "return a number between 1 and 6" in {
      val roll = Dice.roll()
      roll should be >= 1
      roll should be <= 6
    }

    "produce different results over multiple rolls" in {
      val rolls = (1 to 100).map(_ => Dice.roll())
      rolls.distinct.size should be > 1
    }

    "never return a number outside 1 to 6" in {
      val rolls = (1 to 1000).map(_ => Dice.roll())
      all(rolls) should (be >= 1 and be <= 6)
    }
  }
}
