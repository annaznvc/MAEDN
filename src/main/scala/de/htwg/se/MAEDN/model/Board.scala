package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Color

case class Board(
    size: Int = 8,
    moveStrategy: MoveStrategy,
    toBoardStrategy: MoveStrategy,
    kickFigureStrategy: MoveStrategy
) {

  def moveFigure(
      figure: Figure,
      figures: List[Figure],
      rolled: Int
  ): List[Figure] = {
    val newFigureList =
      if (!figure.isOnBoard)
        toBoardStrategy.moveFigure(figure, figures, size, rolled)
      else
        moveStrategy.moveFigure(figure, figures, size, rolled)
    kickFigureStrategy.moveFigure(
      newFigureList
        .find(f => f.id == figure.id && f.owner.id == figure.owner.id)
        .get,
      newFigureList,
      size,
      rolled
    )
  }

  def checkIfMoveIsPossible(
      figures: List[Figure],
      rolled: Int,
      color: Color
  ): Boolean = {
    val playerFigures = figures.filter(_.owner.color == color)
    playerFigures.exists { figure =>
      if (!figure.isOnBoard)
        toBoardStrategy.canMove(figure, figures, size, rolled)
      else
        moveStrategy.canMove(figure, figures, size, rolled)
    }
  }

  def canFigureMove(
      figure: Figure,
      figures: List[Figure],
      rolled: Int
  ): Boolean = {
    val size = this.size
    val figureCount = figure.figureCount
    val currentIndex = figure.index
    val newIndex = currentIndex + rolled
    if (currentIndex >= size * 4) {
      if (newIndex >= size * 4 && newIndex < size * 4 + figureCount) {
        val targetOccupied = figures.exists(f =>
          f.owner == figure.owner && f.index == newIndex && f != figure
        )
        !targetOccupied
      } else false
    } else if (!figure.isOnBoard) {
      toBoardStrategy.canMove(figure, figures, size, rolled)
    } else {
      moveStrategy.canMove(figure, figures, size, rolled)
    }
  }
}

object Board {
  def apply(
      size: Int,
      moveStrategy: MoveStrategy,
      toBoardStrategy: MoveStrategy,
      kickFigureStrategy: MoveStrategy
  ): Board = {
    new Board(size, moveStrategy, toBoardStrategy, kickFigureStrategy)
  }

  def apply(size: Int): Board = {
    new Board(
      size,
      MoveStrategy.createNormalMoveStrategy(),
      MoveStrategy.createToBoardStrategy(),
      MoveStrategy.createKickFigureStrategy()
    )
  }
}
