package model

sealed trait PlayerStatus
case object Active extends PlayerStatus
case class Out(placement: Int) extends PlayerStatus:
  require(placement >= 1 && placement <= 4, "Placement must be between 1 and 4")
