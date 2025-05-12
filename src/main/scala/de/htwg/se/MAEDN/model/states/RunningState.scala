package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.model.{IState, Manager, Board, Player, State}
import de.htwg.se.MAEDN.util.{Event, Dice}
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.strategy.DefaultDiceRollStrategy

case class RunningState(
    override val controller: Controller,
    override val moves: Int,
    override val board: Board,
    override val players: List[Player],
    override val rolled: Int = 0,
    val selectedFigure: Int = 0,
    val allowedRollDice: Boolean = true
) extends Manager {
  override val state: State = State.Running

  override def moveUp(): Manager = {
    val selected = (selectedFigure + 1) % players.head.figures.size
    controller.eventQueue.enqueue(Event.ChangeSelectedFigureEvent(selected))
    copy(
      selectedFigure = selected
    )
  }
  override def moveDown(): Manager = {
    val selected =
      (selectedFigure - 1 + players.head.figures.size) % players.head.figures.size
    controller.eventQueue.enqueue(Event.ChangeSelectedFigureEvent(selected))
    copy(
      selectedFigure = selected
    )
  }

  override def playDice(): Manager = {
    if (!allowedRollDice) return this

    val newRolled = Dice.roll()
    DefaultDiceRollStrategy.handleRoll(newRolled, this)
  }

  override def playNext(): Manager = {
    val nextPlayerIndex = (moves + 1) % players.size
    copy(moves = moves + 1)
  }

  override def moveFigure(): Manager = {
    if (allowedRollDice) return this

    controller.eventQueue.enqueue(Event.MoveFigureEvent(selectedFigure))

    val figure = players(getCurrentPlayer).figures(selectedFigure)
    val newBoard = board.moveFigure(figure, rolled)

    if (board == newBoard) {
      controller.eventQueue.enqueue(Event.InvalidMoveEvent)
      return this
    }

    if (rolled == 6) {
      controller.eventQueue.enqueue(Event.RollDiceEvent(rolled))
      copy(board = newBoard, allowedRollDice = true)
    } else {
      playNext().asInstanceOf[RunningState].copy(board = newBoard)
    }
  }

  override def quitGame(): Manager = {
    controller.eventQueue.enqueue(Event.BackToMenuEvent)
    MenuState(controller, moves, board, players)
  }
}
