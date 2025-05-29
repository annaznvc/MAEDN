package de.htwg.se.MAEDN.model.strategy

import de.htwg.se.MAEDN.model.{Board, Figure, IMoveStrategy, Collision}
import de.htwg.se.MAEDN.util.Position

class NormalMoveStrategy extends IMoveStrategy {
  override def moveFigure(
      figure: Figure,
      figures: List[Figure],
      size: Int,
      rolled: Int
  ): List[Figure] = {
    if (canMove(figure, figures, size, rolled)) {
      // Move the figure to the new position
      figures.map { f =>
        if (f == figure) f.copy(index = figure.index + rolled)
        else f
      }
    } else {
      figures
    }
  }

  override def canMove(
      figure: Figure,
      figures: List[Figure],
      size: Int,
      rolled: Int
  ): Boolean = {
    figure.newAdjustedIndex(size, rolled) match {
      case Position.Normal(_) =>
        !figures.exists(f =>
          f.checkForPossibleCollision(
            figure,
            size,
            figure.newAdjustedIndex(size, rolled)
          ) == Collision.OwnCollision
        )
      case Position.Goal(_) =>
        !figures.exists(f =>
          f.checkForPossibleCollision(
            figure,
            size,
            figure.newAdjustedIndex(size, rolled)
          ) == Collision.OwnCollision
        )
      case Position.OffBoard(_) => false
      case Position.Home(_)     => false
    }
  }
}
