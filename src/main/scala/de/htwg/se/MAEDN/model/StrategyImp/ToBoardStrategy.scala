package de.htwg.se.MAEDN.model.StrategyImp

import de.htwg.se.MAEDN.model.{IBoard, IMoveStrategy, IFigure, Collision}
import de.htwg.se.MAEDN.util.Position

/** The OnBoardStrategy is responsible for moving a figure from home to the
  * start position when a 6 is rolled. It is used when the figure is not on the
  * board.
  */
class ToBoardStrategy extends IMoveStrategy {
  override def moveFigure(
      figure: IFigure,
      figures: List[IFigure],
      size: Int,
      rolled: Int
  ): List[IFigure] = {
    if (!canMove(figure, figures, size, rolled)) {
      figures
    } else {
      figures.map { f =>
        if (f == figure) {
          // Move the figure to the start position if no other figure is there
          f.copy(index = 0)
        } else {
          f
        }
      }
    }
  }

  override def canMove(
      figure: IFigure,
      figures: List[IFigure],
      size: Int,
      rolled: Int
  ): Boolean = {
    // Check if the figure can move to the start position
    rolled == 6 && !figures.exists(f =>
      f.checkForPossibleCollision(
        figure,
        size,
        Position.Start(size, figure.owner.color)
      ) == Collision.OwnCollision
    )
  }
}
