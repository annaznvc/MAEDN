package de.htwg.se.MAEDN.model

package model

import scala.util.Random
import de.htwg.se.MAEDN.util.{Dice, DifficultyLevel, FieldType, PlayerColor}

// The Game holds players, the board, and the game state
class Game(playerNames: List[String], boardSize: Int, figuresPerPlayer: Int) {

  val players: List[Player] = createPlayers()
  var currentPlayerIndex: Int = 0
  val board: Board = new Board(playerNames.size, boardSize, figuresPerPlayer)
  placeFiguresAtHome()
  val dice = new Dice()

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
    if (figure.isFinished) return false // Can't move finished figure

    figure.position match {
      case Some(currentField) =>
        currentField.fieldType match {
          case FieldType.Home =>
            if (steps == 6) {
              // Move from Home to Start field
              val startFieldOpt = board.fields.find(f =>
                f.fieldType == FieldType.Start && f.occupiedBy.isEmpty
              )

              startFieldOpt match {
                case Some(startField) =>
                  currentField.occupiedBy = None
                  startField.occupiedBy = Some(figure)
                  figure.position = Some(startField)
                  true
                case None =>
                  // No free Start field (rare)
                  false
              }
            } else {
              // Not a 6 -> can't leave Home
              false
            }

          case FieldType.Start | FieldType.OnBoard | FieldType.Goal =>
            val currentPosition = currentField.id
            val newPosition = currentPosition + steps

            if (newPosition > board.fields.length) {
              // Overshoot -> cannot move
              false
            } else if (newPosition == board.fields.length) {
              figure.isFinished = true
              currentField.occupiedBy = None
              figure.position = None
              true
            } else {
              val newField = board.fieldById(newPosition).get
              if (newField.occupiedBy.isDefined) {
                // Kick the other figure
                val other = newField.occupiedBy.get
                other.position = None
              }
              currentField.occupiedBy = None
              newField.occupiedBy = Some(figure)
              figure.position = Some(newField)
              true
            }
        }

      case None =>
        // Figure has no position set yet (error)
        false
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
