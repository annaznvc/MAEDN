package de.htwg.se.MAEDN.model.statesImp

import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.util.{Color, Event}
import de.htwg.se.MAEDN.module.Injectable

import scala.util.Try

case class MenuState(
    override val controller: IController,
    override val moves: Int,
    override val board: Board,
    override val players: List[Player],
    override val rolled: Int = 0
) extends IManager
    with Injectable {

  override val state: State = State.Menu

  override def startGame(): Try[IManager] = Try {
    // * MenuState -> ConfigState
    controller.enqueueEvent(
      Event.ConfigEvent
    ) // Send event to controller
    ConfigState(controller, 0, board, players, 0)
  }

  override def quitGame(): Try[IManager] = Try {
    controller.enqueueEvent(
      Event.QuitGameEvent
    )
    this
  }

  // Getter
  override def getPlayerCount: Int = players.size
  override def getFigureCount: Int =
    players.headOption.map(_.figures.size).getOrElse(0)
  override def getBoardSize: Int = board.size
  override def getCurrentPlayer: Int = 0
  override def getPlayers: List[Player] = players

  // Additional members
  override def createMemento: Option[IMemento] = None
  override val selectedFigure: Int = 0
}
