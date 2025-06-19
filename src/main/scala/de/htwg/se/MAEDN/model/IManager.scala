package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.model.statesImp.{MenuState, RunningState}
import de.htwg.se.MAEDN.util.{Color, PlayerFactory}

import scala.util.Try

trait IManager extends IState with IOriginator {

  val moves: Int = 0
  val board: Board = Board(8)
  val players: List[Player] = PlayerFactory(2, 4)
  val selectedFigure: Int = 0

  def increaseBoardSize(): Try[IManager] = Try(this)
  def decreaseBoardSize(): Try[IManager] = Try(this)
  def increaseFigures(): Try[IManager] = Try(this)
  def decreaseFigures(): Try[IManager] = Try(this)
  def moveUp(): Try[IManager] = Try(this)
  def moveDown(): Try[IManager] = Try(this)
  def playDice(): Try[IManager] = Try(this)
  def playNext(): Try[IManager] = Try(this)
  def quitGame(): Try[IManager] = Try(this)
  def startGame(): Try[IManager] = Try(this)
  def moveFigure(): Try[IManager] = Try(this)

  def createMemento: Option[IMemento] = None

  def getPlayerCount: Int = players.size
  def getFigureCount: Int = players.head.figures.size
  def getBoardSize: Int = board.size
  def getCurrentPlayer: Int = moves % players.size
  def getPlayers: List[Player] = players
}

object IManager {
  def apply(controller: IController): IManager = {
    // Create an initial MenuState for the game
    MenuState(
      controller,
      0,
      Board(8),
      PlayerFactory(2, 4)
    )
  }

  def createRunningState(
      controller: IController,
      moves: Int,
      board: Board,
      players: List[Player],
      rolled: Int = 0,
      selectedFigure: Int = 0
  ): IManager = {
    // Create a running state for the game
    RunningState(controller, moves, board, players, rolled, selectedFigure)
  }
}
