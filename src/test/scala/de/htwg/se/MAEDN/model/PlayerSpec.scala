import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model.{Figure, Player}
import de.htwg.se.MAEDN.util.Color

class PlayerSpec extends AnyWordSpec with Matchers {

  "A Player" should {
    "have an id, color, and a list of figures" in {
      val player = Player(2, Nil, Color.YELLOW)
      player.id shouldBe 2
      player.color shouldBe Color.YELLOW
      player.figures shouldBe empty
    }

    "contain its figures" in {
      val dummy = Player(1, Nil, Color.BLUE)
      val figures = List(Figure(1, dummy), Figure(2, dummy))
      val player = dummy.copy(figures = figures)

      player.figures should have size 2
      player.figures.map(_.id) should contain allOf (1, 2)
    }
  }
}
