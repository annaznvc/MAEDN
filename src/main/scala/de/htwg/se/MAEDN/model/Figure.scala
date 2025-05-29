package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Position

enum Collision:
  case NoCollision, OwnCollision, EnemyCollision

// A Figure belongs to a Player and moves across Fields
case class Figure(
    id: Int,
    owner: Player,
    index: Int,
    figureCount: Int
) {
  def adjustedIndex(size: Int): Position = {
    if (index == -1) Position.Home(id)
    else if (index < 4 * size)
      Position.Normal((index + owner.color.offset * size) % (size * 4))
    else if (index < (size * 4 + figureCount))
      Position.Goal(index - (size * 4))
    else
      Position.OffBoard(0)
  }
  def newAdjustedIndex(size: Int, rolled: Int): Position = {
    if (index == -1) Position.Home(id)
    else if (index + rolled < 4 * size)
      Position.Normal((index + rolled + owner.color.offset * size) % (size * 4))
    else if (index + rolled < (size * 4 + figureCount))
      Position.Goal((index + rolled) - (size * 4))
    else
      Position.OffBoard(0)
  }

  def isOnBoard: Boolean = index >= 0
  def isOnStart: Boolean = index == 0
  def isOnGoal(size: Int): Boolean =
    index >= (size * 4) && index < (size * 4 + figureCount)

  def checkForCollision(
      other: Figure,
      size: Int
  ): Collision = {
    println(
      s"DEBUG: Checking collision between Figure(id=${this.id}, owner=${this.owner.color}) and Figure(id=${other.id}, owner=${other.owner.color})"
    )
    println(s"DEBUG: This position: ${this
        .adjustedIndex(size)}, Other position: ${other.adjustedIndex(size)}")

    if (this == other) {
      println("DEBUG: Same figure, no collision")
      Collision.NoCollision
    } else if (this.adjustedIndex(size) == other.adjustedIndex(size)) {
      if (this.owner.color == other.owner.color) {
        println("DEBUG: Own collision detected")
        Collision.OwnCollision
      } else {
        println("DEBUG: Enemy collision detected")
        Collision.EnemyCollision
      }
    } else {
      println("DEBUG: No collision")
      Collision.NoCollision
    }
  }

  def checkForPossibleCollision(
      other: Figure,
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
