package de.htwg.se.MAEDN.model.strategy

import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util.Position
import de.htwg.se.MAEDN.util.Color
// import de.htwg.se.MAEDN.model.StrategyImp.KickFigureStrategy

class NormalMoveStrategy extends MoveStrategy {
  override def moveFigure(
      figure: Figure,
      figures: List[Figure],
      size: Int,
      rolled: Int
  ): List[Figure] = {
    val newPos = figure.newAdjustedIndex(size, rolled)
    val ownCollision = figures.exists(f =>
      f != figure && f.owner.color == figure.owner.color && f.adjustedIndex(
        size
      ) == newPos
    )
    if (canMove(figure, figures, size, rolled) && !ownCollision) {
      figures.map {
        case f if f == figure => f.copy(index = figure.index + rolled)
        case f                => f
      }
    } else figures
  }

  override def canMove(
      figure: Figure,
      figures: List[Figure],
      size: Int,
      rolled: Int
  ): Boolean = {
    val newIndex = figure.index + rolled
    val figureCount = figure.figureCount
    if (newIndex >= 4 * size + figureCount) return false
    if (
      figure.index < 4 * size && newIndex >= 4 * size && newIndex >= 4 * size + figureCount
    ) return false
    val targetPos = figure.newAdjustedIndex(size, rolled)
    targetPos match {
      case Position.Goal(goalIdx) =>
        val ownInGoal = figures.exists(f =>
          f.owner == figure.owner && f.index == newIndex && f != figure
        )
        if (ownInGoal) return false
      case _ =>
    }
    val ownCollision = figures.exists(f =>
      f.checkForPossibleCollision(
        figure,
        size,
        targetPos
      ) == Collision.OwnCollision
    )
    if (ownCollision) return false
    targetPos match {
      case Position.Normal(_) | Position.Goal(_) => true
      case _                                     => false
    }
  }
}
