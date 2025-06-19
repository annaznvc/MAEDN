package de.htwg.se.MAEDN.aview.tui

import org.jline.terminal.Terminal
import org.jline.keymap.{BindingReader, KeyMap}
import de.htwg.se.MAEDN.controller.command._
import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.model.IManager
import de.htwg.se.MAEDN.module.Injectable

class InputManager(val controller: IController, val terminal: Terminal)
    extends Injectable {

  private val bindingReader = new BindingReader(terminal.reader())
  private val keyMap = new KeyMap[Command]()

  keyMap.bind(inject[PlayNextCommand], "x")
  keyMap.bind(inject[MoveUpCommand], "w")
  keyMap.bind(inject[MoveDownCommand], "s")
  keyMap.bind(inject[IncreaseFiguresCommand], "e")
  keyMap.bind(inject[DecreaseFiguresCommand], "d")
  keyMap.bind(inject[IncreaseBoardSizeCommand], "r")
  keyMap.bind(inject[DecreaseBoardSizeCommand], "f")
  keyMap.bind(inject[QuitGameCommand], "q")
  keyMap.bind(inject[StartGameCommand], "n")
  keyMap.bind(inject[UndoCommand], "u")
  keyMap.bind(inject[RedoCommand], "i")
  keyMap.bind(inject[ContinueGameCommand], "c")

  def currentInput: Option[Command] = {
    val key = bindingReader.readBinding(keyMap)
    Option(key)
  }
}
