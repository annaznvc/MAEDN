package de.htwg.se.MAEDN.util

import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.model.{IPlayer, IFigure}

object PlayerFactory {

  def apply(playerCount: Int, figuresPerPlayer: Int): List[IPlayer] = {
    Color.values
      .take(playerCount)
      .zipWithIndex
      .map { case (color, idx) =>
        val id = idx + 1
        val player = IPlayer(id, Nil, color)
        val figs =
          (1 to figuresPerPlayer)
            .map(i => IFigure(i, player, -1, figuresPerPlayer))
            .toList
        player.copy(figures = figs)
      }
      .toList
  }
}
