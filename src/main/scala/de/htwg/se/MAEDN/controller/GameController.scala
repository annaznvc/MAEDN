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
  
    val hasChanged =
      updatedPlayer != player ||
      updatedPlayer.status != player.status ||
      updatedPlayer.figures.exists { f =>
        val original = player.figureById(f.id)
        original.exists(_.state != f.state)
      }

    if hasChanged then
      game.players = game.players.updated(game.currentPlayerIndex, updatedPlayer)
      true
    else
      false


  def endTurn(): Unit =
    game.nextPlayer()

  def isGameOver: Boolean = game.isGameOver
