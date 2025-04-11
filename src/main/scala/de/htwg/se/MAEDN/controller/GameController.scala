package controller

import model._
import util.Dice

class GameController(val game: Game):

  def currentPlayer: Player = game.currentPlayer

  def roll(): Int =
    val result = game.rollDice()
    println(s"${currentPlayer.name} rolled a $result")
    result

  def move(figureId: Int, steps: Int): Unit =
    val player = currentPlayer
    val updatedPlayer = game.moveFigure(player, figureId, steps)

    // ✅ Update the player in the list
    game.players = game.players.updated(game.currentPlayerIndex, updatedPlayer)

  def endTurn(): Unit =
    game.nextPlayer()

  def isGameOver: Boolean = game.isGameOver
