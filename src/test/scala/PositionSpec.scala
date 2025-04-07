package test

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._

class PositionSpec extends AnyWordSpec with Matchers {

  "A Position" should {

    "store x and y values correctly" in {
      val pos = Position(4, 9)
      pos.x shouldBe 4
      pos.y shouldBe 9
    }

    "be equal to another position with same coordinates" in {
      val p1 = Position(2, 3)
      val p2 = Position(2, 3)
      p1 shouldBe p2
    }

    "not be equal if coordinates differ" in {
      val p1 = Position(5, 5)
      val p2 = Position(5, 6)
      p1 should not be p2
    }
  }
}
