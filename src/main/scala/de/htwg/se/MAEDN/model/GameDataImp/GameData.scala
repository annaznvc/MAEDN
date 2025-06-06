package de.htwg.se.MAEDN.model.GameDataImp

import de.htwg.se.MAEDN.model.{IMemento, IManager, IPlayer, IBoard}
import de.htwg.se.MAEDN.controller.IController

import scala.util.{Try, Success, Failure}

case class GameData(
    moves: Int,
    board: IBoard,
    players: List[IPlayer],
    selectedFigure: Int,
    rolled: Int
) extends IMemento {

  def restoreManager(controller: IController): Try[IManager] = {
    (players.size, selectedFigure, rolled, players.isEmpty) match {
      case (size, _, _, _) if size < 2 || size > 4 =>
        Failure(
          new IllegalArgumentException("Players size must be between 2 and 4")
        )
      case (_, idx, _, _) if idx < 0 || idx > players.size =>
        Failure(
          new IllegalArgumentException("Selected figure index out of bounds")
        )
      case (_, _, r, _) if r < -1 || r > 6 =>
        Failure(
          new IllegalArgumentException("Rolled value must be between -1 and 6")
        )
      case (_, _, _, true) =>
        Failure(new IllegalArgumentException("Players list must not be empty"))
      case _ =>
        Success(
          IManager.createRunningState(
            controller,
            moves,
            board,
            players,
            selectedFigure,
            rolled
          )
        )
    }
  }

  // Implementierung für IMemento
  override def restoreIManager(controller: IController): Try[IManager] =
    restoreManager(controller)
}
