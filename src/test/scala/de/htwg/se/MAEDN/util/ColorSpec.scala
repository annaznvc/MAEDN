import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.util.Color

class ColorSpec extends AnyWordSpec with Matchers {

  "Color enum" should {
    "contain all expected values" in {
      Color.values should contain allOf (Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.WHITE)
    }
  }
}
