package de.htwg.se.MAEDN.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ObservableSpec extends AnyWordSpec with Matchers {
  "An Observable" should {
    "notify its observers immediately when instantNotifyObservers is called" in {
      val observable = new Observable
      var updated = false

      val observer = new Observer {
        override def processEvent(event: Event): Unit = {
          updated = true
        }
      }

      observable.add(observer)
      observable.instantNotifyObservers(Event.StartGameEvent)

      updated shouldBe true
    }

    "notify its observers via eventQueue when notifyObservers is called" in {
      val observable = new Observable
      var updated = false

      val observer = new Observer {
        override def processEvent(event: Event): Unit = {
          if (event == Event.ConfigEvent) updated = true
        }
      }

      observable.add(observer)
      observable.eventQueue.enqueue(Event.ConfigEvent)
      observable.notifyObservers()

      updated shouldBe true
    }

    "remove an observer correctly" in {
        val observable = new Observable
        var called = false

        val observer = new Observer {
            override def processEvent(event: Event): Unit = {
            called = true
            }
        }

        observable.add(observer)
        observable.remove(observer)
        observable.instantNotifyObservers(Event.StartGameEvent)

        called shouldBe false // weil observer entfernt wurde
        }

  }
}
