package de.htwg.se.MAEDN.model.BoardImp

import de.htwg.se.MAEDN.util.Color

import de.htwg.se.MAEDN.model.{IBoard, IFigure, IMoveStrategy}

case class Board(
    size: Int = 8,
    moveStrategy: IMoveStrategy,
    toBoardStrategy: IMoveStrategy,
    kickFigureStrategy: IMoveStrategy
) extends IBoard {

  def moveFigure(
      figure: IFigure,
      figures: List[IFigure],
      rolled: Int
  ): List[IFigure] = {
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
      figures: List[IFigure],
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
      figure: IFigure,
      figures: List[IFigure],
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
