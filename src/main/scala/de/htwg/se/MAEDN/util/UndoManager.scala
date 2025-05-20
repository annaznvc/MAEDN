package de.htwg.se.MAEDN.util

import de.htwg.se.MAEDN.controller.command.Command

class UndoManager {
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil
  private var _wasLastOperationUndo: Boolean = false

  def wasLastOperationUndo: Boolean = _wasLastOperationUndo

  def doStep(command: Command): Unit = {
    command.doStep()
    undoStack = command :: undoStack
    redoStack = Nil // This line clears the redo stack
    _wasLastOperationUndo = false // Reset the flag on new commands
  }

  def undoStep(): Unit = undoStack match {
    case head :: tail =>
      head.undoStep()
      undoStack = tail
      redoStack = head :: redoStack
      _wasLastOperationUndo = true // Set the flag when undoing
    case Nil => ()
  }

  def redoStep(): Unit = redoStack match {
    case head :: tail =>
      head.redoStep()
      redoStack = tail
      undoStack = head :: undoStack
      _wasLastOperationUndo = false // Reset the flag when redoing
    case Nil =>
      println("[DEBUG] Redo stack is empty.")
  }
}
