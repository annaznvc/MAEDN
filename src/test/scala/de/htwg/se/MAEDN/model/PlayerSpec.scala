package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.model.{Player, Figure}
import de.htwg.se.MAEDN.util.Color
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class PlayerSpec extends AnyWordSpec with Matchers {

  "A Player" should {

    "have a correct toString representation without recursion" in {
      val player = Player(
        id = 1,
        figures = List(), // leer, damit keine Probleme
        color = Color.BLUE
      )

      player.toString shouldBe "Player(1, BLUE)"
    }
  }
}
