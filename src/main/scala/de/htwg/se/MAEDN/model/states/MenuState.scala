package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.model.{IState, Manager, Board, Player, Figure, State}
import de.htwg.se.MAEDN.util.{Color, Event}
import de.htwg.se.MAEDN.model.IManager
import de.htwg.se.MAEDN.model.GameData

import scala.util.Try

case class MenuState(
    override val controller: IController,
    override val moves: Int,
    override val board: Board,
    override val players: List[Player],
    override val rolled: Int = 0
) extends IManager {

  override val state: State = State.Menu

  override def startGame(): Try[IManager] = Try {
    // * MenuState -> ConfigState
    controller.enqueueEvent(
      Event.ConfigEvent
    ) // Send event to controller
    ConfigState(controller, moves, board, players)
  }

  override def quitGame(): Try[IManager] = Try {
    controller.enqueueEvent(
      Event.QuitGameEvent
    )
    this
  }

  override def increaseBoardSize(): Try[IManager] = Try(this)
  override def decreaseBoardSize(): Try[IManager] = Try(this)
  override def increaseFigures(): Try[IManager] = Try(this)
  override def decreaseFigures(): Try[IManager] = Try(this)
  override def moveUp(): Try[IManager] = Try(this)
  override def moveDown(): Try[IManager] = Try(this)
  override def playDice(): Try[IManager] = Try(this)
  override def playNext(): Try[IManager] = Try(this)
  override def moveFigure(): Try[IManager] = Try(this)

  // Getter
  override def getPlayerCount: Int = players.size
  override def getFigureCount: Int =
    players.headOption.map(_.figures.size).getOrElse(0)
  override def getBoardSize: Int = board.size
  override def getCurrentPlayer: Int = 0
  override def getPlayers: List[Player] = players

  // Additional members
  override def createMemento: Option[GameData] = None
  override val selectedFigure: Int = 0
}
