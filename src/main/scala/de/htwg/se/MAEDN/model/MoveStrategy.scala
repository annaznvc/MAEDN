package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.model.Figure
import de.htwg.se.MAEDN.model.strategy.{
  KickFigureStrategy,
  NormalMoveStrategy,
  ToBoardStrategy
}
import de.htwg.se.MAEDN.module.DependencyInjector

trait MoveStrategy {
  def moveFigure(
      figure: Figure,
      figures: List[Figure],
      size: Int,
      rolled: Int
  ): List[Figure]

  def canMove(
      figure: Figure,
      figures: List[Figure],
      size: Int,
      rolled: Int
  ): Boolean

  def isOnGoal(figure: Figure, goalCount: Int, size: Int): Boolean =
    figure.index >= size * 4 - goalCount

  def collidingFigure(
      figure: Figure,
      figures: List[Figure],
      size: Int
  ): Option[Figure] = {
    val adjustedIndex = figure.adjustedIndex(size)
    figures.find(f => f.adjustedIndex(size) == adjustedIndex && f != figure)
  }

  def isOnLastPossibleField(
      figure: Figure,
      figures: List[Figure],
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

object MoveStrategy {
  def createKickFigureStrategy(): MoveStrategy = {
    DependencyInjector.getInstance[KickFigureStrategy]
  }
  def createToBoardStrategy(): MoveStrategy = {
    DependencyInjector.getInstance[ToBoardStrategy]
  }
  def createNormalMoveStrategy(): MoveStrategy = {
    DependencyInjector.getInstance[NormalMoveStrategy]
  }

  def apply(): MoveStrategy = {
    DependencyInjector.getInstance[NormalMoveStrategy]
  }
}
