package de.htwg.se.MAEDN.aview

import org.jline.terminal.{Terminal, TerminalBuilder}
import org.jline.keymap.{BindingReader, KeyMap}

enum Command {
  case PlayDice, MoveUp, MoveDown
}


class InputManager(val terminal: Terminal) {

  private val bindingReader = new BindingReader(terminal.reader())
  private val keyMap = new KeyMap[Command]

  // Mapping keys to commands
  keyMap.bind(Command.MoveUp, "w")
  keyMap.bind(Command.MoveDown, "s")
  keyMap.bind(Command.PlayDice, " ")


  def currentInput: Option[Command] = {
    val key = bindingReader.readBinding(keyMap)
    Option(key)
  }
}