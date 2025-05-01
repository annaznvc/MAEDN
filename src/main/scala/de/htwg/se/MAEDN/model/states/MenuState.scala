package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.{IState, Manager, Board, Player, Figure, State}
import de.htwg.se.MAEDN.util.{Color, Event}

case class MenuState(
    override val controller: Controller,
    override val moves: Int,
    override val board: Board,
    override val players: List[Player],
    override val rolled: Int = 0
) extends Manager {

  override val state: State = State.Menu

  override def startGame(): Manager = {
    // * MenuState -> ConfigState
    controller.eventQueue.enqueue(Event.ConfigEvent) // Send event to controller
    ConfigState(controller, moves, board, players)
  }

  override def quitGame(): Manager = {
    controller.eventQueue.enqueue(
      Event.QuitGameEvent
    )
    this
  }
}
