package de.htwg.se.MAEDN.controller

import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util._

class GameController(val game: Game):

  def currentPlayer: Player = game.currentPlayer

  def roll(): Int =
    game.rollDice()

  def move(figureId: Int, steps: Int): Boolean =
    val player = currentPlayer
    println(s"[DEBUG] move() called for ${player.name}, figure $figureId, steps = $steps")
  
    val updatedPlayer = game.moveFigure(player, figureId, steps)
  
    println(s"[DEBUG] Is player changed? ${updatedPlayer != player}")
    if updatedPlayer == player then
      false
    else
      game.players = game.players.updated(game.currentPlayerIndex, updatedPlayer)
      true


  def endTurn(): Unit =
    game.nextPlayer()

  def isGameOver: Boolean = game.isGameOver
