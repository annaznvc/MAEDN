package de.htwg.se.MAEDN.model

import scala.util.Random
import de.htwg.se.MAEDN.util.{Dice, DifficultyLevel, FieldType, PlayerColor}

// The Game holds players, the board, and the game state
class Game(playerNames: List[String], boardSize: Int, figuresPerPlayer: Int, testDice: Option[Dice] = None) {

  val players: List[Player] = createPlayers()
  var currentPlayerIndex: Int = 0
  val board: Board = new Board(playerNames.size, boardSize, figuresPerPlayer)
  placeFiguresAtHome()
  val dice = testDice.getOrElse(new Dice())

  // Create players and their figures
  private def createPlayers(): List[Player] = {
    val availableColors = PlayerColor.values.toList
    playerNames.zipWithIndex.map { case (name, index) =>
      val color = availableColors(index % availableColors.length)
      val player = Player(name, Nil, color)
      val figures = (1 to figuresPerPlayer).map(i => Figure(player, i)).toList
      player.copy(figures = figures)
    }
  }


  // Get the player whose turn it is
  def currentPlayer: Player = players(currentPlayerIndex)

  def rollDice(): Int = dice.roll()
  // Move a figure by a number of steps
  def moveFigure(figure: Figure, steps: Int): Boolean = {
    GameLogic.moveFigure(board, figure, steps)
  }

  // End turn and switch to next player
  def nextPlayer(): Unit = {
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size
  }

  // Check if a player has won
  def hasWinner: Option[Player] = {
    players.find(p => p.figures.forall(_.isFinished))
  }

  private def placeFiguresAtHome(): Unit = {
    for (player <- players) {
      val homeFields = board.fields.filter(f =>
        f.fieldType == FieldType.Home && f.ownerColor.contains(player.color)
      )

      // Assign each figure to a free home field
      player.figures.zip(homeFields).foreach { case (figure, homeField) =>
        figure.position = Some(homeField)
        homeField.occupiedBy = Some(figure)
      }
    }
  }


}
