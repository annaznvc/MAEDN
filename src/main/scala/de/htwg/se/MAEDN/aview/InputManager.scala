package de.htwg.se.MAEDN.aview

import org.jline.terminal.Terminal
import org.jline.keymap.{BindingReader, KeyMap}
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.controller.command._

class InputManager(val controller: Controller, val terminal: Terminal) {

  private val bindingReader = new BindingReader(terminal.reader())
  private val keyMap = new KeyMap[Command]()

  // Tasten direkt an Command-Objekte binden
  keyMap.bind(PlayDiceCommand(controller), "z")
  keyMap.bind(PlayNextCommand(controller), "x")
  keyMap.bind(MoveUpCommand(controller), "w")
  keyMap.bind(MoveDownCommand(controller), "s")
  keyMap.bind(IncreaseFiguresCommand(controller), "e")
  keyMap.bind(DecreaseFiguresCommand(controller), "d")
  keyMap.bind(IncreaseBoardSizeCommand(controller), "r")
  keyMap.bind(DecreaseBoardSizeCommand(controller), "f")
  keyMap.bind(QuitGameCommand(controller), "q")
  keyMap.bind(StartGameCommand(controller), "n")
  keyMap.bind(MoveFigureCommand(controller), "m")

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
