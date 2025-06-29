package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.util.PlayerFactory
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class PlayerFactorySpec extends AnyWordSpec with Matchers {

  "PlayerFactory" should {

    "create the correct number of players" in {
      val players = PlayerFactory(playerCount = 2, figuresPerPlayer = 4)
      players should have size 2
    }

    "assign unique player ids starting from 1" in {
      val players = PlayerFactory(4, 1)
      players.map(_.id) shouldBe List(1, 2, 3, 4)
    }

    "assign correct colors from Color.values" in {
      val players = PlayerFactory(4, 1)
      players.map(_.color) shouldBe Color.values
    }

    "create correct number of figures per player" in {
      val players = PlayerFactory(2, 4)
      all(players.map(_.figures.size)) shouldBe 4
    }

  }
}
