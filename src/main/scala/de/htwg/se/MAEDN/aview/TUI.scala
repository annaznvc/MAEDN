package de.htwg.se.MAEDN.aview

import org.jline.terminal.{TerminalBuilder, Terminal}

import de.htwg.se.MAEDN.util.{Event, Observer}
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.State

class TUI(controller: Controller) extends Observer {

  controller.add(this)

  private def writeline(s: String): Unit = {
    terminal.writer().println(s)
    terminal.flush()
  }

  val terminal: Terminal = TerminalBuilder.builder().system(true).build()
  val inputManager = InputManager(terminal)

  def run(): Unit = {
    writeline(TextDisplay.clearTerminal())
    writeline(TextDisplay.printCover(controller.manager))
    update()
  }

  // * INPUT
  def update(): Unit = {

    inputManager.currentInput match {
      case Some(Command.Escape) => quit()
      case Some(Command.QuitGame)
          if controller.manager.state == State.Menu => // Quit game
      case Some(value) => {
        controller.processCommand(value)
        update()
      }
      case None => update()
    }
  }

  // * OUTPUT
  override def processEvent(event: Event): Unit = {
    event match {
      case Event.StartGameEvent => {
        writeline(TextDisplay.clearTerminal())
        writeline(TextDisplay.printCover(controller.manager))
        writeline(TextDisplay.printBoard(controller.manager.board))
      }
      case Event.ConfigEvent => {
        val manager = controller.manager
        writeline(TextDisplay.clearTerminal())
        writeline(TextDisplay.printCover(manager))
        writeline(
          TextDisplay.printConfig(
            manager.getPlayerCount,
            manager.getFigureCount,
            manager.getBoardSize
          )
        )
      }
      case Event.BackToMenuEvent => {
        writeline(TextDisplay.clearTerminal())
        writeline(TextDisplay.printCover(controller.manager))
      }
      case Event.InvalidMoveEvent => {
        writeline("Invalid move!")
        writeline(TextDisplay.printBoard(controller.manager.board))
      }
      case Event.QuitGameEvent => quit()
      case _                   => // Nothing to do for other events
    }
  }

  private def quit(): Unit = {
    writeline(TextDisplay.clearTerminal())
    writeline("Exiting...")
    terminal.close()
  }
}
