package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.model.IManager

import scala.util.Try

trait Command {
  def execute(): Try[IManager]
  def isNormal: Boolean = true
}
