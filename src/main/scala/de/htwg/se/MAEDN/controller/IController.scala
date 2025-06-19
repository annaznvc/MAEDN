package de.htwg.se.MAEDN.controller

import de.htwg.se.MAEDN.model.IManager
import de.htwg.se.MAEDN.util.{Event, Observer}
import de.htwg.se.MAEDN.model.IMemento
import scala.collection.mutable.PriorityQueue
import de.htwg.se.MAEDN.controller.command.Command

trait IController {
  var manager: IManager
  def executeCommand(command: Command): Unit
  def add(observer: Observer): Unit
  def remove(observer: Observer): Unit
  def undoStack: scala.collection.mutable.Stack[IMemento]
  def redoStack: scala.collection.mutable.Stack[IMemento]
  def eventQueue: PriorityQueue[Event]

  def enqueueEvent(event: Event): Unit

}
