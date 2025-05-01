package de.htwg.se.MAEDN.aview

import org.jline.terminal.{Terminal, TerminalBuilder}
import org.jline.keymap.{BindingReader, KeyMap}

enum Command {
  case PlayDice, PlayNext, MoveUp, MoveDown, IncreaseFigures, DecreaseFigures,
    IncreaseBoardSize, DecreaseBoardSize, QuitGame, StartGame, Escape
}

class InputManager(val terminal: Terminal) {

  private val bindingReader = new BindingReader(terminal.reader())
  private val keyMap = new KeyMap[Command]

  // Mapping keys to commands
  keyMap.bind(Command.IncreaseFigures, "e")
  keyMap.bind(Command.DecreaseFigures, "d")
  keyMap.bind(Command.IncreaseBoardSize, "r")
  keyMap.bind(Command.DecreaseBoardSize, "f")
  keyMap.bind(Command.MoveUp, "w")
  keyMap.bind(Command.MoveDown, "s")
  keyMap.bind(Command.QuitGame, "q")
  keyMap.bind(Command.StartGame, "n")
  keyMap.bind(Command.PlayNext, " ")
  keyMap.bind(Command.PlayDice, "z")
  keyMap.bind(Command.Escape, "\u001b")

  def currentInput: Option[Command] = {
    val key = bindingReader.readBinding(keyMap)
    Option(key)
  }
}
