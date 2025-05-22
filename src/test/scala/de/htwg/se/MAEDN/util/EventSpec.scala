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

    "have correct priority for game events" in {
      Event.PlayNextEvent(1).priority shouldBe 1
      Event.PlayDiceEvent(6).priority shouldBe 2
      Event.MoveFigureEvent(0).priority shouldBe 3
      Event.ChangeSelectedFigureEvent(42).priority shouldBe 4
      Event.KickFigureEvent.priority shouldBe 5
    }

    "have correct priority for command events" in {
      Event.UndoEvent.priority shouldBe 6
      Event.RedoEvent.priority shouldBe 6
    }

    "have correct priority for error events" in {
      Event.ErrorEvent("Something went wrong").priority shouldBe 7
    }

    "be comparable via priority ordering" in {
      val sorted = List(
        Event.MoveFigureEvent(1), // 3
        Event.StartGameEvent, // 0
        Event.ErrorEvent("err"), // 7
        Event.PlayNextEvent(9), // 1
        Event.ChangeSelectedFigureEvent(5) // 4
      ).sorted(Observable.eventOrdering)

      sorted.map(_.priority) shouldBe Seq(0, 1, 3, 4, 7)
    }
  }
}
