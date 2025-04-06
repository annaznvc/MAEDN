package model

sealed trait PositionState
case object Home extends PositionState
case class OnBoard(pos: Position) extends PositionState
case object Finished extends PositionState
