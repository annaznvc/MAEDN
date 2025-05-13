package de.htwg.se.MAEDN.model.strategy

import de.htwg.se.MAEDN.model.{Board, Figure, IMoveStrategy}

class KickFigureStrategy extends IMoveStrategy {
  override def moveFigure(
      figure: Figure,
      figures: List[Figure],
      size: Int,
      rolled: Int
  ): List[Figure] = {
    figures
      .map { otherFigure =>
        if (
          otherFigure != figure && otherFigure.adjustedIndex(size) == figure
            .adjustedIndex(size)
        ) {
          otherFigure.copy(index = -1) // Move the collided figure to home
        } else {
          otherFigure
        }
      }
  }
}
