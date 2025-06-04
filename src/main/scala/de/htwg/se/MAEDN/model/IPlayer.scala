package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.model.PlayerImp.Player

/** Interface for Player entities in the MAEDN game. Defines the contract for
  * player objects that manage game figures.
  */
trait IPlayer {

  /** Unique identifier for the player */
  val id: Int

  /** List of figures owned by this player */
  var figures: List[IFigure]

  /** Color assigned to this player */
  val color: Color

  /** Calculates the start position for this player based on board size
    * @param size
    *   the size of the board
    * @return
    *   the start position index
    */
  def startPosition(size: Int): Int

  /** Creates a copy of the player with updated properties This method allows
    * for creating a new player instance with modified properties while keeping
    * the original instance unchanged.
    * @param id
    *   the new unique identifier for the player
    * @param figures
    *   the new list of figures owned by the player
    * @param color
    *   the new color assigned to the player
    * @return
    *   a new player instance with the specified properties
    */
  def copy(
      id: Int = id,
      figures: List[IFigure] = figures,
      color: Color = color
  ): IPlayer = {
    Player(id, figures, color)
  }
}

object IPlayer {

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
  def apply(id: Int, figures: List[IFigure], color: Color): IPlayer = {
    Player(id, figures, color)
  }
}
