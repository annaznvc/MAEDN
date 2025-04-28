package de.htwg.se.MAEDN.aview

import de.htwg.se.MAEDN.controller.GameController
import de.htwg.se.MAEDN.model.Player
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

      println("Press Enter to roll the dice...")
      readLine()
      val diceRoll = controller.rollDice()
      println(s"You rolled a $diceRoll!")

      println("Choose a figure to move (enter figure ID 1-4): ")
      val input = readLine()

      try {
        val figureId = input.toInt
        val moveSuccessful = controller.moveFigure(figureId, diceRoll)

        if (moveSuccessful) {
          println(s"Moved figure $figureId by $diceRoll steps.")
        } else {
          println(s"Invalid figure ID or move not possible. Please try again.")
        }

      } catch {
        case _: NumberFormatException =>
          println("Invalid input. Please enter a number between 1 and 4.")
      }

      controller.winner match {
        case Some(winner) =>
          println(s"Congratulations! ${winner.name} has won the game!")
          running = false
        case None =>
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
        case Some(figure) => s"[${figure.player.color.toString.head}]"
        case None         => "[ ]"
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
