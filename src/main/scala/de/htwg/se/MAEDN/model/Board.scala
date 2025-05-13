package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.model.strategy.{
  KickFigureStrategy,
  NormalMoveStrategy,
  ToBoardStrategy
}

case class Board(
    size: Int = 8,
    moveStrategy: IMoveStrategy,
    toBoardStrategy: IMoveStrategy,
    KickFigureStrategy: IMoveStrategy
) {

  def moveFigure(
      figure: Figure,
      figures: List[Figure],
      rolled: Int
  ): List[Figure] = {
    if (!figure.isOnBoard) {
      toBoardStrategy.moveFigure(figure, figures, size, rolled) // Move to board
    } else {
      val newFigureList =
        moveStrategy.moveFigure(figure, figures, size, rolled) // Move on board
      KickFigureStrategy.moveFigure(
        figure,
        newFigureList,
        size,
        rolled
      ) // Check for collision and handles kicking
    }
  }
}

object Board {
  def apply(
      size: Int,
      moveStrategy: IMoveStrategy,
      toBoardStrategy: IMoveStrategy,
      kickFigureStrategy: IMoveStrategy
  ): Board = {
    new Board(size, moveStrategy, toBoardStrategy, kickFigureStrategy)
  }

  def apply(size: Int): Board = {
    new Board(
      size,
      new NormalMoveStrategy(),
      new ToBoardStrategy(),
      new KickFigureStrategy()
    )
  }
}
