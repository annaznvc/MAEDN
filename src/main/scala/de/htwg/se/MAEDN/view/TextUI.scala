package de.htwg.se.MAEDN.view

import de.htwg.se.MAEDN.controller._
import de.htwg.se.MAEDN.model._
import scala.io.StdIn.readLine

class TextUI(controller: GameController):

  def run(): Unit =
    println("\nThe game begins!")

    while !controller.isGameOver do
      var player = controller.currentPlayer

      val checkedPlayer = controller.game.checkForFinish(player)
      if checkedPlayer != player then
        controller.game.players = controller.game.players.updated(controller.game.currentPlayerIndex, checkedPlayer)
        controller.endTurn()
      else
        while player.status.isInstanceOf[Out] do
          controller.endTurn()
          player = controller.currentPlayer

        val allAtHome = player.figures.forall(_.isAtHome)
        var remainingRolls = if (allAtHome) 3 else 1
        var takeAnotherTurn = false
        var movedSuccessfully = false

        while remainingRolls > 0 do
          readLine(s"\n${player.name}'s turn (${player.color}), press ENTER to roll the dice... (${remainingRolls} roll${if remainingRolls > 1 then "s" else ""} left)")
          val roll = controller.roll()
          if roll == 6 then
            remainingRolls = 1
          else remainingRolls -= 1

          


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
              if player.hasFigureAt(startPos) then None else Some(i)

            case (fig @ Figure(_, _, OnBoard(pos)), i) =>
              val path = board.boardPath
              val goalEntry = board.goalEntryPosition(player.color)
              val goalPath = board.goalPath(player.color)
              val currentIndex = path.indexWhere(_ == pos)
              if currentIndex == -1 then None
              else
                val goalEntryIndex = path.indexWhere(_ == goalEntry)
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
                        case Figure(_, color, Goal(p)) if p == newPos && color == player.color => true
                        case Figure(_, color, Finished) if newPos == goalPath.last && color == player.color => true
                        case _ => false
                      })
                      if blocked then None else Some(i)
                    else None
                  else
                    val newPos = path((currentIndex + roll) % path.length)
                    if player.hasFigureAt(newPos) then None else Some(i)
                else
                  val newPos = path((currentIndex + roll) % path.length)
                  if player.hasFigureAt(newPos) then None else Some(i)

            case (fig @ Figure(_, _, Goal(pos)), i) =>
              val goalPath = board.goalPath(player.color)
              val currentIndex = goalPath.indexWhere(_ == pos)
              if currentIndex == -1 then None
              else
                val targetIndex = currentIndex + roll
                if targetIndex >= goalPath.length then None
                else
                  val newPos = goalPath(targetIndex)
                  val blocked = controller.game.players.exists(_.figures.exists {
                    case f if f.id == fig.id => false
                    case Figure(_, color, Goal(p)) if p == newPos && color == player.color => true
                    case Figure(_, color, Finished) if newPos == goalPath.last && color == player.color => true
                    case _ => false
                  })
                  if blocked then None else Some(i)

            case _ => None
          }

          if validFigures.isEmpty then
            println("No valid moves.")
          else if validFigures.size == 1 then
            val index = validFigures.head
            val figure = player.figures(index)
            movedSuccessfully = controller.move(figure.id, roll)
            player = controller.currentPlayer
            if movedSuccessfully && roll == 6 && figure.isOnBoard then
              takeAnotherTurn = true

            println(if movedSuccessfully then "Moved." else "No valid move.")
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
                    movedSuccessfully = moved
                    player = controller.currentPlayer
                    println(if moved then "Moved." else "Invalid move.")
                  case _ => println("Invalid input. Try again.")

        val playerAfterTurn = controller.game.currentPlayer
        val checkedAfterMove = controller.game.checkForFinish(playerAfterTurn)
        if checkedAfterMove != playerAfterTurn then
          controller.game.players = controller.game.players.updated(controller.game.currentPlayerIndex, checkedAfterMove)

        if !movedSuccessfully && !takeAnotherTurn then
          println("Turn ended.")

        controller.endTurn()

    println("\nGame over!")
    for player <- controller.game.players do
      player.status match
        case Out(place) => println(s"${player.name} placed $place")
        case _ => println(s"${player.name} did not finish")

  def renderBoard(): String =
    val layout = Array.fill(11, 11)(formatField(""))
    for pos <- controller.game.board.boardPath do layout(pos.y)(pos.x) = formatField("o")

    val redGoals = List(Position(9, 5), Position(8, 5), Position(7, 5), Position(6, 5))
    val greenGoals = List(Position(5, 1), Position(5, 2), Position(5, 3), Position(5, 4))
    val blueGoals = List(Position(5, 9), Position(5, 8), Position(5, 7), Position(5, 6))
    val yellowGoals = List(Position(1, 5), Position(2, 5), Position(3, 5), Position(4, 5))

    redGoals.foreach(p => layout(p.y)(p.x) = formatField("R"))
    greenGoals.foreach(p => layout(p.y)(p.x) = formatField("G"))
    blueGoals.foreach(p => layout(p.y)(p.x) = formatField("B"))
    yellowGoals.foreach(p => layout(p.y)(p.x) = formatField("Y"))

    for player <- controller.game.players do
      for figure <- player.figures do
        figure.state match
          case OnBoard(pos) => layout(pos.y)(pos.x) = formatField(s"${player.color.toString.head}${figure.id % 4}")
          case Goal(pos)    => layout(pos.y)(pos.x) = formatField(s"${player.color.toString.head}${figure.id % 4}")
          case Finished     =>
            val pos = controller.game.board.goalPath(player.color).last
            layout(pos.y)(pos.x) = formatField(s"${player.color.toString.head}${figure.id % 4}")
          case _ =>

    layout.map(_.mkString("")).mkString("\n")

  def formatField(content: String): String =
    if content.length == 3 then content
    else if content.length == 2 then s" $content"
    else if content.length == 1 then s" $content "
    else "   "

  def clearScreen(): Unit =
    print("\u001b[2J")
    print("\u001b[H")