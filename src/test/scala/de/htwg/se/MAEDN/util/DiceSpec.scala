package de.htwg.se.MAEDN.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class DiceSpec extends AnyWordSpec with Matchers {

  "A Dice" should {

    "return a number between 1 and 6" in {
      val dice = new Dice()
      val roll = dice.roll()
      roll should be >= 1
      roll should be <= 6
    }

    "produce different results over multiple rolls" in {
      val dice = new Dice()
      val rolls = (1 to 100).map(_ => dice.roll())
      rolls.distinct.size should be > 1
    }

    "never return a number outside 1 to 6" in {
      val dice = new Dice()
      val rolls = (1 to 1000).map(_ => dice.roll())
      all(rolls) should (be >= 1 and be <= 6)
    }

  }

}
