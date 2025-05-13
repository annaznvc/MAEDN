package de.htwg.se.MAEDN.aview

import org.jline.terminal.{TerminalBuilder, Terminal}

import de.htwg.se.MAEDN.util.{Event, Observer}
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.State
import de.htwg.se.MAEDN.controller.command._
import de.htwg.se.MAEDN.model.states.RunningState

class TUI(controller: Controller) extends Observer {

  controller.add(this)

  // war mal private def writeline(s: String): Unit = {
  protected def writeline(s: String): Unit = {
    terminal.writer().println(s)
    terminal.flush()
  }

  val terminal: Terminal = TerminalBuilder.builder().system(true).build()
  val inputManager = InputManager(controller, terminal)

  def run(): Unit = {
    writeline(TextDisplay.clearTerminal())
    writeline(TextDisplay.printCover(controller.manager))
    update()
  }

  // * INPUT
  /** Die Methode update() ist nicht testbar, weil sie auf reale
    * Tastatureingaben über inputManager.currentInput wartet und sich dabei
    * rekursiv selbst aufruft.
    */
  def update(): Unit = {
    if (inputManager.isEscape) {
      quit()
    } else {
      inputManager.currentInput match {
        case Some(cmd) =>
          cmd.execute()
          update()
        case None =>
          update()
      }
    }
  }

  // * OUTPUT
  override def processEvent(event: Event): Unit = {
    event match {

      case Event.StartGameEvent =>
        writeline(TextDisplay.clearTerminal())
        controller.manager match {
          case rs: RunningState =>
            writeline(TextDisplay.printCover(rs))
            writeline(
              TextDisplay.printBoard(
                rs.board,
                rs.selectedFigure,
                rs.getCurrentPlayer,
                rs.players
              )
            )
          case _ =>
            writeline(TextDisplay.printCover(controller.manager))
            writeline(TextDisplay.printBoard(controller.manager.board))
        }

      case Event.ConfigEvent =>
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

      case Event.PlayDiceEvent(rolled) =>
        writeline(TextDisplay.clearTerminal())
        controller.manager match {
          case rs: RunningState =>
            writeline(TextDisplay.printCover(rs))
            writeline(
              TextDisplay.printBoard(
                rs.board,
                rs.selectedFigure,
                rs.getCurrentPlayer,
                rs.players
              )
            )
          case _ =>
            writeline(TextDisplay.printCover(controller.manager))
            writeline(TextDisplay.printBoard(controller.manager.board))
        }
        writeline(s"You rolled a $rolled!")
        if (rolled == 6)
          writeline(
            "You rolled a 6! Use 'w'/'s' to select a figure and press 'm' to move."
          )

      case Event.ChangeSelectedFigureEvent(_) =>
        writeline(TextDisplay.clearTerminal())
        controller.manager match {
          case rs: RunningState =>
            writeline(TextDisplay.printCover(rs))
            writeline(
              TextDisplay.printBoard(
                rs.board,
                rs.selectedFigure,
                rs.getCurrentPlayer,
                rs.players
              )
            )
          case _ =>
            writeline(TextDisplay.printCover(controller.manager))
            writeline(TextDisplay.printBoard(controller.manager.board))
        }

      case Event.InvalidMoveEvent =>
        writeline("Invalid move!")
        controller.manager match {
          case rs: RunningState =>
            writeline(
              TextDisplay.printBoard(
                rs.board,
                rs.selectedFigure,
                rs.getCurrentPlayer,
                rs.players
              )
            )
          case _ =>
            writeline(TextDisplay.printBoard(controller.manager.board))
        }

      case Event.BackToMenuEvent =>
        writeline(TextDisplay.clearTerminal())
        writeline(TextDisplay.printCover(controller.manager))

      case Event.QuitGameEvent =>
        quit()

      case _ =>
        writeline("") // No-op fallback
    }
  }

  // war mal private def quit(): Unit = {
  protected def quit(): Unit = {
    writeline(TextDisplay.clearTerminal())
    writeline("Exiting...")
    terminal.close()
  }
}
