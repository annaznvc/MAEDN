package de.htwg.se.MAEDN.aview.gui

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.controller.command._
import javafx.event.ActionEvent
import javafx.scene.input.{KeyCode}
import javafx.scene.input.KeyEvent

import javafx.fxml.FXML

class ActionManager(controller: Controller) {

  // Method to handle GUI actions (buttons, menu items, etc.)
  @FXML
  def onPlayNext(): Unit = {
    controller.executeCommand(PlayNextCommand(controller))
  }

  @FXML
  def onMoveUp(): Unit = {
    controller.executeCommand(MoveUpCommand(controller))
  }

  @FXML
  def onMoveDown(): Unit = {
    controller.executeCommand(MoveDownCommand(controller))
  }

  @FXML
  def onIncreaseFigures(): Unit = {
    controller.executeCommand(IncreaseFiguresCommand(controller))
  }

  @FXML
  def onDecreaseFigures(): Unit = {
    controller.executeCommand(DecreaseFiguresCommand(controller))
  }

  @FXML
  def onIncreaseBoardSize(): Unit = {
    controller.executeCommand(IncreaseBoardSizeCommand(controller))
  }

  @FXML
  def onDecreaseBoardSize(): Unit = {
    controller.executeCommand(DecreaseBoardSizeCommand(controller))
  }

  @FXML
  def onQuitGame(): Unit = {
    controller.executeCommand(QuitGameCommand(controller))
  }

  @FXML
  def onStartGame(): Unit = {
    controller.executeCommand(StartGameCommand(controller))
  }

  @FXML
  def onUndo(): Unit = {
    controller.executeCommand(UndoCommand(controller))
  }

  @FXML
  def onRedo(): Unit = {
    controller.executeCommand(RedoCommand(controller))
  }

  @FXML
  def onBackToMenu(): Unit = {
    // Use QuitGameCommand to go back to menu (based on ConfigState.quitGame implementation)
    controller.executeCommand(QuitGameCommand(controller))
  }

  @FXML
  def onOpenConfiguration(): Unit = {
    // Use StartGameCommand to go to config (based on MenuState.startGame implementation)
    controller.executeCommand(StartGameCommand(controller))
  }

  // Handle keyboard shortcuts
  def handleKeyEvent(event: KeyEvent): Unit = {
    event.getCode match {
      case KeyCode.X => onPlayNext()
      case KeyCode.W => onMoveUp()
      case KeyCode.S => onMoveDown()
      case KeyCode.E => onIncreaseFigures()
      case KeyCode.D => onDecreaseFigures()
      case KeyCode.R => onIncreaseBoardSize()
      case KeyCode.F => onDecreaseBoardSize()
      case KeyCode.Q => onQuitGame()
      case KeyCode.N => onStartGame()
      case KeyCode.U => onUndo()
      case KeyCode.I => onRedo()
      case _         => // No action for other keys
    }
    event.consume()
  }
}
