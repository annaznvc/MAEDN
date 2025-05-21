package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.model.Manager

import scala.util.Try

trait Command {
  def execute(): Try[Manager]
  def isNormal: Boolean = true
}
