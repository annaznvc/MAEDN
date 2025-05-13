package de.htwg.se.MAEDN.model.strategy

import de.htwg.se.MAEDN.model.{Board, Figure, IMoveStrategy}

/** The OnBoardStrategy is responsible for moving a figure from home to the
  * start position when a 6 is rolled. It is used when the figure is not on the
  * board.
  */
class ToBoardStrategy extends IMoveStrategy {
  override def moveFigure(
      figure: Figure,
      figures: List[Figure],
      size: Int,
      rolled: Int
  ): List[Figure] = {
    if (rolled != 6) {
      figures
    } else {
      figures.map { f =>
        if (f == figure) {
          // If the rolled number is 6, the figure can move to the start position
          f.copy(index = 0)
        } else {
          f
        }
      }
    }
  }
}
