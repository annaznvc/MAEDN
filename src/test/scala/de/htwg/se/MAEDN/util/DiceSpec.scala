package de.htwg.se.MAEDN.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class DiceSpec extends AnyWordSpec with Matchers {

  "A Dice" should {

    "roll values between 1 and 6" in {
      for (_ <- 1 to 1000) {
        val result = Dice.roll()
        result should (be >= 1 and be <= 6)
      }
    }

    "roll multiple values with correct length and range" in {
      val results = Dice.rollMultiple(50)
      results should have size 50
      all(results) should (be >= 1 and be <= 6)
    }

    "produce all values from 1 to 6 over many rolls" in {
      val results = Dice.rollMultiple(10000)
      val unique = results.toSet
      unique should contain allElementsOf (1 to 6)
    }

    "produce roughly even distribution of values" in {
      val results = Dice.rollMultiple(10000)
      val counts = results.groupBy(identity).view.mapValues(_.size)
      val avg = results.size / 6.0
      counts.values.foreach { count =>
        count.toDouble should (be > avg * 0.8 and be < avg * 1.2)
      }
    }
  }
}
