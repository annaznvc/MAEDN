
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._

class PlayerStatusTest extends AnyWordSpec with Matchers:

  "PlayerStatus" should {

    "be Active by default in Player" in {
      val figs = (1 to 4).map(i => Figure(i, Color.Red, Home)).toList
      val player = Player(999, "Test", Color.Red, figs)
      player.status shouldBe Active
    }

    "allow valid placement in Out" in {
      val out = Out(2)
      out.placement shouldBe 2
    }

    "fail for invalid placement below 1" in {
      val ex = intercept[IllegalArgumentException] {
        Out(0)
      }
      ex.getMessage should include ("Placement must be between 1 and 4")
    }

    "fail for invalid placement above 4" in {
      val ex = intercept[IllegalArgumentException] {
        Out(5)
      }
      ex.getMessage should include ("Placement must be between 1 and 4")
    }
  }
