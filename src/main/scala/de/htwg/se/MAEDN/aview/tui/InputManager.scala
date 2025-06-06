package de.htwg.se.MAEDN.aview.tui

import org.jline.terminal.Terminal
import org.jline.keymap.{BindingReader, KeyMap}
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.controller.command._
import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.model.IManager

class InputManager(val controller: IController, val terminal: Terminal) {

  private val bindingReader = new BindingReader(terminal.reader())
  private val keyMap = new KeyMap[Command]()

  // Tasten direkt an Command-Objekte binden
  keyMap.bind(PlayNextCommand(controller), "x")
  keyMap.bind(MoveUpCommand(controller), "w")
  keyMap.bind(MoveDownCommand(controller), "s")
  keyMap.bind(IncreaseFiguresCommand(controller), "e")
  keyMap.bind(DecreaseFiguresCommand(controller), "d")
  keyMap.bind(IncreaseBoardSizeCommand(controller), "r")
  keyMap.bind(DecreaseBoardSizeCommand(controller), "f")
  keyMap.bind(QuitGameCommand(controller), "q")
  keyMap.bind(StartGameCommand(controller), "n")
  keyMap.bind(UndoCommand(controller), "u")
  keyMap.bind(RedoCommand(controller), "i")

  // ESC bleibt direkt verarbeitet – das ist kein Command
  private val ESC = "\u001b"

  def currentInput: Option[Command] = {
    val key = bindingReader.readBinding(keyMap)
    Option(key)
  }

  def isEscape: Boolean = {
    val peek = terminal.reader().peek(10)
    peek == 27 // ASCII ESC
  }
}
