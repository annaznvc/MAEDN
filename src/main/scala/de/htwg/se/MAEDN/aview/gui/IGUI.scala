package de.htwg.se.MAEDN.aview.gui

import de.htwg.se.MAEDN.util.Event

trait IGUI {
  def start(): Unit
  def processEvent(event: Event): Unit
  def main(args: Array[String]): Unit
}
