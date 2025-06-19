package de.htwg.se.MAEDN.aview.gui

import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.model.IManager
import de.htwg.se.MAEDN.controller.command._
import de.htwg.se.MAEDN.module.Injectable
import javafx.event.ActionEvent
import javafx.scene.input.{KeyCode}
import javafx.scene.input.KeyEvent

import javafx.fxml.FXML

class ActionManager(controller: IController) extends Injectable {

  // Method to handle GUI actions (buttons, menu items, etc.)
  @FXML
  def onPlayNext(): Unit = {
    controller.executeCommand(inject[PlayNextCommand])
  }

  @FXML
  def onMoveUp(): Unit = {
    controller.executeCommand(inject[MoveUpCommand])
  }

  @FXML
  def onMoveDown(): Unit = {
    controller.executeCommand(inject[MoveDownCommand])
  }

  @FXML
  def onIncreaseFigures(): Unit = {
    controller.executeCommand(inject[IncreaseFiguresCommand])
  }

  @FXML
  def onDecreaseFigures(): Unit = {
    controller.executeCommand(inject[DecreaseFiguresCommand])
  }

  @FXML
  def onIncreaseBoardSize(): Unit = {
    controller.executeCommand(inject[IncreaseBoardSizeCommand])
  }

  @FXML
  def onDecreaseBoardSize(): Unit = {
    controller.executeCommand(inject[DecreaseBoardSizeCommand])
  }

  @FXML
  def onQuitGame(): Unit = {
    controller.executeCommand(inject[QuitGameCommand])
  }

  @FXML
  def onStartGame(): Unit = {
    controller.executeCommand(inject[StartGameCommand])
  }

  @FXML
  def onUndo(): Unit = {
    controller.executeCommand(inject[UndoCommand])
  }

  @FXML
  def onRedo(): Unit = {
    controller.executeCommand(inject[RedoCommand])
  }

  @FXML
  def onBackToMenu(): Unit = {
    // Use QuitGameCommand to go back to menu (based on ConfigState.quitGame implementation)
    controller.executeCommand(inject[QuitGameCommand])
  }

  @FXML
  def onOpenConfiguration(): Unit = {
    // Use StartGameCommand to go to config (based on MenuState.startGame implementation)
    controller.executeCommand(inject[StartGameCommand])
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
