
package test

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._

class PlayerSpec extends AnyWordSpec with Matchers {

  val figures = (1 to 4).map(id => Figure(id, Color.Red, Home)).toList
  val player = Player(1, "Alice", Color.Red, figures)

  "A Player" should {

    "have the correct name, id and color" in {
      player.name shouldBe "Alice"
      player.id shouldBe 1
      player.color shouldBe Color.Red
    }

    "have 4 figures" in {
      player.figures.size shouldBe 4
    }

    "start with all figures at home" in {
      player.figures.forall(_.state == Home) shouldBe true
    }

    "contain figures with unique IDs" in {
      player.figures.map(_.id).distinct.size shouldBe 4
    }
  }
}
