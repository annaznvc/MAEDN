package de.htwg.se.MAEDN.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class EventSpec extends AnyWordSpec with Matchers {

  "An Event" should {

    "have correct priority for standard events" in {
      Event.StartGameEvent.priority shouldBe 0
      Event.QuitGameEvent.priority shouldBe 0
      Event.BackToMenuEvent.priority shouldBe 0
      Event.ConfigEvent.priority shouldBe 0
    }

  }
}
