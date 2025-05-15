package de.htwg.se.MAEDN.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ObservableSpec extends AnyWordSpec with Matchers {

  "An Observable" should {

    "allow observers to be added and removed" in {
      val observable = new Observable
      val observer = new TestObserver

      observable.subscribers shouldBe empty
      observable.add(observer)
      observable.subscribers should contain(observer)

      observable.remove(observer)
      observable.subscribers should not contain observer
    }

    "call processEvent on all subscribers via instantNotifyObservers" in {
      val observable = new Observable
      val observer1 = new TestObserver
      val observer2 = new TestObserver

      observable.add(observer1)
      observable.add(observer2)

      val event = Event.StartGameEvent
      observable.instantNotifyObservers(event)

      observer1.events should contain(event)
      observer2.events should contain(event)
    }

    "enqueue and dispatch events in order via notifyObservers" in {
      val observable = new Observable
      val observer = new TestObserver
      observable.add(observer)

      val events = List(Event.StartGameEvent, Event.QuitGameEvent)
      observable.eventQueue.enqueueAll(events)

      observable.notifyObservers()

      observer.events shouldBe events
      observable.eventQueue shouldBe empty
    }
  }

  class TestObserver extends Observer {
    var events: List[Event] = List.empty

    override def processEvent(event: Event): Unit = {
      events = events :+ event
    }
  }
}
