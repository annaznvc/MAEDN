package de.htwg.se.MAEDN

import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.controller._
import de.htwg.se.MAEDN.view.TextUI

import scala.io.StdIn.readLine

@main def main(): Unit =
  println("Welcome to Mensch ärgere dich nicht!\n")

  val availableColors = List(Color.Red, Color.Blue, Color.Green, Color.Yellow)

  // Ask for player count
  val numberOfPlayers = Iterator
    .continually(readLine("How many players? (2 to 4): ").trim)
    .map(_.toIntOption)
    .collectFirst { case Some(n) if n >= 2 && n <= 4 => n }
    .get

  // Ask for player names
  val players = (0 until numberOfPlayers).map { i =>
    val color = availableColors(i)
    val name = readLine(s"Enter name for Player ${i + 1} (Color: $color): ").trim match
      case "" => s"Player${i + 1}"
      case s  => s

    Player(
      id = i,
      name = name,
      color = color,
      figures = (0 to 3).toList.map(j => Figure(i * 4 + j, color, Home))
    )
  }.toList

  val game = Game(players)
  val controller = GameController(game)
  val tui = TextUI(controller)

  tui.run()
