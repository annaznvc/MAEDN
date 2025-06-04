package de.htwg.se.MAEDN.model.StrategyImp

import de.htwg.se.MAEDN.model.{IBoard, IFigure, IMoveStrategy, Collision}
import de.htwg.se.MAEDN.util.Position

class KickFigureStrategy extends IMoveStrategy {
  override def moveFigure(
      figure: IFigure,
      figures: List[IFigure],
      size: Int,
      rolled: Int
  ): List[IFigure] = {
    figures.map { f =>
      val movingPos = figure.adjustedIndex(size)
      val targetPos = f.adjustedIndex(size)
      (movingPos, targetPos) match {
        case (Position.Goal(_), _) | (_, Position.Goal(_)) =>
          f // No kicking in goal
        case _
            if figure.checkForCollision(
              f,
              size
            ) == Collision.EnemyCollision && f.isOnBoard && f.owner != figure.owner =>
          f.copy(index = -1) // Send enemy home
        case _ => f
      }
    }
  }

  // Checks if there are collisions with other figures
  override def canMove(
      figure: IFigure,
      figures: List[IFigure],
      size: Int,
      rolled: Int
  ): Boolean =
    figures.exists(f =>
      f.checkForCollision(figure, size) == Collision.EnemyCollision
    )
}
