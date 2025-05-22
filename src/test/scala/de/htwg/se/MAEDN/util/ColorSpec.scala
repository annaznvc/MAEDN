package de.htwg.se.MAEDN.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ColorSpec extends AnyWordSpec with Matchers {

  "A Color trait" should {

    "have defined offsets for each color" in {
      Color.RED.offset shouldBe 0
      Color.BLUE.offset shouldBe 1
      Color.GREEN.offset shouldBe 2
      Color.YELLOW.offset shouldBe 3
      Color.WHITE.offset shouldBe -1
    }

    "include only the four game colors in values" in {
      Color.values should contain theSameElementsAs List(
        Color.RED,
        Color.BLUE,
        Color.GREEN,
        Color.YELLOW
      )
      Color.values should not contain Color.WHITE
    }

    "allow lookup by name manually using pattern matching" in {
      def fromString(name: String): Option[Color] = name.toUpperCase match {
        case "RED"    => Some(Color.RED)
        case "BLUE"   => Some(Color.BLUE)
        case "GREEN"  => Some(Color.GREEN)
        case "YELLOW" => Some(Color.YELLOW)
        case "WHITE"  => Some(Color.WHITE)
        case _        => None
      }

      fromString("red") shouldBe Some(Color.RED)
      fromString("WHITE") shouldBe Some(Color.WHITE)
      fromString("unknown") shouldBe None
    }
  }
}
