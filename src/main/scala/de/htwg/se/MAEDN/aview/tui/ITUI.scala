package de.htwg.se.MAEDN.aview.tui

import de.htwg.se.MAEDN.util.Event

trait ITUI {
  def run(): Unit
  def processEvent(event: Event): Unit
}
