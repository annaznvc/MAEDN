package de.htwg.se.MAEDN.controller

import scala.collection.mutable.Stack

import de.htwg.se.MAEDN.controller.command.{Command, UndoCommand, RedoCommand}
import de.htwg.se.MAEDN.model.{Manager, IMemento}
import de.htwg.se.MAEDN.util._

class Controller extends Observable {
  var manager: Manager = Manager(this)

  val undoStack = Stack[IMemento]()
  val redoStack = Stack[IMemento]()

  // Command execution
  def executeCommand(command: Command): Unit = {
    if !command.isInstanceOf[UndoCommand] && !command.isInstanceOf[RedoCommand]
    then undoStack.push(manager.getSnapshot)

    command.execute()
    notifyObservers()
  }
}