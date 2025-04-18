package de.htwg.se.MAEDN.model

case class Figure(id: Int, color: Color, state: PositionState):
  require(id >= 0 && id <= 15, "Figure ID must be between 0 and 15")

  def isAtHome: Boolean = state == Home

  def isFinished: Boolean = state == Finished

  def isOnBoard: Boolean = state match
    case OnBoard(_) => true
    case _ => false

  def isInGoal: Boolean = state match
    case Goal(_) => true
    case _ => false