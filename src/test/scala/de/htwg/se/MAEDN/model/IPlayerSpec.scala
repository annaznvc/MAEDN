package de.htwg.se.MAEDN.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.util.Color

class IPlayerSpec extends AnyWordSpec with Matchers {
  "An IPlayer" should {
    val player = IPlayer(1, Nil, Color.RED)

    "have an id, figures and color" in {
      player.id shouldBe 1
      player.figures shouldBe Nil
      player.color shouldBe Color.RED
    }

    "calculate start position based on board size" in {
      player.startPosition(8) shouldBe (player.color.offset * 8)
    }

    "copy itself with new values" in {
      val newFigures = Nil
      val newPlayer =
        player.copy(id = 2, figures = newFigures, color = Color.BLUE)
      newPlayer.id shouldBe 2
      newPlayer.figures shouldBe newFigures
      newPlayer.color shouldBe Color.BLUE
    }
  }
}
