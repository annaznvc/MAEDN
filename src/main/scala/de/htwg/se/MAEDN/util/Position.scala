package de.htwg.se.MAEDN.util

enum Position(val value: Int):
  case Home(override val value: Int) extends Position(value)
  case Normal(override val value: Int) extends Position(value)
  case Goal(override val value: Int) extends Position(value)
  case OffBoard(override val value: Int) extends Position(value)

object Position:
  def Start(size: Int, color: Color): Position =
    Normal(color.offset * size)
