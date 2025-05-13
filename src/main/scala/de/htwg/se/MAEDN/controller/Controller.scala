package de.htwg.se.MAEDN.controller

import de.htwg.se.MAEDN.util._
import de.htwg.se.MAEDN.model.Manager
import de.htwg.se.MAEDN.controller.command.Command
import scala.collection.mutable.Stack
import de.htwg.se.MAEDN.controller.command.UndoCommand

class Controller extends Observable with Originator {
  var manager: Manager = Manager(this)

  private val undoStack = Stack[Memento]()

  // Originator methods
  override def createMemento(): Memento = ManagerMemento(manager)
  override def restoreMemento(m: Memento): Unit = m match {
    case ManagerMemento(oldState) => manager = oldState
  }

  // Command execution
  def executeCommand(command: Command): Unit = {
    if !command.isInstanceOf[UndoCommand] then undoStack.push(createMemento())
    command.execute()
    notifyObservers()
  }

  def undo(): Unit = {
    if undoStack.nonEmpty then {
      val memento = undoStack.pop()
      restoreMemento(memento)
      notifyObservers()
    }
  }
}
