package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.model.Manager

trait Command {
  def doStep(): Unit
  def undoStep(): Unit
  def redoStep(): Unit
}
