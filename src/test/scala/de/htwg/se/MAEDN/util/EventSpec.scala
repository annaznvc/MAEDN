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
      Event.PlayNextEvent(1).priority shouldBe 2
      Event.PlayDiceEvent(6).priority shouldBe 3
      Event.MoveFigureEvent(0).priority shouldBe 4
      Event.ChangeSelectedFigureEvent(42).priority shouldBe 5
      Event.KickFigureEvent.priority shouldBe 6
    }

    "have correct priority for command events" in {
      Event.UndoEvent.priority shouldBe 7
      Event.RedoEvent.priority shouldBe 7
    }

    "have correct priority for error events" in {
      Event.ErrorEvent("Something went wrong").priority shouldBe 8
    }

    "be comparable via priority ordering" in {
      val sorted = List(
        Event.MoveFigureEvent(1), // 4
        Event.StartGameEvent, // 0
        Event.ErrorEvent("err"), // 8
        Event.PlayNextEvent(9), // 2
        Event.ChangeSelectedFigureEvent(5) // 5
      ).sorted(Observable.eventOrdering)

      sorted.map(_.priority) shouldBe Seq(0, 2, 4, 5, 8)
    }
  }
}
