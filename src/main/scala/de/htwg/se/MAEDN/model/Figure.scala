package de.htwg.se.MAEDN.model

// A Figure belongs to a Player and moves across Fields
case class Figure(
    id: Int,
    owner: Player,
    index: Int
) {
  def adjustedIndex(size: Int): Int = {
    val offset = owner.color.offset
    ((index + offset * size) % (size * 4))
  }

  def isOnBoard: Boolean = index >= 0
  def isOnStart: Boolean = index == 0
}
