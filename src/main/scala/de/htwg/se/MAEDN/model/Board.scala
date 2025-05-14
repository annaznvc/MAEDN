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
      val newFigureList =
        toBoardStrategy.moveFigure(
          figure,
          figures,
          size,
          rolled
        ) // Move to board
      return KickFigureStrategy.moveFigure(
        newFigureList.find(_.owner == figure.owner).get,
        newFigureList,
        size,
        rolled
      )
    } else {
      val newFigureList =
        moveStrategy.moveFigure(figure, figures, size, rolled) // Move on board
      return KickFigureStrategy.moveFigure(
        newFigureList.find(_.owner == figure.owner).get,
        newFigureList,
        size,
        rolled
      ) // Check for collision and handles kicking
    }
  }

  def checkIfMoveIsPossible(
      figures: List[Figure],
      rolled: Int,
      color: Color
  ): Boolean = {
    val playerFigures = figures.filter(_.owner.color == color)
    playerFigures.exists { figure =>
      if (!figure.isOnBoard) {
        toBoardStrategy.canMove(figure, figures, size, rolled)
      } else {
        moveStrategy.canMove(figure, figures, size, rolled)
      }
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
