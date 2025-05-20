package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.IState
import de.htwg.se.MAEDN.model.states.MenuState
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.model.GameData

trait Manager extends IState {

  def data: GameData

  def getGameData: GameData
  def setGameData(data: GameData): Manager

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

  def getPlayerCount: Int = players.size
  def getFigureCount: Int = players.head.figures.size
  def getBoardSize: Int = board.size
  def getCurrentPlayer: Int = moves % players.size

  val moves: Int = data.moves
  def board: Board = data.board
  def players: List[Player] = data.players
  val rolled: Int = data.rolled
  val selectedFigure: Int = data.selectedFigure

}

object Manager {
  def apply(controller: Controller): Manager =
    MenuState(
      controller,
      GameData(
        moves = 0,
        board = Board(8),
        players = PlayerFactory.createPlayers(2, 4)
      )
    )
}
