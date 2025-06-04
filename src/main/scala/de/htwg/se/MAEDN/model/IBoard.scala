package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.model.IFigure
import de.htwg.se.MAEDN.model.BoardImp.Board

/** Interface for IBoard entities in the MAEDN game. Defines the contract for
  * board objects that manage game logic and figure movement.
  */
trait IBoard {

  /** Size of the board (affects number of fields) */
  val size: Int

  /** Strategy for moving figures on the board */
  val moveStrategy: IMoveStrategy

  /** Strategy for moving figures from home to board */
  val toBoardStrategy: IMoveStrategy

  /** Strategy for handling figure collisions and kicking */
  val kickFigureStrategy: IMoveStrategy

  /** Moves a figure on the board according to the dice roll
    * @param figure
    *   the figure to move
    * @param figures
    *   all figures currently on the board
    * @param rolled
    *   the dice roll result
    * @return
    *   updated list of all figures after the move
    */
  def moveFigure(
      figure: IFigure,
      figures: List[IFigure],
      rolled: Int
  ): List[IFigure]

  /** Checks if any move is possible for a given player
    * @param figures
    *   all figures on the board
    * @param rolled
    *   the dice roll result
    * @param color
    *   the player's color
    * @return
    *   true if any move is possible
    */
  def checkIfMoveIsPossible(
      figures: List[IFigure],
      rolled: Int,
      color: Color
  ): Boolean

  /** Checks if a specific figure can move
    * @param figure
    *   the figure to check
    * @param figures
    *   all figures on the board
    * @param rolled
    *   the dice roll result
    * @return
    *   true if the figure can move
    */
  def canFigureMove(
      figure: IFigure,
      figures: List[IFigure],
      rolled: Int
  ): Boolean
}

object IBoard {
  def apply(
      size: Int,
      moveStrategy: IMoveStrategy,
      toBoardStrategy: IMoveStrategy,
      kickFigureStrategy: IMoveStrategy
  ): IBoard = {
    new Board(size, moveStrategy, toBoardStrategy, kickFigureStrategy)
  }

  def apply(size: Int): IBoard = {
    new Board(
      size,
      IMoveStrategy.createNormalMoveStrategy(),
      IMoveStrategy.createToBoardStrategy(),
      IMoveStrategy.createKickFigureStrategy()
    )
  }
}
