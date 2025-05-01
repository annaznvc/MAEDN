package de.htwg.se.MAEDN.aview

import de.htwg.se.MAEDN.model.{IState, State, Board, Field, FieldType}
import de.htwg.se.MAEDN.util.Color

import scala.io.AnsiColor._

object TextDisplay {
  def clearTerminal(): String = {
    // ANSI escape code to clear the terminal
    "\u001b[2J\u001b[H"
  }

  def printCover(menuState: IState): String = {
    menuState.state match {
      case State.Menu =>
        s"""${RED}Menu${RESET}
                   |${menuState.moves} moves
                   |${menuState.getPlayerCount} players
                   |${menuState.getBoardSize}x${menuState.getBoardSize} board
                   |""".stripMargin
      case State.Config =>
        s"""${YELLOW}Config${RESET}
                   |${menuState.moves} moves
                   |${menuState.getPlayerCount} players
                   |${menuState.getBoardSize}x${menuState.getBoardSize} board
                   |""".stripMargin
      case State.Running =>
        s"""${GREEN}Running${RESET}
                   |${menuState.moves} moves
                   |${menuState.getPlayerCount} players
                   |${menuState.getBoardSize}x${menuState.getBoardSize} board
                   |""".stripMargin
    }
  }

  def printBoard(board: Board): String = {
    def colorCode(c: Color): String = c match {
      case Color.RED    => RED
      case Color.BLUE   => BLUE
      case Color.GREEN  => GREEN
      case Color.YELLOW => YELLOW
      case Color.WHITE  => WHITE
    }

    def renderField(f: Field): String = {
      val baseColor = colorCode(f.color)
      val content = f.figure match {
        case Some(fig) => s"F${fig.id}"
        case None =>
          f.fieldType match {
            case FieldType.Home   => "H "
            case FieldType.Start  => "S "
            case FieldType.Goal   => "G "
            case FieldType.Normal => "N "
          }
      }
      s"$baseColor$content$RESET"
    }

    val sb = new StringBuilder

    // 1. Print Home fields
    sb.append(s"${BOLD}Home Fields:${RESET}\n")
    val perGoal = board.homeFields.size / 4
    val groupedHome =
      if (perGoal > 0) board.homeFields.grouped(perGoal).toList
      else List.fill(4)(List.empty[Field]) // empty placeholders
    val labels = List("Red", "Blue", "Green", "Yellow")

    for ((group, i) <- groupedHome.zipWithIndex) {
      val label = if (i < labels.length) labels(i) else s"Player${i + 1}"
      val color = colorCode(
        group.headOption.map(_.color).getOrElse(Color.WHITE)
      )
      val rendered = group.map(renderField).mkString(" ")
      sb.append(s"$color$label:$RESET $rendered\n")
    }

    sb.append("\n")

    // 2. Print Main Grid
    sb.append(s"${BOLD}Main Board:${RESET}\n")
    val perPlayer = board.size
    val perSection = perPlayer + 1 + perGoal
    val sections = board.fields.grouped(perSection).toList

    for ((section, i) <- sections.zipWithIndex) {
      //ursprünglich: val label = if (i < labels.length) labels(i) else s"Player${i + 1}"
      val label = labels.lift(i).getOrElse("")
      val color = colorCode(
        section.headOption.map(_.color).getOrElse(Color.WHITE)
      )
      val rendered = section.map(renderField).mkString(" ")
      sb.append(s"$color$label:$RESET $rendered\n")
    }

    sb.toString()
  }

  def printConfig(
      playerCount: Int,
      figureCount: Int,
      boardSize: Int
  ): String = {
    s"""
         Players       Figures       Board size
        ${CYAN}/\\ 'w'${RESET}       ${GREEN}/\\ 'e'${RESET}         ${YELLOW}/\\ 'r'${RESET}
         |             |              |
        ${CYAN}$playerCount${RESET}             ${GREEN}$figureCount${RESET}              ${YELLOW}$boardSize${RESET}
         |             |              |
        ${CYAN}\\/ 's'${RESET}       ${GREEN}\\/'d'${RESET}         ${YELLOW}\\/'f'${RESET}

    ${BOLD}Press [Space] to start a new game${RESET}
  """.stripMargin
  }

}
