/** The `Observable` class implements the Observer pattern with asynchronous
  * event notification and prioritization.
  *
  * ==Overview==
  *   - Maintains a list of `Observer` subscribers.
  *   - Uses a priority queue (`PriorityQueue[Event]`) to manage events, where
  *     lower priority values indicate higher priority.
  *   - Notifies observers asynchronously using Scala `Future`s.
  *
  * ==Members==
  *   - `add(s: Observer)`: Adds an observer to the subscribers list.
  *   - `remove(s: Observer)`: Removes an observer from the subscribers list.
  *   - `enqueueEvent(event: Event)`: Adds an event to the priority queue.
  *   - `instantNotifyObservers(event: Event)`: Immediately notifies all
  *     observers of a single event asynchronously.
  *   - `notifyObservers()`: Notifies all observers of all queued events in
  *     priority order, asynchronously.
  *   - `clearEvents()`: Clears all events from the queue.
  *
  * ==Usage==
  *   1. Create an instance of `Observable`. 2. Add observers implementing the
  *      `Observer` trait. 3. Enqueue events or use `instantNotifyObservers` for
  *      immediate notification. 4. Call `notifyObservers` to process all queued
  *      events.
  *
  * ==Threading==
  *   - All notifications are performed asynchronously using the provided
  *     `ExecutionContext` (defaults to global).
  *   - Exceptions in observer processing are caught and logged.
  *
  * ==Companion Object==
  *   - Provides an implicit `Ordering[Event]` for the priority queue.
  */
package de.htwg.se.MAEDN.util

import scala.collection.mutable.PriorityQueue
import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Failure, Success}

// Observer trait remains unchanged
trait Observer {
  def processEvent(event: Event): Unit
}

object Observable {
  // Ordering: lower priority value means higher priority
  implicit val eventOrdering: Ordering[Event] = new Ordering[Event] {
    override def compare(x: Event, y: Event): Int = {
      x.priority.compareTo(y.priority)
    }
  }
}

class Observable(using ec: ExecutionContext = ExecutionContext.global) {
  import Observable._

  private var subscribers: Vector[Observer] = Vector()
  val eventQueue: PriorityQueue[Event] = PriorityQueue.empty

  def add(s: Observer): Unit = subscribers = subscribers :+ s
  def remove(s: Observer): Unit = subscribers = subscribers.filterNot(_ == s)

  def enqueueEvent(event: Event): Unit = eventQueue.enqueue(event)

  def instantNotifyObservers(event: Event): Unit = {
    subscribers.foreach { observer =>
      Future {
        observer.processEvent(event)
      }.onComplete {
        case Failure(ex) => println(s"Observer failed: ${ex.getMessage}")
        case Success(_)  => // ok
      }
    }
  }

  def notifyObservers(): Unit = {
    while (eventQueue.nonEmpty) {
      val event = eventQueue.dequeue()
      subscribers.foreach { observer =>
        Future {
          observer.processEvent(event)
        }.onComplete {
          case Failure(ex) => println(s"Observer failed: ${ex.getMessage}")
          case Success(_)  => // ok
        }
      }
    }
  }

  def clearEvents(): Unit = eventQueue.clear()
}
