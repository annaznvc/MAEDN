package de.htwg.se.MAEDN.util

import scala.collection.mutable.Queue

trait Observer {
  def processEvent(event: Event): Unit
}

class Observable {
  var subscribers: Vector[Observer] = Vector()
  val eventQueue: Queue[Event] = Queue()

  def add(s: Observer): Unit = subscribers = subscribers :+ s
  def remove(s: Observer): Unit = subscribers =
    subscribers.filterNot(o => o == s)

  def instantNotifyObservers(event: Event): Unit = {
    subscribers.foreach(_.processEvent(event))
  }

  def notifyObservers(): Unit = {
    while (eventQueue.nonEmpty) {
      val e = eventQueue.dequeue()
      subscribers.foreach(_.processEvent(e))
    }
  }
}
