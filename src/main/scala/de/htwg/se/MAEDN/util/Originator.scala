package de.htwg.se.MAEDN.util

trait Originator {
  def createMemento(): Memento
  def restoreMemento(m: Memento): Unit
}
