package de.htwg.se.MAEDN.aview.tui

import org.jline.terminal.{TerminalBuilder, Terminal}
import scala.io.AnsiColor.{RED, RESET, GREEN, YELLOW}
import scala.util.{Success, Failure}

import de.htwg.se.MAEDN.util.{Event, Observer}
import de.htwg.se.MAEDN.model.State
import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.controller.command._
import de.htwg.se.MAEDN.module.Injectable

class TUI(controller: IController, terminal: Terminal)
    extends Observer
    with Injectable {
  var continue = true
  controller.add(this)

  // war mal protected def writeline(s: String): Unit = {
  protected def writeline(s: String): Unit = {
    terminal.writer().println(s)
    terminal.flush()
  }
  val inputManager = inject[InputManager]
  def run(): Unit = {
    writeline(TextDisplay.clearTerminal())
    writeline(TextDisplay.printCover(controller.manager, hasSaveFiles()))
    update()
  }

  // * INPUT
  /** Die Methode update() ist nicht testbar, weil sie auf reale
    * Tastatureingaben über inputManager.currentInput wartet und sich dabei
    * rekursiv selbst aufruft.
    */
  def update(): Unit = {
    while (continue) {
      inputManager.currentInput match {
        case Some(cmd) =>
          controller.executeCommand(cmd)
        case None => // No-op
      }
    }
  }

  // * OUTPUT
  override def processEvent(event: Event): Unit = {
    event match { // ----------------------------------------------------------------------
      case Event.StartGameEvent | Event.MoveFigureEvent(_) |
          Event.ChangeSelectedFigureEvent(_) =>
        writeline(TextDisplay.clearTerminal())
        printBoard(
          TextDisplay.printCover(controller.manager, hasSaveFiles()),
          ""
        )

      // ----------------------------------------------------------------------
      case Event.ConfigEvent =>
        val manager = controller.manager
        writeline(TextDisplay.clearTerminal())
        writeline(TextDisplay.printCover(manager, hasSaveFiles()))
        writeline(
          TextDisplay.printConfig(
            manager.getPlayerCount,
            manager.getFigureCount,
            manager.getBoardSize
          )
        )
      // ----------------------------------------------------------------------
      case Event.PlayDiceEvent(rolled) =>
        writeline(TextDisplay.clearTerminal())
        val afterMessage =
          if (rolled == 6)
            "You rolled a 6! Use 'w'/'s' to select a figure and press 'm' to move."
          else
            s"You rolled a $rolled!"
        printBoard(
          TextDisplay.printCover(controller.manager, hasSaveFiles()),
          afterMessage
        )
      // ----------------------------------------------------------------------
      case Event.PlayNextEvent(id) =>
        writeline(TextDisplay.clearTerminal())
        controller.manager.state match {
          case State.Running =>
            printBoard(
              TextDisplay.printCover(controller.manager, hasSaveFiles()),
              s"Player ${id + 1}'s turn!"
            )
          case _ =>
            printBoard(
              TextDisplay.printCover(controller.manager, hasSaveFiles()),
              s"Player ${id + 1}'s turn!"
            )
        }
      // ----------------------------------------------------------------------
      case Event.UndoEvent | Event.RedoEvent =>
        writeline(TextDisplay.clearTerminal())
        controller.manager.state match {
          case State.Running =>
            printBoard(
              TextDisplay.printCover(controller.manager, hasSaveFiles()),
              s"${event.toString} executed!"
            )
          case _ =>
            printBoard(
              TextDisplay.printCover(controller.manager, hasSaveFiles()),
              ""
            )
        } // ----------------------------------------------------------------------
      case Event.BackToMenuEvent =>
        writeline(TextDisplay.clearTerminal())
        writeline(TextDisplay.printCover(controller.manager, hasSaveFiles()))
      // ----------------------------------------------------------------------
      case Event.WinEvent(playerId) =>
        writeline(TextDisplay.clearTerminal())
        val playerName = s"Player ${playerId + 1}"
        val winMessage = s"🎉 ${GREEN}$playerName has won the game!${RESET} 🎉"
        printBoard(
          TextDisplay.printCover(controller.manager, hasSaveFiles()),
          winMessage
        )
        writeline("")
        writeline(
          s"${YELLOW}Congratulations! Press any key to return to menu...${RESET}"
        )
      // ----------------------------------------------------------------------
      case Event.ErrorEvent(message) =>
        writeline(TextDisplay.clearTerminal())
        printBoard(
          TextDisplay.printCover(controller.manager, hasSaveFiles()),
          s"Error: ${RED}$message${RESET}"
        )
      // ----------------------------------------------------------------------
      case Event.QuitGameEvent =>
        continue = false
        quit()
      // ----------------------------------------------------------------------
      case _ =>
        writeline("") // No-op fallback
    }
  }

  /** Checks if save files exist for continue functionality */
  def hasSaveFiles(): Boolean = {
    try {
      val fileIO = inject[de.htwg.se.MAEDN.util.FileIO]
      fileIO.listSaveFiles(Some(de.htwg.se.MAEDN.util.FileFormat.JSON)) match {
        case Success(files) => files.nonEmpty
        case Failure(_)     => false
      }
    } catch {
      case _: Exception => false
    }
  }

  protected def printBoard(
      beforeMessage: String,
      afterMessage: String
  ): String = {
    val message = beforeMessage + TextDisplay.printBoard(
      controller.manager.board,
      controller.manager.selectedFigure,
      controller.manager.getCurrentPlayer,
      controller.manager.players
    ) + afterMessage

    writeline(message)
    message
  }

  protected def quit(): Unit = {
    writeline(TextDisplay.clearTerminal())
    writeline("Exiting...")
    terminal.close()
  }
}
