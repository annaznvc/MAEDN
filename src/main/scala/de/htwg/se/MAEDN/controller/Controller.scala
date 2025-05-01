package de.htwg.se.MAEDN.controller

import de.htwg.se.MAEDN.util.Observable
import de.htwg.se.MAEDN.model.Manager
import de.htwg.se.MAEDN.aview.Command

class Controller extends Observable {
  var manager: Manager = Manager(this)

  def processCommand(command: Command): Unit = {
    command match {
      case Command.PlayDice          => manager = manager.playDice()
      case Command.MoveUp            => manager = manager.moveUp()
      case Command.MoveDown          => manager = manager.moveDown()
      case Command.IncreaseFigures   => manager = manager.increaseFigures()
      case Command.DecreaseFigures   => manager = manager.decreaseFigures()
      case Command.IncreaseBoardSize => manager = manager.increaseBoardSize()
      case Command.DecreaseBoardSize => manager = manager.decreaseBoardSize()
      case Command.QuitGame          => manager = manager.quitGame()
      case Command.StartGame         => manager = manager.startGame()
      case Command.PlayNext          => manager = manager.playNext()
      case _                         => println("Unknown command")
    }
    notifyObservers()
  }

}
