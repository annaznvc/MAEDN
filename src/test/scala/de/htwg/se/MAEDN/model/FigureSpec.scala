import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model.{Figure, Player}
import de.htwg.se.MAEDN.util.Color

class FigureSpec extends AnyWordSpec with Matchers {

  "A Figure" should {
    "have an ID and an owner" in {
      val player = Player(1, Nil, Color.GREEN)
      val figure = Figure(3, player)

      figure.id shouldBe 3
      figure.owner shouldBe player
    }
  }
}
