import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._

class ColorTest extends AnyWordSpec with Matchers:

  "Color enum" should {

    "contain all defined colors" in {
      Color.Red.toString shouldBe "Red"
      Color.Blue.toString shouldBe "Blue"
      Color.Green.toString shouldBe "Green"
      Color.Yellow.toString shouldBe "Yellow"
    }
  }

