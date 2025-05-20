package de.htwg.se.MAEDN.util

import de.htwg.se.MAEDN.controller.command.Command

class UndoManager {
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def doStep(command: Command): Unit = {
    println(s"[DEBUG] Executing command: $command")
    command.doStep()
    undoStack = command :: undoStack
    println(s"[DEBUG] Undo stack size: ${undoStack.size}")
    redoStack = Nil // This line clears the redo stack!
    println("[DEBUG] Redo stack cleared")
  }

  def undoStep(): Unit = undoStack match {
    case head :: tail =>
      println(s"[DEBUG] Undoing command: $head")
      head.undoStep()
      undoStack = tail
      redoStack = head :: redoStack
      println(s"[DEBUG] Redo stack size after undo: ${redoStack.size}")
    case Nil =>
      println("[DEBUG] Undo stack is empty.")
  }

  def redoStep(): Unit = redoStack match {
    case head :: tail =>
      println(s"[DEBUG] Redoing command: $head")
      head.redoStep()
      redoStack = tail
      undoStack = head :: undoStack
      println(s"[DEBUG] Undo stack size after redo: ${undoStack.size}")
    case Nil =>
      println("[DEBUG] Redo stack is empty.")
  }

  def printStackSizes(): Unit = {
    println(s"[DEBUG] Current undo stack size: ${undoStack.size}")
    println(s"[DEBUG] Current redo stack size: ${redoStack.size}")
  }
}
