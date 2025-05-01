package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.controller.Controller

enum State {
  case Menu, Config, Running
}

trait IState {
  val moves: Int
  val state: State
  val controller: Controller
  val rolled: Int

  def startGame(): Manager
  def quitGame(): Manager
  def moveUp(): Manager
  def moveDown(): Manager
  def increaseFigures(): Manager
  def decreaseFigures(): Manager
  def increaseBoardSize(): Manager
  def decreaseBoardSize(): Manager
  def playDice(): Manager
  def playNext(): Manager
  def moveFigure(): Manager

  def getPlayerCount: Int
  def getFigureCount: Int
  def getBoardSize: Int
  def getCurrentPlayer: Int
}
