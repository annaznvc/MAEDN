package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.controller.Controller

import scala.util.Try

enum State {
  case Menu, Config, Running
}

trait IState {
  val moves: Int
  val state: State
  val controller: Controller
  val rolled: Int
  val selectedFigure: Int

  def startGame(): Try[Manager]
  def quitGame(): Try[Manager]
  def moveUp(): Try[Manager]
  def moveDown(): Try[Manager]
  def increaseFigures(): Try[Manager]
  def decreaseFigures(): Try[Manager]
  def increaseBoardSize(): Try[Manager]
  def decreaseBoardSize(): Try[Manager]
  def playDice(): Try[Manager]
  def playNext(): Try[Manager]
  def moveFigure(): Try[Manager]

  def getPlayers: List[Player]
  def getPlayerCount: Int
  def getFigureCount: Int
  def getBoardSize: Int
  def getCurrentPlayer: Int
}
