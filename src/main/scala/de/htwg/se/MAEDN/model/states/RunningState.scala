package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util.{Event, Dice}
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.GameData

case class RunningState(
    override val controller: Controller,
    override val data: GameData
) extends Manager {

  override def getGameData: GameData = data

  override val state: State = State.Running

  override def setGameData(newData: GameData): Manager =
    this.copy(data = newData)

  override def moveUp(): Manager = {
    val selected = (data.selectedFigure + 1) % data.players.head.figures.size
    controller.eventQueue.enqueue(Event.ChangeSelectedFigureEvent(selected))
    copy(data = data.copy(selectedFigure = selected))
  }

  override def moveDown(): Manager = {
    val selected =
      (data.selectedFigure - 1 + data.players.head.figures.size) % data.players.head.figures.size
    controller.eventQueue.enqueue(Event.ChangeSelectedFigureEvent(selected))
    copy(data = data.copy(selectedFigure = selected))
  }

  override def playDice(): Manager = {
    val newRoll = Dice.roll()
    controller.eventQueue.enqueue(Event.PlayDiceEvent(newRoll))
    copy(data = data.copy(rolled = newRoll))
  }

  override def playNext(): Manager = data.rolled match {
    case -1 =>
      controller.eventQueue.enqueue(
        Event.PlayNextEvent((getCurrentPlayer + 1) % data.players.size)
      )
      copy(data = data.copy(moves = data.moves + 1, rolled = 0))
    case 0 => playDice()
    case _ => moveFigure()
  }

  override def moveFigure(): Manager = {
    println(
      s"🧪 moveFigure() called — rolled = ${data.rolled}, selectedFigure = ${data.selectedFigure}, currentPlayer = ${getCurrentPlayer}"
    )

    val canMove = data.board.checkIfMoveIsPossible(
      data.players.flatMap(_.figures),
      data.rolled,
      data.players(getCurrentPlayer).color
    )
    println(s"🧪 checkIfMoveIsPossible = $canMove")

    if (!canMove) {
      println("🧪 No valid moves. Skipping turn.")
      controller.eventQueue.enqueue(
        Event.PlayNextEvent((getCurrentPlayer + 1) % data.players.size)
      )
      return copy(data = data.copy(rolled = 0, moves = data.moves + 1))
    }

    val figures = data.players.flatMap(_.figures)
    val figure = data.players(getCurrentPlayer).figures(data.selectedFigure)
    println(
      s"🧪 Trying to move figure ID=${figure.id}, current index=${figure.index}"
    )

    val newFigures = data.board.moveFigure(figure, figures, data.rolled)

    val updatedFigure = newFigures.find(_.id == figure.id)
    println(s"🧪 Updated figure position = ${updatedFigure.map(_.index)}")

    if (newFigures == figures) {
      println("🧪 No change in figures — move was invalid.")
      controller.eventQueue.enqueue(Event.InvalidMoveEvent)
      this
    } else {
      println("🧪 Move was successful.")
      controller.eventQueue.enqueue(Event.MoveFigureEvent(figure.id))
      val updatedPlayers = data.players.zipWithIndex.map { case (player, _) =>
        val playerFigures = newFigures.filter(_.owner.id == player.id)
        player.copy(figures = playerFigures)
      }

      copy(data =
        data.copy(
          players = updatedPlayers,
          rolled = if (data.rolled == 6) 0 else -1
        )
      )
    }
  }

  override def quitGame(): Manager = {
    controller.eventQueue.enqueue(Event.BackToMenuEvent)
    MenuState(controller, data)
  }
}
