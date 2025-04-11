package model

sealed trait PositionState //abstrakter Obertyp

case object Home extends PositionState //object weil Variante ohne Daten

case class Start(pos: Position) extends PositionState //case class weil brauchen klare position z.b (5,8) oder so

case class OnBoard(pos: Position) extends PositionState

case object Finished extends PositionState
