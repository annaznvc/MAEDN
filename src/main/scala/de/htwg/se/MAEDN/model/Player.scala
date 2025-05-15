package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Color

// A Player owns multiple Figures
case class Player(
    id: Int,
    var figures: List[Figure],
    color: Color
) {
  def startPosition(size: Int): Int = color.offset * size

  override def toString: String = s"Player($id, $color)" // 💥 FIX: kein Rekursionszugriff auf figures
}
