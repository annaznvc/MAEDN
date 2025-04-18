package de.htwg.se.MAEDN.model

sealed trait PositionState //abstrakter Obertyp

case object Home extends PositionState //object weil Variante ohne Daten

case class OnBoard(pos: Position) extends PositionState

case class Goal(pos: Position) extends PositionState

case object Finished extends PositionState
