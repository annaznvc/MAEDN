
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._

class PlayerStatusSpec extends AnyWordSpec with Matchers:

  "PlayerStatus" should {

    "be Active by default in Player" in {
      val figs = (1 to 4).map(i => Figure(i, Color.Red, Home)).toList
      val player = Player(999, "Test", Color.Red, figs)
      player.status shouldBe Active
    }
  }