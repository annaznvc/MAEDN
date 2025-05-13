package de.htwg.se.MAEDN.controller.command

trait Command {
  def execute(): Unit
  def undo(): Unit = ()
}
