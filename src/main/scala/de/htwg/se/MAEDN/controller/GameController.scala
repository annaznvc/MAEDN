package de.htwg.se.MAEDN.controller

import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util._

class GameController(val game: Game): //Klasse bekommt paramter namens game vom Typ Game

  def currentPlayer: Player = game.currentPlayer //rückgabewert player

  def roll(): Int =
    game.rollDice()

  def move(figureId: Int, steps: Int): Boolean =
    val player = currentPlayer
   /**
     * Dollarzeichen bei String Interpolations, um verkettungen zu umgehen
     * s vor String aktiviert interpolation
     * dollar fügt werte der variablen in string ein
     */
    println(s"[DEBUG] move() called for ${player.name}, figure $figureId, steps = $steps")
  
    val updatedPlayer = game.moveFigure(player, figureId, steps) //bewegunng ausführen, neuer Spielerzustand mit aktualiserter Figur
  
    /**
      * Prüfen:
        1) hat sich spieler als ganzes verändert?
        2) hat sich status des spielers verändert? (out, in game)
        3)Gibts eine Figur, deren Zustand jetzt anders ist als vor dem Zug?
      */
    val hasChanged = 
      updatedPlayer != player ||
      updatedPlayer.status != player.status ||
      updatedPlayer.figures.exists { f =>
        val original = player.figureById(f.id) //holt die Figur mit der ID von player
        original.exists(_.state != f.state) //vergleich state der original figur mit zustand der neuen figur
      }

    if hasChanged then //wenn sich was geändert hat, also has changed true ist
      game.players = game.players.updated(game.currentPlayerIndex, updatedPlayer) //neue Liste mit aktuelisiertem Spieler
      true
    else
      false


  def endTurn(): Unit =
    game.nextPlayer()

  def isGameOver: Boolean = game.isGameOver
