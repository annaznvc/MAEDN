import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.util.Dice

class DiceSpec extends AnyWordSpec with Matchers {

  "Dice" should {
    "roll a number between 1 and 6" in {
      val result = Dice.roll()
      result should (be >= 1 and be <= 6)
    }

    "roll multiple numbers between 1 and 6" in {
      val results = Dice.rollMultiple(5)
      results should have size 5
      all(results) should (be >= 1 and be <= 6)
    }
  }
}
