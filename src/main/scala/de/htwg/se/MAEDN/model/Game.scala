package de.htwg.se.MAEDN.model

package model

import scala.util.Random
import de.htwg.se.MAEDN.util.{Dice, DifficultyLevel, PlayerColor}

// The Game holds players, the board, and the game state
class Game(playerNames: List[String], difficulty: DifficultyLevel.Value) {

  val players: List[Player] = createPlayers()
  var currentPlayerIndex: Int = 0
  val board: Board = new Board(players.size, difficulty)
  val dice = new Dice()

  // Create players and their figures
  private def createPlayers(): List[Player] = {
    val availableColors = PlayerColor.values.toList
    playerNames.zipWithIndex.map { case (name, index) =>
      val color = availableColors(index % availableColors.length)
      val player = Player(name, Nil, color)
      val figures = (1 to 4).map(i => Figure(player, i)).toList
      player.copy(figures = figures)
    }
  }

  // Get the player whose turn it is
  def currentPlayer: Player = players(currentPlayerIndex)

  def rollDice(): Int = dice.roll()
  // Move a figure by a number of steps
  def moveFigure(figure: Figure, steps: Int): Unit = {
    if (figure.isFinished) return // Cannot move finished figure

    val currentPosition = figure.position.map(_.id).getOrElse(0)
    val newPosition = currentPosition + steps

    if (newPosition > board.fields.length) {
      // Overshoot: stay in place
      return
    } else if (newPosition == board.fields.length) {
      figure.isFinished = true
      figure.position.foreach(_.occupiedBy = None)
      figure.position = None
    } else {
      figure.position.foreach(_.occupiedBy = None) // Clear old field
      val newField = board.fieldById(newPosition).get
      if (newField.occupiedBy.isDefined) {
        // If occupied: kick the other figure back to start
        val other = newField.occupiedBy.get
        other.position = None
      }
      newField.occupiedBy = Some(figure)
      figure.position = Some(newField)
    }
  }

  // End turn and switch to next player
  def nextPlayer(): Unit = {
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size
  }

  // Check if a player has won
  def hasWinner: Option[Player] = {
    players.find(p => p.figures.forall(_.isFinished))
  }
}
