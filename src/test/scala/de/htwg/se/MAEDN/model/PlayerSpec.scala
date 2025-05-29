package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Color
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class PlayerSpec extends AnyWordSpec with Matchers {

  "A Player" should {

    "have correct id, color and figures" in {
      val player = Player(1, Nil, Color.BLUE)
      player.id shouldBe 1
      player.color shouldBe Color.BLUE
      player.figures shouldBe empty
    }

    "compute correct startPosition based on color offset" in {
      val playerRed = Player(1, Nil, Color.RED)
      val playerGreen = Player(2, Nil, Color.GREEN)
      val playerYellow = Player(3, Nil, Color.YELLOW)

      playerRed.startPosition(8) shouldBe 0 // RED offset = 0
      playerGreen.startPosition(8) shouldBe 16 // GREEN offset = 2
      playerYellow.startPosition(8) shouldBe 24 // YELLOW offset = 3
    }

    "support updating figures via copy" in {
      val player = Player(1, Nil, Color.RED)
      val f1 = Figure(1, player, 0, 4)
      val f2 = Figure(2, player, 1, 4)
      val updated = player.copy(figures = List(f1, f2))

      updated.figures should contain allOf (f1, f2)
    }
  }
}
