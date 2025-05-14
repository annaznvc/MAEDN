package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.IState
import de.htwg.se.MAEDN.model.states.MenuState
import de.htwg.se.MAEDN.util.Color

trait Manager extends IState with IMemento {

  val moves: Int = 0
  val board: Board = Board(8)
  val players: List[Player] = PlayerFactory.createPlayers(2, 4)
  val selectedFigure: Int = 0

  def increaseBoardSize(): Manager = this
  def decreaseBoardSize(): Manager = this
  def increaseFigures(): Manager = this
  def decreaseFigures(): Manager = this
  def moveUp(): Manager = this
  def moveDown(): Manager = this
  def playDice(): Manager = this
  def playNext(): Manager = this
  def quitGame(): Manager = this
  def startGame(): Manager = this
  def moveFigure(): Manager = this

  def getSnapshot: Manager = this

  def getPlayerCount: Int = players.size
  def getFigureCount: Int = players.head.figures.size
  def getBoardSize: Int = board.size
  def getCurrentPlayer: Int = moves % players.size
}

object Manager {
  def apply(controller: Controller): Manager =
    MenuState(
      controller,
      0,
      Board(8),
      PlayerFactory.createPlayers(2, 4)
    )
}
