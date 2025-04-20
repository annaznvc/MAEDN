package de.htwg.se.MAEDN.view

import de.htwg.se.MAEDN.controller._
import de.htwg.se.MAEDN.model._
import scala.io.StdIn.readLine

class TextUI(controller: GameController):

  def run(): Unit =
    println("\nThe game begins!")

    while !controller.isGameOver do
      val player = controller.currentPlayer
      readLine(s"\n${player.name}'s turn (${player.color}), press ENTER to roll the dice...")
      val roll = controller.roll()

      clearScreen()
      println(s"${player.name} rolled a $roll")
      println("Current Board:")
      println(renderBoard())

      println("Your Figures:")
      for (figure, i) <- player.figures.zipWithIndex do
        println(s"  [$i] ${figure.state}")

      val board = controller.game.board

      val validFigures = player.figures.zipWithIndex.flatMap {
        case (Figure(_, _, Home), i) if roll == 6 =>
          val startPos = board.startPosition(player.color)
          val blocked = player.hasFigureAt(startPos)
          if blocked then None else Some(i)

        case (fig @ Figure(_, _, OnBoard(pos)), i) =>
          val path = board.boardPath
          val goalEntry = board.goalEntryPosition(player.color)
          val goalPath = board.goalPath(player.color)
          val currentIndex = path.indexWhere(p => p.x == pos.x && p.y == pos.y)

          if currentIndex == -1 then None
          else
            val goalEntryIndex = path.indexWhere(p => p.x == goalEntry.x && p.y == goalEntry.y)
            val newIndex = currentIndex + roll
            val crossesGoalEntry =
              (currentIndex <= goalEntryIndex && newIndex > goalEntryIndex) ||
              (goalEntryIndex < currentIndex && (newIndex % path.length) > goalEntryIndex)

            if crossesGoalEntry then
              val distanceToEntry = (path.length + goalEntryIndex - currentIndex) % path.length
              if roll > distanceToEntry then
                val stepsIntoGoal = roll - distanceToEntry - 1
                if stepsIntoGoal >= 0 && stepsIntoGoal < goalPath.length then
                  val newPos = goalPath(stepsIntoGoal)
                  val blocked = controller.game.players.exists(_.figures.exists {
                    case f if f.id == fig.id => false
                    case Figure(_, _, Goal(p2)) => p2 == newPos
                    case Figure(_, _, Finished) => goalPath.last == newPos
                    case _ => false
                  })
                  if blocked then None else Some(i)
                else None
              else
                val nextIndex = (currentIndex + roll) % path.length
                val newPos = path(nextIndex)
                if player.hasFigureAt(newPos) then None else Some(i)
            else
              val nextIndex = (currentIndex + roll) % path.length
              val newPos = path(nextIndex)
              if player.hasFigureAt(newPos) then None else Some(i)

        case (fig @ Figure(_, _, Goal(pos)), i) =>
          val goalPath = board.goalPath(player.color)
          val currentIndex = goalPath.indexWhere(p => p.x == pos.x && p.y == pos.y)
          if currentIndex == -1 then None
          else
            val targetIndex = currentIndex + roll
            if targetIndex >= goalPath.length then
              None
            else
              val pathToCheck = goalPath.slice(currentIndex + 1, targetIndex + 1)
              val blocked = pathToCheck.exists(pos =>
                controller.game.players.exists(_.figures.exists {
                  case f if f.id == fig.id => false
                  case Figure(_, _, Goal(p2)) => p2 == pos
                  case Figure(_, _, Finished) => goalPath.last == pos
                  case _ => false
                })
              )
              if blocked then
                val remainingFields = goalPath.slice(currentIndex + 1, goalPath.length)
                val allBlocked = remainingFields.forall(pos =>
                  controller.game.players.exists(_.figures.exists {
                    case f if f.id == fig.id => false
                    case Figure(_, _, Goal(p2)) => p2 == pos
                    case Figure(_, _, Finished) => goalPath.last == pos
                    case _ => false
                  })
                )
                if allBlocked then Some(i) else None
              else Some(i)

        case _ => None
      }

      if validFigures.isEmpty then
        println("No valid moves. Turn skipped.")
      else if validFigures.size == 1 then
        val onlyIndex = validFigures.head
        val figure = player.figures(onlyIndex)
        val moved = controller.move(figure.id, roll)
        println(if moved then "Moved." else "No valid move. Turn skipped.")
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

  def generateLayout(): Array[Array[String]] =
    val layout = Array.fill(11, 11)(formatField(""))

    // Main path
    for pos <- controller.game.board.boardPath do
      layout(pos.y)(pos.x) = formatField("o")

    // Goal paths
    val blueGoals = List(Position(5, 9), Position(5, 8), Position(5, 7), Position(5, 6))
    val yellowGoals = List(Position(1, 5), Position(2, 5), Position(3, 5), Position(4, 5))
    val redGoals = List(Position(9, 5), Position(8, 5), Position(7, 5), Position(6, 5))
    val greenGoals = List(Position(5, 1), Position(5, 2), Position(5, 3), Position(5, 4))

    redGoals.foreach(p => layout(p.y)(p.x) = formatField("R"))
    greenGoals.foreach(p => layout(p.y)(p.x) = formatField("G"))
    blueGoals.foreach(p => layout(p.y)(p.x) = formatField("B"))
    yellowGoals.foreach(p => layout(p.y)(p.x) = formatField("Y"))

    // Home fields
    layout(0)(9) = formatField("G"); layout(0)(10) = formatField("G"); layout(1)(9) = formatField("G"); layout(1)(10) = formatField("G")
    layout(9)(0) = formatField("B"); layout(9)(1) = formatField("B"); layout(10)(0) = formatField("B"); layout(10)(1) = formatField("B")
    layout(0)(0) = formatField("Y"); layout(0)(1) = formatField("Y"); layout(1)(0) = formatField("Y"); layout(1)(1) = formatField("Y")
    layout(9)(9) = formatField("R"); layout(9)(10) = formatField("R"); layout(10)(9) = formatField("R"); layout(10)(10) = formatField("R")

    // Center
    layout(5)(5) = formatField("X")

    // Player figures
    for player <- controller.game.players do
      for figure <- player.figures do
        figure.state match
          case OnBoard(pos) =>
            layout(pos.y)(pos.x) = formatField(s"${player.color.toString.head}${figure.id % 4}")
          case Goal(pos) =>
            layout(pos.y)(pos.x) = formatField(s"${player.color.toString.head}${figure.id % 4}")
          case Finished =>
            val goalPath = controller.game.board.goalPath(player.color)
            val pos = goalPath.last
            layout(pos.y)(pos.x) = formatField(s"${player.color.toString.head}${figure.id % 4}")
          case _ => // Home not shown

    layout

  def renderBoard(): String =
    val layout = generateLayout()
    layout.map(_.mkString("")).mkString("\n")

  def formatField(content: String): String =
    if content.length == 3 then content
    else if content.length == 2 then s" $content"
    else if content.length == 1 then s" $content "
    else "   "

  def clearScreen(): Unit =
    print("\u001b[2J")  // clear terminal
    print("\u001b[H")   // move cursor to top-left
