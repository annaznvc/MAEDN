package model

case class Figure(id: Int, color: Color, state: PositionState):
  require(id >= 0 && id <= 15, "Figure ID must be between 0 and 15")
