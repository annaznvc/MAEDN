package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Color

object PlayerFactory {

  /** Creates a list of players with the given colors and figures per player.
    *
    * @param colors
    *   The list of colors for the players.
    * @param figuresPerPlayer
    *   The number of figures each player should have.
    * @return
    *   A list of players with the specified colors and figures.
    */
  def createPlayers(playerCount: Int, figuresPerPlayer: Int): List[Player] = {
    Color.values
      .take(playerCount)
      .zipWithIndex
      .map { case (color, idx) =>
        val id = idx + 1
        val player = new Player(id, Nil, color)
        val figs =
          (1 to figuresPerPlayer).map(i => Figure(i, player, -1)).toList
        player.figures = figs
        player
      }
      .toList
  }
}
