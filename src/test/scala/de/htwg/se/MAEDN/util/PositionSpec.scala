package de.htwg.se.MAEDN.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class PositionSpec extends AnyWordSpec with Matchers {

  "A Position" should {

    "be creatable as Home with a value" in {
      val pos = Position.Home(0)
      pos shouldBe a [Position.Home]
      pos.value shouldBe 0
    }

    "be creatable as Normal with a value" in {
      val pos = Position.Normal(5)
      pos shouldBe a [Position.Normal]
      pos.value shouldBe 5
    }

    "be creatable as Goal with a value" in {
      val pos = Position.Goal(3)
      pos shouldBe a [Position.Goal]
      pos.value shouldBe 3
    }

    "be creatable as OffBoard with a value" in {
      val pos = Position.OffBoard(-1)
      pos shouldBe a [Position.OffBoard]
      pos.value shouldBe -1
    }

    "create correct Start position via Position.Start" in {
      val redStart = Position.Start(10, Color.RED)   // RED offset = 0
      val blueStart = Position.Start(10, Color.BLUE) // BLUE offset = 1
      redStart shouldBe Position.Normal(0)
      blueStart shouldBe Position.Normal(10)
    }
  }
}
