package de.htwg.se.MAEDN.aview

import de.htwg.se.MAEDN.model.{IState, State, Board, Field, FieldType}
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.model.states.RunningState
import de.htwg.se.MAEDN.model.Player

import scala.io.AnsiColor._

object TextDisplay {
  def clearTerminal(): String = {
    "\u001b[2J\u001b[H"
  }

  def printCover(menuState: IState): String = {
    menuState.state match {
      case State.Menu =>
        s"""${RED}Menu${RESET}
           |${menuState.moves} moves
           |${menuState.getPlayerCount} players
           |${menuState.getBoardSize}x${menuState.getBoardSize} Board
           |${menuState.getFigureCount} Figures
           |""".stripMargin
      case State.Config =>
        s"""${YELLOW}Config${RESET}
           |${menuState.moves} moves
           |${menuState.getPlayerCount} players
           |${menuState.getBoardSize}x${menuState.getBoardSize} board
           |${menuState.getFigureCount} Figures
           |""".stripMargin
      case State.Running =>
        s"""${GREEN}Running${RESET}
            |${menuState.moves} moves
            |Current Player: ${menuState.getCurrentPlayer + 1}
            |Selected Figure: ${menuState
            .asInstanceOf[RunningState]
            .selectedFigure + 1}
            |Rolled: ${menuState.rolled}
            |${menuState.getPlayerCount} players
            |${menuState.getBoardSize}x${menuState.getBoardSize} board
            |${menuState.getFigureCount} Figures
            |""".stripMargin

    }
  }

  def printBoard(
      board: Board,
      selectedFigure: Int = -1,
      currentPlayerIndex: Int = -1,
      players: List[Player] = Nil
  ): String = {
    def colorCode(c: Color): String = c match {
      case Color.RED    => RED
      case Color.BLUE   => BLUE
      case Color.GREEN  => GREEN
      case Color.YELLOW => YELLOW
      case Color.WHITE  => WHITE
    }

    def renderField(f: Field, isSelected: Boolean): String = {
      val baseColor = colorCode(f.color)
      val content = f.figure match {
        case Some(fig) =>
          if (isSelected) s">${fig.id}<" else s"F${fig.id}"
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

    // === Home Fields ===
    sb.append(s"${BOLD}Home Fields:${RESET}\n")

    val groupedHomeFields =
      players.map(p => board.homeFields.filter(_.color == p.color))

    for ((player, i) <- players.zipWithIndex) {
      val color = colorCode(player.color)
      val label = player.color.toString.capitalize
      val group = groupedHomeFields(i)
      val rendered = group
        .map { f =>
          val isSelected =
            i == currentPlayerIndex &&
              f.figure.exists(_.id == selectedFigure + 1)
          renderField(f, isSelected)
        }
        .mkString(" ")
      sb.append(s"$color$label:$RESET $rendered\n")
    }

    sb.append("\n")

    // === Main Board ===
    sb.append(s"${BOLD}Main Board:${RESET}\n")

    val perPlayer = board.size
    val figuresPerPlayer =
      if (players.nonEmpty)
        board.homeFields.count(_.color == players.head.color)
      else 0
    val perSection = perPlayer + 1 + figuresPerPlayer
    val sections = board.fields.grouped(perSection).toList

    for ((section, i) <- sections.zipWithIndex) {
      val labelColor = section
        .find(_.fieldType == FieldType.Start)
        .map(_.color)
        .getOrElse(Color.WHITE)
      val color = colorCode(labelColor)
      val rendered = section.map(f => renderField(f, false)).mkString(" ")
      sb.append(s"$rendered\n")
    }

    sb.toString()
  }

  def printFlatBoard(board: Board): String = {
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
    sb.append(s"${BOLD}Main Board:${RESET}\n")

    board.fields.foreach { field =>
      sb.append(renderField(field)).append(" ")
    }

    sb.toString().trim + "\n"
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
