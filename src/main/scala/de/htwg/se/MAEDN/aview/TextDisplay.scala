package de.htwg.se.MAEDN.aview

import de.htwg.se.MAEDN.model.{Board, Figure, IState}
import de.htwg.se.MAEDN.model.states.RunningState
import de.htwg.se.MAEDN.model.State
import de.htwg.se.MAEDN.model.Player
import de.htwg.se.MAEDN.util.{Color, Position}
import scala.io.AnsiColor._

object TextDisplay {

  /** Clears the terminal screen */
  def clearTerminal(): String = "\u001b[2J\u001b[H"

  /** Renders the cover with the game title in state-specific color */
  def printCover(menuState: IState): String = {
    val title = "Mensch ärger dich nicht"
    val styledTitle = menuState.state match {
      case State.Menu    => s"${RED}$title${RESET}"
      case State.Config  => s"${YELLOW}$title${RESET}"
      case State.Running => s"${GREEN}$title${RESET}"
    }
    val baseInfo = Seq(
      s"${menuState.moves} moves",
      s"${menuState.getPlayerCount} players",
      s"${menuState.getBoardSize}x${menuState.getBoardSize} board",
      s"${menuState.getFigureCount} figures"
    )
    val runningInfo = menuState match {
      case rs: RunningState =>
        Seq(
          s"Current Player: ${rs.getCurrentPlayer + 1}",
          s"Selected Figure: ${rs.selectedFigure + 1}",
          s"Rolled: ${rs.rolled}"
        )
      case _ => Seq.empty
    }
    (Seq(styledTitle) ++ baseInfo ++ runningInfo).mkString("\n") + "\n"
  }

  /** Renders the game board: home benches, main track, and goal lanes */
  def printBoard(
      board: Board,
      selectedFigure: Int = -1,
      currentPlayerIndex: Int = -1,
      players: List[Player] = Nil
  ): String = {
    val size = board.size
    val figures: Seq[Figure] = players.flatMap(_.figures)

    def colorCode(c: Color): String = c match {
      case Color.RED    => RED
      case Color.BLUE   => BLUE
      case Color.YELLOW => YELLOW
      case Color.GREEN  => GREEN
      case Color.WHITE  => WHITE
    }

    // Home benches
    val homeLines = players.map { player =>
      val color = colorCode(player.color)
      val label = player.color.toString.capitalize
      val slots = player.figures
        .map { fig =>
          fig.adjustedIndex(size) match {
            case Position.Home(_) =>
              val content = s"F${fig.id}"
              if (
                player.id - 1 == currentPlayerIndex && fig.id == selectedFigure + 1
              ) s">$content<"
              else content
            case _ => "H "
          }
        }
        .mkString("\t")
      s"$color$label Home:\t$slots$RESET"
    }

    // Main track: size*4 positions, grouped per row
    val mainLines = (0 until size * 4)
      .grouped(size)
      .map { row =>
        row
          .map { pos =>
            figures.find(_.adjustedIndex(size) == Position.Normal(pos)) match {
              case Some(fig) =>
                val figColor = colorCode(fig.owner.color)
                val content = s"F${fig.id}"
                val display =
                  if (
                    fig.owner.id - 1 == currentPlayerIndex && fig.id == selectedFigure + 1
                  ) s">$content<"
                  else content
                s"$figColor$display$RESET"
              case None =>
                val startField = players.find(_.startPosition(size) == pos)
                val startColor = startField match {
                  case Some(player) => colorCode(player.color)
                  case None         => ""
                }
                startField match {
                  case Some(player) =>
                    val startColor = colorCode(player.color)
                    s"${startColor}S $RESET"
                  case None => "N "
                }
            }
          }
          .mkString("\t")
      }
      .toList

    // Goal lanes
    val goalLines = players.map { player =>
      val color = colorCode(player.color)
      val label = player.color.toString.capitalize
      val slots = (0 until player.figures.size)
        .map { goalIndex =>
          player.figures.find(
            _.adjustedIndex(size) == Position.Goal(goalIndex)
          ) match {
            case Some(fig) =>
              val content = s"F${fig.id}"
              if (
                player.id - 1 == currentPlayerIndex && fig.id == selectedFigure + 1
              ) s">$content<"
              else content
            case None => "G "
          }
        }
        .mkString("\t")
      s"$color$label Goal:\t$slots$RESET"
    }

    (Seq(s"${BOLD}Home Benches:${RESET}") ++
      homeLines ++ Seq("") ++
      Seq(s"${BOLD}Main Track:${RESET}") ++
      mainLines ++ Seq("") ++
      Seq(s"${BOLD}Goal Lanes:${RESET}") ++
      goalLines).mkString("\n") + "\n"
  }

  /** Simple flat view of the main track */
  def printFlatBoard(board: Board): String = {
    val size = board.size
    val fields = (0 until size * 4).map(_ => "?").mkString(" ")
    s"${BOLD}Main Track:${RESET}\n$fields\n"
  }

  /** Renders configuration screen. */
  def printConfig(
      playerCount: Int,
      figureCount: Int,
      boardSize: Int
  ): String = {
    s"""
         Players       Figures       Board size
        ${CYAN}/\\ 'w'${RESET}       ${GREEN}/\\ 'e'${RESET}         ${YELLOW}/\\ 't'${RESET}
         |             |              |
        ${CYAN}$playerCount${RESET}             ${GREEN}$figureCount${RESET}              ${YELLOW}$boardSize${RESET}
         |             |              |
        ${CYAN}\\/ 's'${RESET}       ${GREEN}\\/'d'${RESET}         ${YELLOW}\\/'f'${RESET}

    ${BOLD}Press [Space] to start a new game${RESET}
  """.stripMargin
  }
}
