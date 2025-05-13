package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.model.Manager

trait Command {
  def execute(): Manager
}
