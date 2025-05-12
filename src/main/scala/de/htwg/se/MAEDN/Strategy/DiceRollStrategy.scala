package de.htwg.se.MAEDN.model.strategy

import de.htwg.se.MAEDN.model.states.RunningState
import de.htwg.se.MAEDN.model.Manager

trait DiceRollStrategy {
  def handleRoll(rolled: Int, state: RunningState): Manager
}
