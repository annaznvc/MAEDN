package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.IState
import de.htwg.se.MAEDN.model.states.MenuState
import de.htwg.se.MAEDN.util.Color

import scala.util.Try

trait Manager extends IState with IOriginator {

  val moves: Int = 0
  val board: Board = Board(8)
  val players: List[Player] = PlayerFactory(2, 4)
  val selectedFigure: Int = 0

  def increaseBoardSize(): Try[Manager] = Try(this)
  def decreaseBoardSize(): Try[Manager] = Try(this)
  def increaseFigures(): Try[Manager] = Try(this)
  def decreaseFigures(): Try[Manager] = Try(this)
  def moveUp(): Try[Manager] = Try(this)
  def moveDown(): Try[Manager] = Try(this)
  def playDice(): Try[Manager] = Try(this)
  def playNext(): Try[Manager] = Try(this)
  def quitGame(): Try[Manager] = Try(this)
  def startGame(): Try[Manager] = Try(this)
  def moveFigure(): Try[Manager] = Try(this)

  def createMemento: Option[GameData] = None

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
      PlayerFactory(2, 4)
    )
}
