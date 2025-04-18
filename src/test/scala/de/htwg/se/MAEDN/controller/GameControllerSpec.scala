package de.htwg.se.MAEDN.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.controller._
import de.htwg.se.MAEDN.model._

class GameControllerSpec extends AnyWordSpec with Matchers:

  "A GameController" should {

    "roll a dice and return a number between 1 and 6" in {
      val figs = (1 to 4).map(i => Figure(i, Color.Red, Home)).toList
      val player = Player(1, "Alice", Color.Red, figs)
      val game = Game(List(player))
      val controller = GameController(game)

      val result = controller.roll()
      result should (be >= 1 and be <= 6)
    }

    "move a figure on the board" in {
      val figs = List(
        Figure(1, Color.Blue, OnBoard(Position(0, 0))),
        Figure(2, Color.Blue, Home),
        Figure(3, Color.Blue, Home),
        Figure(4, Color.Blue, Home)
      )
      val player = Player(1, "Bob", Color.Blue, figs)
      val game = Game(List(player))
      val controller = GameController(game)

      controller.move(1, 3)

      val movedFigure = game.players.head.figureById(1).get
      movedFigure.state shouldBe OnBoard(Position(3, 0))
    }

    "end the turn and move to next player" in {
      val p1 = Player(1, "P1", Color.Red, (1 to 4).map(i => Figure(i, Color.Red, Home)).toList)
      val p2 = Player(2, "P2", Color.Blue, (5 to 8).map(i => Figure(i, Color.Blue, Home)).toList)

      val game = Game(List(p1, p2))
      val controller = GameController(game)

      controller.currentPlayer.name shouldBe "P1"
      controller.endTurn()
      controller.currentPlayer.name shouldBe "P2"
    }

    "detect when the game is over" in {
      val winner = Player(1, "Winner", Color.Green, (1 to 4).map(i => Figure(i, Color.Green, Finished)).toList)
      val loser = Player(2, "Loser", Color.Red, (5 to 8).map(i => Figure(i, Color.Red, Home)).toList)

      val game = Game(List(winner, loser))
      val controller = GameController(game)

      controller.isGameOver shouldBe true
    }
  }

