import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._
import controller._
import util.Dice

class GameTest extends AnyWordSpec with Matchers:

  "A Game" should {

    "return the current player and cycle to the next player" in {
      val figs1 = (1 to 4).map(i => Figure(i, Color.Red, Home)).toList
      val figs2 = (5 to 8).map(i => Figure(i, Color.Blue, Home)).toList
      val player1 = Player(1, "Alice", Color.Red, figs1)
      val player2 = Player(2, "Bob", Color.Blue, figs2)

      val game = Game(List(player1, player2))
      game.currentPlayer.name shouldBe "Alice"

      game.nextPlayer()
      game.currentPlayer.name shouldBe "Bob"

      game.nextPlayer()
      game.currentPlayer.name shouldBe "Alice"
    }

    "detect game over when a player has all figures finished" in {
      val finishedFigs = (1 to 4).map(i => Figure(i, Color.Green, Finished)).toList
      val unfinishedFigs = (1 to 4).map(i => Figure(i, Color.Red, Home)).toList
      val winner = Player(1, "Greenie", Color.Green, finishedFigs)
      val other = Player(2, "Red", Color.Red, unfinishedFigs)

      val game = Game(List(other, winner))
      game.isGameOver shouldBe true
    }

    "move a figure on the board" in {
      val figures = List(
        Figure(1, Color.Red, OnBoard(Position(3, 0))),
        Figure(2, Color.Red, Home),
        Figure(3, Color.Red, Home),
        Figure(4, Color.Red, Home)
      )
      val player = Player(1, "Mover", Color.Red, figures)
      val game = Game(List(player))

      val updatedPlayer = game.moveFigure(player, 1, 2)
      val movedFigure = updatedPlayer.figureById(1).get

      movedFigure.state shouldBe OnBoard(Position(5, 0))
    }

    "not move a figure that is not on board" in {
      val figures = List(
        Figure(1, Color.Red, Home),
        Figure(2, Color.Red, Home),
        Figure(3, Color.Red, Home),
        Figure(4, Color.Red, Home)
      )
      val player = Player(1, "Static", Color.Red, figures)
      val game = Game(List(player))

      val updatedPlayer = game.moveFigure(player, 1, 3)
      updatedPlayer.figureById(1).get.state shouldBe Home
    }

        "roll a number between 1 and 6" in {
      val figs = (1 to 4).map(i => Figure(i, Color.Red, Home)).toList
      val player = Player(1, "Roller", Color.Red, figs)
      val game = Game(List(player))

      val roll = game.rollDice()
      roll should (be >= 1 and be <= 6)
    }

        "leave figures unchanged if figure is not on board" in {
      val figs = List(
        Figure(1, Color.Red, Home),
        Figure(2, Color.Red, Home),
        Figure(3, Color.Red, Home),
        Figure(4, Color.Red, Home)
      )
      val player = Player(1, "NoMove", Color.Red, figs)
      val game = Game(List(player))

      val result = game.moveFigure(player, 1, 3)
      result shouldBe player // kein Unterschied, Figur wurde nicht bewegt
    }

  }
