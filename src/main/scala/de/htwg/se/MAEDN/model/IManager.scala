package de.htwg.se.MAEDN.model

import scala.util.Try
import de.htwg.se.MAEDN.controller.IController

trait IManager extends IState {
  override val controller: IController
  override val moves: Int
  val board: Board
  val players: List[Player]
  override val rolled: Int
  override val selectedFigure: Int
  override val state: State

  def increaseBoardSize(): Try[IManager]
  def decreaseBoardSize(): Try[IManager]
  def increaseFigures(): Try[IManager]
  def decreaseFigures(): Try[IManager]
  def moveUp(): Try[IManager]
  def moveDown(): Try[IManager]
  def playDice(): Try[IManager]
  def playNext(): Try[IManager]
  def quitGame(): Try[IManager]
  def startGame(): Try[IManager]
  def moveFigure(): Try[IManager]
  def createMemento: Option[GameData]

  def getPlayerCount: Int
  def getFigureCount: Int
  def getBoardSize: Int
  def getCurrentPlayer: Int
}
