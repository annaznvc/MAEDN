package de.htwg.se.MAEDN.view

import de.htwg.se.MAEDN.controller._
import de.htwg.se.MAEDN.model._

import scala.io.StdIn.readLine

class TextUI(controller: GameController):

  def run(): Unit =
    println("\nThe game begins!")

    while !controller.isGameOver do
      val player = controller.currentPlayer
      println(s"\n${player.name}'s turn (${player.color})")
      val roll = controller.roll()
      println(s"${player.name} rolled a $roll")

      println("Current Board:")
      println(renderBoard())

      println("Your Figures:")
      for (figure, i) <- player.figures.zipWithIndex do
        println(s"  [$i] ${figure.state}")

      val validFigures = player.figures.zipWithIndex.collect {
        case (Figure(_, _, Home), i) if roll == 6 => i
        case (Figure(_, _, OnBoard(_)), i) => i
        case (Figure(_, _, Goal(_)), i) => i
      }

      if validFigures.isEmpty then
        println("No valid moves. Turn skipped.")
      else
        println(s"Choose a figure to move: ${validFigures.mkString("[", ", ", "]")}")

        var moved = false
        while !moved do
          val input = readLine("Enter figure number (or 'skip'): ").trim
          if input == "skip" then
            println("Turn skipped.")
            moved = true
          else
            input.toIntOption match
              case Some(figId) if validFigures.contains(figId) =>
                moved = controller.move(figId, roll)
                println(if moved then "Moved." else "Invalid move.")
              case _ =>
                println("Invalid input. Try again.")

      controller.endTurn()

    println("\nGame over!")
    for player <- controller.game.players do
      player.status match
        case Out(place) => println(s"${player.name} placed $place")
        case _ => println(s"${player.name} did not finish")

  def renderBoard(): String = {
  val board = BoardLayout.layout
  val height = board.length
  val width = if board.nonEmpty then board.head.length else 0

  val figureMap: Map[Position, String] =
    controller.game.players.flatMap { player =>
      player.figures.collect {
        case f if f.state match
          case OnBoard(_) | Goal(_) => true
          case _ => false
        =>
          val pos = f.state match
            case OnBoard(p) => p
            case Goal(p)    => p
          pos -> s"${player.color.toString.head}${f.id % 4}"
      }
    }.toMap

  val rows = for y <- 0 until height yield
    val cols = for x <- 0 until width yield
      val pos = Position(x, y)
      figureMap.getOrElse(pos, board(y)(x))
    cols.mkString(" ")
  rows.mkString("\n")
}
