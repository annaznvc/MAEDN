package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.model.{IState, Manager, Board, Player, State}
import de.htwg.se.MAEDN.util.{Event, Dice}
import de.htwg.se.MAEDN.controller.Controller

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
    println(s"[DEBUG] moveUp called — current selected: $selectedFigure")
    val selected = (selectedFigure + 1) % players.head.figures.size
    controller.eventQueue.enqueue(Event.ChangeSelectedFigureEvent(selected))
    copy(selectedFigure = selected)
  }

  override def moveDown(): Manager = {
    println(s"[DEBUG] moveDown called — current selected: $selectedFigure")
    val selected =
      (selectedFigure - 1 + players.head.figures.size) % players.head.figures.size
    controller.eventQueue.enqueue(Event.ChangeSelectedFigureEvent(selected))
    copy(selectedFigure = selected)
  }

  override def playDice(): Manager = {
    if (!allowedRollDice) return this

    val newRolled = Dice.roll()
    controller.eventQueue.enqueue(Event.RollDiceEvent(newRolled))

    if (newRolled == 6) {
      // Spieler bleibt dran
      copy(rolled = newRolled, allowedRollDice = false)
    } else {
      // Nächster Spieler, Auswahl zurücksetzen
      controller.eventQueue.enqueue(Event.ChangeSelectedFigureEvent(0))
      copy(rolled = newRolled, allowedRollDice = true, moves = moves + 1)
    }
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
      this
    } else if (rolled == 6) {
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
