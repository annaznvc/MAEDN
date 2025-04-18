package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.model._

object BoardHelper:

  def generateFields(): List[Field] =
    val board = new Board()
    board.fields