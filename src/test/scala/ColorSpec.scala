package test

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.Color

class ColorSpec extends AnyWordSpec with Matchers {

  "The Color enum" should {

    "contain exactly four values" in {
      Color.values.size shouldBe 4
      Color.values should contain allOf (Color.Red, Color.Blue, Color.Green, Color.Yellow)
    }

    "return correct names with toString" in {
      Color.Red.toString shouldBe "Red"
      Color.Blue.toString shouldBe "Blue"
      Color.Green.toString shouldBe "Green"
      Color.Yellow.toString shouldBe "Yellow"
    }

    "support valueOf for valid names" in {
      Color.valueOf("Red") shouldBe Color.Red
      Color.valueOf("Blue") shouldBe Color.Blue
      Color.valueOf("Green") shouldBe Color.Green
      Color.valueOf("Yellow") shouldBe Color.Yellow
    }

    "throw IllegalArgumentException for invalid valueOf input" in {
      an [IllegalArgumentException] should be thrownBy Color.valueOf("Pink")
    }

    "support fromOrdinal for valid ordinals" in {
      Color.fromOrdinal(0) shouldBe Color.Red
      Color.fromOrdinal(1) shouldBe Color.Blue
      Color.fromOrdinal(2) shouldBe Color.Green
      Color.fromOrdinal(3) shouldBe Color.Yellow
    }

    "throw NoSuchElementException for invalid fromOrdinal index" in {
      an [NoSuchElementException] should be thrownBy Color.fromOrdinal(99)
    }

    "have correct ordinal values" in {
      Color.Red.ordinal shouldBe 0
      Color.Blue.ordinal shouldBe 1
      Color.Green.ordinal shouldBe 2
      Color.Yellow.ordinal shouldBe 3
    }

    "support productPrefix" in {
      Color.Red.productPrefix shouldBe "Red"
      Color.Green.productPrefix shouldBe "Green"
    }

    "support canEqual and productArity" in {
      Color.Red.canEqual(Color.Red) shouldBe true
      Color.Red.productArity shouldBe 0
    }

    "support productElementNames (empty for enums)" in {
      Color.Red.productElementNames.toList shouldBe empty
    }

    "throw IndexOutOfBoundsException for productElement" in {
      an [IndexOutOfBoundsException] should be thrownBy Color.Red.productElement(0)
    }

    "throw IndexOutOfBoundsException for productElementName" in {
      an [IndexOutOfBoundsException] should be thrownBy Color.Red.productElementName(0)
    }

    "support pattern matching for all values" in {
      def describe(color: Color): String = color match
        case Color.Red    => "warm"
        case Color.Blue   => "cool"
        case Color.Green  => "natural"
        case Color.Yellow => "bright"

      describe(Color.Red) shouldBe "warm"
      describe(Color.Blue) shouldBe "cool"
      describe(Color.Green) shouldBe "natural"
      describe(Color.Yellow) shouldBe "bright"
    }
  }
}
