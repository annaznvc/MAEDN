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
      readLine(s"${player.name}, press ENTER to roll the dice...")
      val roll = controller.roll()
      println(s"${player.name} rolled a $roll")

      println("Current Board:")
      println(renderBoard())

      println("Your Figures:")
      for (figure, i) <- player.figures.zipWithIndex do
        println(s"  [$i] ${figure.state}")

      val validFigures = player.figures.zipWithIndex.flatMap {
        case (Figure(_, _, Home), i) if roll == 6 => Some(i)
        case (Figure(_, _, OnBoard(_)), i) => Some(i)
        case (Figure(_, _, Goal(pos)), i) =>
          val goalPath = controller.game.board.goalPath(player.color)
          val idx = goalPath.indexOf(pos)
          if idx != -1 && idx + roll < goalPath.length then Some(i)
          else None
        case _ => None
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
              case Some(figIndex) if validFigures.contains(figIndex) =>
                val figure = player.figures(figIndex)
                moved = controller.move(figure.id, roll)

                println(if moved then "Moved." else "Invalid move.")
              case _ =>
                println("Invalid input. Try again.")

      controller.endTurn()

    println("\nGame over!")
    for player <- controller.game.players do
      player.status match
        case Out(place) => println(s"${player.name} placed $place")
        case _ => println(s"${player.name} did not finish")

  def generateLayout(): Array[Array[String]] = {
    val layout = Array.fill(11, 11)("   ")

    // Main path
    for pos <- controller.game.board.boardPath do
      layout(pos.y)(pos.x) = " o "

    // Goal paths
    val blueGoals = List(Position(5, 9), Position(5, 8), Position(5, 7), Position(5, 6))
    val yellowGoals = List(Position(1, 5), Position(2, 5), Position(3, 5), Position(4, 5))
    val redGoals = List(Position(9, 5), Position(8, 5), Position(7, 5), Position(6, 5))
    val greenGoals = List(Position(5, 1), Position(5, 2), Position(5, 3), Position(5, 4))

    redGoals.foreach(p => layout(p.y)(p.x) = " R ")
    greenGoals.foreach(p => layout(p.y)(p.x) = " G ")
    blueGoals.foreach(p => layout(p.y)(p.x) = " B ")
    yellowGoals.foreach(p => layout(p.y)(p.x) = " Y ")

    // Home fields
    layout(0)(9) = " G "; layout(0)(10) = " G "; layout(1)(9) = " G "; layout(1)(10) = " G " // Green top right
    layout(9)(0) = " B "; layout(9)(1) = " B "; layout(10)(0) = " B "; layout(10)(1) = " B " // Blue bottom left
    layout(0)(0) = " Y "; layout(0)(1) = " Y "; layout(1)(0) = " Y "; layout(1)(1) = " Y "   // Yellow top left
    layout(9)(9) = " R "; layout(9)(10) = " R "; layout(10)(9) = " R "; layout(10)(10) = " R " // Red bottom right


    // Center
    layout(5)(5) = " X "

    // Player figures
    for player <- controller.game.players do
      for figure <- player.figures do
        figure.state match
          case OnBoard(pos) =>
            layout(pos.y)(pos.x) = s" ${player.color.toString.head}${figure.id % 4} "
          case Goal(pos) =>
            layout(pos.y)(pos.x) = s" ${player.color.toString.head}${figure.id % 4} "
          case Finished =>
           val goalPath = controller.game.board.goalPath(player.color)
           val pos = goalPath.last
           layout(pos.y)(pos.x) = s" ${player.color.toString.head}${figure.id % 4} "
          case _ => // Home not shown


    layout
  }

  def renderBoard(): String = {
    val layout = generateLayout()
    layout.map(_.mkString("")).mkString("\n")
  }
