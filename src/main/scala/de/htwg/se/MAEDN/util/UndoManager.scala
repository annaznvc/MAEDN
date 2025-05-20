package de.htwg.se.MAEDN.util

import de.htwg.se.MAEDN.controller.command.Command

class UndoManager {
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def doStep(command: Command): Unit = {
    command.doStep()
    undoStack = command :: undoStack
    redoStack = Nil
  }

  def undoStep(): Unit = undoStack match {
    case head :: tail =>
      head.undoStep()
      undoStack = tail
      redoStack = head :: redoStack
    case Nil => ()
  }

  def redoStep(): Unit = redoStack match {
    case head :: tail =>
      println(s"[DEBUG] Redoing command: $head")
      head.redoStep()
      redoStack = tail
      undoStack = head :: undoStack
    case Nil =>
      println("[DEBUG] Redo stack is empty.")
  }

}
