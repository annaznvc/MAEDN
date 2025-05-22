package de.htwg.se.MAEDN.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}
import scala.util.Try

class ObservableSpec extends AnyWordSpec with Matchers {

  "An Observable" should {

    "add and remove observers" in {
      val observable = new Observable
      val observer = new Observer {
        override def processEvent(event: Event): Unit = ()
      }

      observable.add(observer)
      observable.remove(observer)
      // Test succeeds if no exception occurs
    }

    "enqueue and clear events" in {
      val observable = new Observable
      observable.enqueueEvent(Event.MoveFigureEvent(0))
      observable.eventQueue.nonEmpty shouldBe true
      observable.clearEvents()
      observable.eventQueue.isEmpty shouldBe true
    }

    "notify observers immediately with instantNotifyObservers" in {
      var wasCalled = false
      val observer = new Observer {
        override def processEvent(event: Event): Unit = {
          wasCalled = true
          event shouldBe Event.StartGameEvent
        }
      }

      val observable = new Observable
      observable.add(observer)
      observable.instantNotifyObservers(Event.StartGameEvent)
      wasCalled shouldBe true
    }

    "notify all observers asynchronously with notifyObservers" in {
      val promise1 = Promise[Boolean]()
      val promise2 = Promise[Boolean]()

      val observer1 = new Observer {
        override def processEvent(event: Event): Unit = promise1.success(true)
      }

      val observer2 = new Observer {
        override def processEvent(event: Event): Unit = promise2.success(true)
      }

      val observable = new Observable
      observable.add(observer1)
      observable.add(observer2)
      observable.enqueueEvent(Event.PlayDiceEvent(6))
      observable.notifyObservers()

      Await.result(promise1.future, 1.second) shouldBe true
      Await.result(promise2.future, 1.second) shouldBe true
    }

    "handle exceptions in observers gracefully (instant and async)" in {
      val crashingObserver = new Observer {
        override def processEvent(event: Event): Unit =
          throw new RuntimeException("expected failure")
      }

      val observable = new Observable
      observable.add(crashingObserver)

      noException should be thrownBy {
        observable.instantNotifyObservers(Event.ConfigEvent)
        observable.enqueueEvent(Event.ErrorEvent("fail"))
        observable.notifyObservers()
        Thread.sleep(100) // wait for async
      }
    }

  }
}
