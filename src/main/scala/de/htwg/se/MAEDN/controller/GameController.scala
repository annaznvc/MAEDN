package de.htwg.se.MAEDN.controller

import de.htwg.se.MAEDN.model.model.Game
import de.htwg.se.MAEDN.model.{Field, Figure, Player}
import de.htwg.se.MAEDN.util.DifficultyLevel

// Controller connects View and Model
class GameController(playerNames: List[String], difficulty: DifficultyLevel.Value) {

  private val game = new Game(playerNames, difficulty) // Our model: Game

  // Roll the dice and return the result
  def rollDice(): Int = {
    game.rollDice()
  }

  // Move a figure by ID (1-4) for the current player
  def moveFigure(figureId: Int, steps: Int): Boolean = {
    val figureOpt = game.currentPlayer.figures.find(_.id == figureId)
    figureOpt match {
      case Some(figure) =>
        game.moveFigure(figure, steps)
        true
      case None =>
        false
    }
  }

  // End current player's turn and return the new current player
  def nextPlayer(): Player = {
    game.nextPlayer()
    game.currentPlayer
  }

  // Check if there is a winner
  def winner: Option[Player] = {
    game.hasWinner
  }

  // Get the current player
  def currentPlayer: Player = {
    game.currentPlayer
  }

  // Get all players
  def allPlayers: List[Player] = {
    game.players
  }

  // Get all fields (board)
  def boardFields: List[Field] = {
    game.board.fields
  }
}
