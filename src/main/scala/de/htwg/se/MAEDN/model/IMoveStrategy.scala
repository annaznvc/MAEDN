package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.model.IFigure
import de.htwg.se.MAEDN.model.StrategyImp.{
  KickFigureStrategy,
  NormalMoveStrategy,
  ToBoardStrategy
}

trait IMoveStrategy {
  def moveFigure(
      figure: IFigure,
      figures: List[IFigure],
      size: Int,
      rolled: Int
  ): List[IFigure]

  def canMove(
      figure: IFigure,
      figures: List[IFigure],
      size: Int,
      rolled: Int
  ): Boolean

  def isOnGoal(figure: IFigure, goalCount: Int, size: Int): Boolean =
    figure.index >= size * 4 - goalCount

  def collidingFigure(
      figure: IFigure,
      figures: List[IFigure],
      size: Int
  ): Option[IFigure] = {
    val adjustedIndex = figure.adjustedIndex(size)
    figures.find(f => f.adjustedIndex(size) == adjustedIndex && f != figure)
  }

  def isOnLastPossibleField(
      figure: IFigure,
      figures: List[IFigure],
      goalCount: Int,
      size: Int
  ): Boolean = {
    val lastFieldIndex = size * 4 - 1
    if (figure.index == lastFieldIndex) {
      true
    } else if (isOnGoal(figure, goalCount, size)) {
      val goalStartIndex = size * 4 - size
      val goalFigures = figures.filter(f =>
        isOnGoal(
          f,
          goalCount,
          size
        ) && f.owner.color == figure.owner.color && f != figure
      )
      val goalIndices = goalFigures.map(_.index).sorted
      goalIndices.zipWithIndex.forall { case (index, idx) =>
        index == goalStartIndex + idx
      }
    } else {
      false
    }
  }
}

object IMoveStrategy {
  def createKickFigureStrategy(): IMoveStrategy = {
    new KickFigureStrategy()
  }
  def createToBoardStrategy(): IMoveStrategy = {
    new ToBoardStrategy()
  }
  def createNormalMoveStrategy(): IMoveStrategy = {
    new NormalMoveStrategy()
  }

  def apply(): IMoveStrategy = {
    new NormalMoveStrategy()
  }
}
