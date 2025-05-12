package de.htwg.se.MAEDN.model.strategy

import de.htwg.se.MAEDN.model.states.RunningState
import de.htwg.se.MAEDN.model.Manager
import de.htwg.se.MAEDN.util.Event

object DefaultDiceRollStrategy extends DiceRollStrategy {
  override def handleRoll(rolled: Int, state: RunningState): Manager = {
    val controller = state.controller
    controller.eventQueue.enqueue(Event.RollDiceEvent(rolled))

    if (rolled == 6) {
      // Stay on the same player, allow move
      state.copy(rolled = rolled, allowedRollDice = false)
    } else {
      // Move to next player
      val next = state.copy(
        rolled = rolled,
        moves = state.moves + 1,
        allowedRollDice = true
      )
      controller.eventQueue.enqueue(
        Event.ChangeSelectedFigureEvent(0)
      ) // reset selection
      next
    }
  }
}
