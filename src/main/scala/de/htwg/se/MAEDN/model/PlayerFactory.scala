package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Color

object PlayerFactory {

  def apply(playerCount: Int, figuresPerPlayer: Int): List[Player] = {
    Color.values
      .take(playerCount)
      .zipWithIndex
      .map { case (color, idx) =>
        val id = idx + 1
        val player = Player(id, Nil, color)
        val figs =
          (1 to figuresPerPlayer)
            .map(i => Figure(i, player, -1, figuresPerPlayer))
            .toList
        player.copy(figures = figs)
      }
      .toList
  }
}
