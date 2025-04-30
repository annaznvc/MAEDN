

/**
 * 
 * 
 * 
 package de.htwg.se.MAEDN.controller

import de.htwg.se.MAEDN.util.Dice
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class GameControllerSpec extends AnyWordSpec with Matchers {

  "A GameController" should {
    "initialize with one player, one figure, and deterministic dice" in {
      val fixedDice = new Dice(fixedRoll = Some(6))
      val controller = new GameController(List("TestPlayer"), boardSize = 10, figuresPerPlayer = 1, testDice = Some(fixedDice))

      controller.allPlayers should have size 1
      controller.allPlayers.head.name shouldBe "TestPlayer"

      val roll = controller.rollDice()
      roll shouldBe 6

      val moveSuccess = controller.moveFigure(1, roll)
      moveSuccess shouldBe true
    }
  }
}
*/


