package de.htwg.se.MAEDN.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.controller._
import de.htwg.se.MAEDN.util._

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


    "support productPrefix" in { //gibt Namen des Enum-EIntrags als String zurück
      Color.Red.productPrefix shouldBe "Red"
      Color.Green.productPrefix shouldBe "Green"
      Color.Yellow.productPrefix shouldBe "Yellow"
      Color.Blue.productPrefix shouldBe "Blue"
    }

    "support canEqual" in { //prüfen, ob objekte vom gleichen typ sind
      Color.Red.canEqual(Color.Red) shouldBe true
    }

  }
}
