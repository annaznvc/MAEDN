import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._

class FigureTest extends AnyWordSpec with Matchers:

  "A Figure" should {

    "store id, color and state correctly" in {
      val fig = Figure(1, Color.Red, Home)
      fig.id shouldBe 1
      fig.color shouldBe Color.Red
      fig.state shouldBe Home
    }

    "support copying with a new state" in {
      val original = Figure(2, Color.Blue, Home)
      val moved = original.copy(state = OnBoard(Position(3, 4)))

      moved.id shouldBe 2
      moved.color shouldBe Color.Blue
      moved.state shouldBe OnBoard(Position(3, 4))
    }

    "fail if the figure ID is negative" in {
      val ex = intercept[IllegalArgumentException] {
        Figure(-5, Color.Yellow, Home)
      }
      ex.getMessage should include ("Figure ID must be between 0 and 15")
    }

    "only allow IDs between 0 and 15" in {
      val validIDs = 0 to 15

      for (id <- validIDs) {
        noException should be thrownBy Figure(id, Color.Red, Home)
      }

      an [IllegalArgumentException] should be thrownBy Figure(-1, Color.Red, Home)
      an [IllegalArgumentException] should be thrownBy Figure(16, Color.Red, Home)
      an [IllegalArgumentException] should be thrownBy Figure(99, Color.Red, Home)
    }
  }
