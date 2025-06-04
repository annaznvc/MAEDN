package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Position

enum Collision:
  case NoCollision, OwnCollision, EnemyCollision

/** Interface for Figure entities in the MAEDN game. Defines the contract for
  * figure objects that represent game pieces belonging to players.
  */
trait IFigure {

  /** Unique identifier of the figure */
  val id: Int

  /** The player who owns this figure */
  val owner: IPlayer

  /** Current position index of the figure on the board (-1 for home) */
  val index: Int

  /** Total number of figures the owner has */
  val figureCount: Int

  /** Calculates the adjusted position of the figure on the board
    * @param size
    *   the size of the board
    * @return
    *   the position of the figure (Home, Normal, Goal, or OffBoard)
    */
  def adjustedIndex(size: Int): Position

  /** Calculates the adjusted position of the figure after a potential move
    * @param size
    *   the size of the board
    * @param rolled
    *   the number of steps to move
    * @return
    *   the new position after the move
    */
  def newAdjustedIndex(size: Int, rolled: Int): Position

  /** Checks if the figure is currently on the board (not at home)
    * @return
    *   true if the figure is on the board, false if at home
    */
  def isOnBoard: Boolean

  /** Checks if the figure is at the start position
    * @return
    *   true if the figure is at the start position
    */
  def isOnStart: Boolean

  /** Checks if the figure is in the goal area
    * @param size
    *   the size of the board
    * @return
    *   true if the figure is in the goal area
    */
  def isOnGoal(size: Int): Boolean

  /** Checks for collision with another figure at current positions
    * @param other
    *   the other figure to check collision with
    * @param size
    *   the size of the board
    * @return
    *   the type of collision (NoCollision, OwnCollision, EnemyCollision)
    */
  def checkForCollision(other: IFigure, size: Int): Collision

  /** Checks for potential collision with another figure at a new position
    * @param other
    *   the other figure to check collision with
    * @param size
    *   the size of the board
    * @param newPosition
    *   the new position to check
    * @return
    *   the type of collision at the new position
    */
  def checkForPossibleCollision(
      other: IFigure,
      size: Int,
      newPosition: Position
  ): Collision

  /** Creates a copy of this figure with the same properties
    * @return
    *   a new instance of IFigure with the same id, owner, index, and
    *   figureCount
    */
  def copy(
      id: Int = this.id,
      owner: IPlayer = this.owner,
      index: Int = this.index,
      figureCount: Int = this.figureCount
  ): IFigure = {
    FigureImp.Figure(id, owner, index, figureCount)
  }
}
object IFigure {
  def apply(
      id: Int,
      owner: IPlayer,
      index: Int,
      figureCount: Int
  ): IFigure = new FigureImp.Figure(id, owner, index, figureCount)
}
