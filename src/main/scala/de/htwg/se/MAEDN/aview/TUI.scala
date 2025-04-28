package de.htwg.se.MAEDN.aview

import de.htwg.se.MAEDN.controller.GameController
import de.htwg.se.MAEDN.model.Player
import de.htwg.se.MAEDN.util.FieldType

import scala.io.StdIn.readLine

// Text User Interface (TUI) for the game
class TUI(controller: GameController) {

  // Main game loop
  def run(): Unit = {
    println("Welcome to Mensch ärgere Dich nicht!")
    println("--------------------------------------")
    var running = true

    while (running) {
      printBoard()
      printCurrentPlayer()

      controller.prepareTurn()

      var turnContinues = true

      while (turnContinues) {
        if (!controller.canRoll && !controller.isBonusTurn) {
          println("No tries left!")
          turnContinues = false
        } else {
          println("Press Enter to roll the dice...")
          readLine()

          val diceRoll = if (controller.isBonusTurn) controller.rollDice() else controller.rollDiceForTurn()
          println(s"You rolled a $diceRoll!")

          if (controller.allFiguresAtHome && diceRoll != 6) {
            println("You didn't roll a 6, try again!")
            println(s"Tries left: ${controller.remainingTries}")

            if (!controller.canRoll) {
              println("No tries left! Turn over.")
              turnContinues = false
            }

          } else {
            println("Choose a figure to move (enter figure ID 1-4): ")
            val input = readLine()

            try {
              val figureId = input.toInt
              val moveSuccessful = controller.moveFigure(figureId, diceRoll)

              if (moveSuccessful) {
                println(s"Moved figure $figureId by $diceRoll steps.")

                printBoard() // ✅ Show updated board immediately

                controller.winner match {
                  case Some(winner) =>
                    println(s"Congratulations! ${winner.name} has won the game!")
                    running = false
                    return
                  case None =>
                }

                if (diceRoll == 6) {
                  println("You rolled a 6! Bonus roll!")
                  // Bonus: stay in turn
                } else {
                  turnContinues = false
                }

              } else {
                println("Invalid move. You may need a 6 to leave Home, or wrong figure. Try again.")
                if (diceRoll != 6) {
                  turnContinues = false
                }
              }

            } catch {
              case _: NumberFormatException =>
                println("Invalid input. Please enter a number between 1 and 4.")
            }
          }
        }
      }

      if (running) {
        controller.nextPlayer()
      }
    }
  }




  // Print the current board state
  def printBoard(): Unit = {
    val fields = controller.boardFields

    println("\nBoard State:")

    val boardString = fields.map { field =>
      field.occupiedBy match {
        case Some(figure) =>
          // Show the figure's color first letter
          s"[${figure.player.color.toString.head}]"
        case None =>
          // No figure → show by field type
          field.fieldType match {
            case FieldType.Home    => "[H]"
            case FieldType.Start   => "[S]"
            case FieldType.OnBoard => "[ ]"
            case FieldType.Goal    => "[G]"
          }
      }
    }.mkString(" ")

    println(boardString)
    println()
  }


  // Show which player's turn it is
  def printCurrentPlayer(): Unit = {
    val player = controller.currentPlayer
    println(s"It is ${player.name}'s turn (${player.color}).")
  }
}
