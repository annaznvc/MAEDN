package de.htwg.se.MAEDN.aview

import org.jline.terminal.{TerminalBuilder, Terminal}

import de.htwg.se.MAEDN.util.Observer
import de.htwg.se.MAEDN.controller.Controller

class Tui(controller: Controller) extends Observer {

  controller.add(this)
  
  private def writeline(s: String): Unit = {
    terminal.writer().println(s)
    terminal.flush()
  }

  val terminal: Terminal = TerminalBuilder.builder().system(true).build()

  override def update: Unit = {
    writeline("Controller has been updated.")
  }
}