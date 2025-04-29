package de.htwg.se.MAEDN.controller

import de.htwg.se.MAEDN.model.model.Game
import de.htwg.se.MAEDN.model.{Field, Figure, Player}
import de.htwg.se.MAEDN.util.{DifficultyLevel, FieldType}

// Controller connects View and Model
class GameController(playerNames: List[String], boardSize: Int = 40, figuresPerPlayer: Int = 4) {

  private val game = new Game(playerNames, boardSize, figuresPerPlayer)
  private var bonusTurn: Boolean = false
  var remainingTries: Int = 1


  // Roll the dice and return the result
  def rollDice(): Int = {
    val roll = game.rollDice()

    if (roll == 6) {
      bonusTurn = true
    }

    roll
  }

  def isBonusTurn: Boolean = bonusTurn

  def consumeBonus(): Unit = {
    bonusTurn = false
  }

  def prepareTurn(): Unit = {
    val allAtHome = game.currentPlayer.figures.forall(_.position.exists(_.fieldType == FieldType.Home))
    remainingTries = if (allAtHome) 3 else 1
  }

  def canRoll: Boolean = remainingTries > 0

  def rollDiceForTurn(): Int = {
    if (!canRoll) throw new IllegalStateException("No tries left.")

    val roll = rollDice()
    remainingTries -= 1
    roll
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

  def allFiguresAtHome: Boolean = {
    game.currentPlayer.figures.forall(_.position.exists(_.fieldType == FieldType.Home))
  }

}
