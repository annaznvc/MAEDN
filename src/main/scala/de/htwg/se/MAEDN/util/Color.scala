package de.htwg.se.MAEDN.util

sealed trait Color {
  def offset: Int
}

object Color {
  case object RED extends Color { val offset: Int = 0 }
  case object BLUE extends Color { val offset: Int = 1 }
  case object GREEN extends Color { val offset: Int = 2 }
  case object YELLOW extends Color { val offset: Int = 3 }
  case object WHITE extends Color { val offset: Int = -1 }

  val values: List[Color] = List(RED, BLUE, GREEN, YELLOW)
}
