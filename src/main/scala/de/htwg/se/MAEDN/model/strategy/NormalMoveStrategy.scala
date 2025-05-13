package de.htwg.se.MAEDN.model.strategy

import de.htwg.se.MAEDN.model.{Board, Figure, IMoveStrategy}

class NormalMoveStrategy extends IMoveStrategy {
  override def moveFigure(
      figure: Figure,
      figures: List[Figure],
      size: Int,
      rolled: Int
  ): List[Figure] = {
    // Check if there is a figure with the same adjustedIndex and the same color
    if (
      figures
        .exists(f =>
          f != figure
            && f.adjustedIndex(size) == figure.adjustedIndex(size)
            && f.owner.color == figure.owner.color
        )
    ) {
      figures
    } else {
      // Move the figure to the new position
      figures.map { f =>
        if (f == figure) f.copy(index = figure.adjustedIndex(size)) else f
      }
    }
  }
}
