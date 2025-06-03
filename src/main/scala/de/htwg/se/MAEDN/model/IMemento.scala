package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.model.IManager

import scala.util.Try

/** Trait representing a Memento in the Memento design pattern. Provides a
  * method to restore the state of a Manager from a saved memento.
  */
trait IMemento {

  /** Restores and returns RunningManager instance from the memento.
    * @return
    *   the restored Manager
    */
  def restoreManager(controller: Controller): Try[Manager]

  /** Restores and returns RunningManager instance from the memento.
    * @return
    *   the restored Manager
    */
  def restoreIManager(controller: IController): Try[IManager]
}

/** Trait representing an Originator in the Memento design pattern. Provides a
  * method to create a memento (GameData) encapsulating the current state.
  */
trait IOriginator {

  /** Creates a memento containing the current state of the Originator.
    * @return
    *   a Try containing the GameData memento or a failure if creation fails
    */
  def createMemento: Option[GameData]
}
