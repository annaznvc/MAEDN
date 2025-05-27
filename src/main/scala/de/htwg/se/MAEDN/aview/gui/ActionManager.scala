package de.htwg.se.MAEDN.aview.gui

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.controller.command._
import javafx.event.ActionEvent
import javafx.scene.input.{KeyCode, KeyEvent}

class ActionManager(controller: Controller) {

  // Method to handle GUI actions (buttons, menu items, etc.)
  def executePlayNext(): Unit = {
    controller.executeCommand(PlayNextCommand(controller))
  }

  def executeMoveUp(): Unit = {
    controller.executeCommand(MoveUpCommand(controller))
  }

  def executeMoveDown(): Unit = {
    controller.executeCommand(MoveDownCommand(controller))
  }

  def executeIncreaseFigures(): Unit = {
    controller.executeCommand(IncreaseFiguresCommand(controller))
  }

  def executeDecreaseFigures(): Unit = {
    controller.executeCommand(DecreaseFiguresCommand(controller))
  }

  def executeIncreaseBoardSize(): Unit = {
    controller.executeCommand(IncreaseBoardSizeCommand(controller))
  }

  def executeDecreaseBoardSize(): Unit = {
    controller.executeCommand(DecreaseBoardSizeCommand(controller))
  }

  def executeQuitGame(): Unit = {
    controller.executeCommand(QuitGameCommand(controller))
  }

  def executeStartGame(): Unit = {
    controller.executeCommand(StartGameCommand(controller))
  }

  def executeUndo(): Unit = {
    controller.executeCommand(UndoCommand(controller))
  }

  def executeRedo(): Unit = {
    controller.executeCommand(RedoCommand(controller))
  }
  def executeBackToMenu(): Unit = {
    // Use QuitGameCommand to go back to menu (based on ConfigState.quitGame implementation)
    controller.executeCommand(QuitGameCommand(controller))
  }

  def executeOpenConfiguration(): Unit = {
    // Use StartGameCommand to go to config (based on MenuState.startGame implementation)
    controller.executeCommand(StartGameCommand(controller))
  }

  // Handle keyboard shortcuts
  def handleKeyEvent(event: KeyEvent): Unit = {
    event.getCode match {
      case KeyCode.X => executePlayNext()
      case KeyCode.W => executeMoveUp()
      case KeyCode.S => executeMoveDown()
      case KeyCode.E => executeIncreaseFigures()
      case KeyCode.D => executeDecreaseFigures()
      case KeyCode.R => executeIncreaseBoardSize()
      case KeyCode.F => executeDecreaseBoardSize()
      case KeyCode.Q => executeQuitGame()
      case KeyCode.N => executeStartGame()
      case KeyCode.U => executeUndo()
      case KeyCode.I => executeRedo()
      case _         => // No action for other keys
    }
    event.consume()
  }

  // Handle action events from buttons
  def handleActionEvent(action: String): Unit = {
    action.toLowerCase match {
      case "playnext"          => executePlayNext()
      case "moveup"            => executeMoveUp()
      case "movedown"          => executeMoveDown()
      case "increasefigures"   => executeIncreaseFigures()
      case "decreasefigures"   => executeDecreaseFigures()
      case "increaseboardsize" => executeIncreaseBoardSize()
      case "decreaseboardsize" => executeDecreaseBoardSize()
      case "quitgame"          => executeQuitGame()
      case "startgame"         => executeStartGame()
      case "undo"              => executeUndo()
      case "redo"              => executeRedo()
      case "backtomenu"        => executeBackToMenu()
      case "openconfiguration" => executeOpenConfiguration()
      case _                   => // No action for unknown commands
    }
  }
}
