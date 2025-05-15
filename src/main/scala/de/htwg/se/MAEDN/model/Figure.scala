package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Position

enum Collision:
  case NoCollision, OwnCollision, EnemyCollision

// A Figure belongs to a Player and moves across Fields
case class Figure(
    id: Int,
    owner: Player,
    index: Int
) {
  def adjustedIndex(size: Int): Position = {
    if (index == -1) Position.Home(id)
    else if (index >= (size * 4 + owner.figures.size)) Position.OffBoard(0)
    else if (index >= (size * 4)) Position.Goal(index % (size * 4))
    else Position.Normal((index + owner.color.offset * size) % (size * 4))
  }

  def newAdjustedIndex(size: Int, rolled: Int): Position = {
    if (index == -1) Position.Home(id)
    else if (index + rolled >= (size * 4 + owner.figures.size))
      Position.OffBoard(0)
    else if (index + rolled >= (size * 4))
      Position.Goal((index + rolled) % (size * 4))
    else
      Position.Normal(
        ((index + rolled) + owner.color.offset * size) % (size * 4)
      )
  }

  def isOnBoard: Boolean = index >= 0
  def isOnStart: Boolean = index == 0
  def isOnGoal(size: Int): Boolean = index >= (size * 4)

  def checkForCollision(
      other: Figure,
      size: Int
  ): Collision = {
    if (this == other || this.adjustedIndex(size) != other.adjustedIndex(size))
      Collision.NoCollision
    else if (this.owner.color == other.owner.color) Collision.OwnCollision
    else Collision.EnemyCollision
  }

  def checkForPossibleCollision(
      other: Figure,
      size: Int,
      newPosition: Position
  ): Collision = {
    if (this == other || this.adjustedIndex(size) != newPosition)
      Collision.NoCollision
    else if (this.owner.color == other.owner.color) Collision.OwnCollision
    else Collision.EnemyCollision
  }

  override def toString: String = s"Figure($id, index=$index)"
}
