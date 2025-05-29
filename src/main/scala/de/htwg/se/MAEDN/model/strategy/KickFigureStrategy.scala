package de.htwg.se.MAEDN.model.strategy

import de.htwg.se.MAEDN.model.{Board, Figure, IMoveStrategy, Collision}

class KickFigureStrategy extends IMoveStrategy {
  override def moveFigure(
      figure: Figure,
      figures: List[Figure],
      size: Int,
      rolled: Int
  ): List[Figure] = {
    figures.map { f =>
      if (
        figure.checkForCollision(
          f,
          size
        ) == Collision.EnemyCollision && f.isOnBoard && f.owner != figure.owner
      ) {
        // Move the enemy figure back to home
        f.copy(index = -1)
      } else {
        f
      }
    }
  }

  // Checks if there are collisions with other figures
  override def canMove(
      figure: Figure,
      figures: List[Figure],
      size: Int,
      rolled: Int
  ): Boolean = {
    figures.exists(f =>
      f.checkForCollision(figure, size) == Collision.EnemyCollision
    )
  }
}
