package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.module.Injectable

// A Player owns multiple Figures
case class Player(
    id: Int,
    var figures: List[Figure],
    color: Color
) extends Injectable {
  def startPosition(size: Int): Int = color.offset * size
}

object Player {

  /** Factory method to create a new player instance
    * @param id
    *   the player's unique identifier
    * @param figures
    *   the list of figures owned by the player
    * @param color
    *   the color assigned to the player
    * @return
    *   a new player instance
    */
  def apply(id: Int, figures: List[Figure], color: Color): Player =
    new Player(id, figures, color)
}
