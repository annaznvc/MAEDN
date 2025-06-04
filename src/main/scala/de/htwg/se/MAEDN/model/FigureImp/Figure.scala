package de.htwg.se.MAEDN.model.FigureImp

import de.htwg.se.MAEDN.util.Position
import de.htwg.se.MAEDN.model.{IFigure, IPlayer, Collision}

// A Figure belongs to a Player and moves across Fields
case class Figure(
    id: Int,
    owner: IPlayer,
    index: Int,
    figureCount: Int
) extends IFigure {
  override def adjustedIndex(size: Int): Position = {
    if (index == -1) Position.Home(id)
    else if (index < 4 * size)
      Position.Normal((index + owner.color.offset * size) % (size * 4))
    else if (index < (size * 4 + figureCount))
      Position.Goal(index - (size * 4))
    else
      Position.OffBoard(0)
  }
  override def newAdjustedIndex(size: Int, rolled: Int): Position = {
    if (index == -1) Position.Home(id)
    else if (index + rolled < 4 * size)
      Position.Normal((index + rolled + owner.color.offset * size) % (size * 4))
    else if (index + rolled < (size * 4 + figureCount))
      Position.Goal((index + rolled) - (size * 4))
    else
      Position.OffBoard(0)
  }

  override def isOnBoard: Boolean = index >= 0
  override def isOnStart: Boolean = index == 0
  override def isOnGoal(size: Int): Boolean =
    index >= (size * 4) && index < (size * 4 + figureCount)

  override def checkForCollision(
      other: IFigure,
      size: Int
  ): Collision = {
    if (this == other) {
      Collision.NoCollision
    } else if (this.adjustedIndex(size) == other.adjustedIndex(size)) {
      if (this.owner.color == other.owner.color) {
        Collision.OwnCollision
      } else {
        Collision.EnemyCollision
      }
    } else {
      Collision.NoCollision
    }
  }

  override def checkForPossibleCollision(
      other: IFigure,
      size: Int,
      newPosition: Position
  ): Collision = {
    if (this == other) Collision.NoCollision
    else if (newPosition == other.adjustedIndex(size)) {
      if (this.owner.color == other.owner.color) Collision.OwnCollision
      else Collision.EnemyCollision
    } else Collision.NoCollision
  }
}
