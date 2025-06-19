package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.model.IManager

import scala.util.Try

enum State {
  case Menu, Config, Running
}

trait IState {
  val moves: Int
  val state: State
  val controller: IController
  val rolled: Int
  val selectedFigure: Int

  def startGame(): Try[IManager]
  def quitGame(): Try[IManager]
  def moveUp(): Try[IManager]
  def moveDown(): Try[IManager]
  def increaseFigures(): Try[IManager]
  def decreaseFigures(): Try[IManager]
  def increaseBoardSize(): Try[IManager]
  def decreaseBoardSize(): Try[IManager]
  def playDice(): Try[IManager]
  def playNext(): Try[IManager]
  def moveFigure(): Try[IManager]

  def getPlayers: List[Player]
  def getPlayerCount: Int
  def getFigureCount: Int
  def getBoardSize: Int
  def getCurrentPlayer: Int
}
