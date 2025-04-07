package model

case class Figure(id: Int, color: Color, state: PositionState):
  require(id >= 0, "Figure ID must be non-negative")