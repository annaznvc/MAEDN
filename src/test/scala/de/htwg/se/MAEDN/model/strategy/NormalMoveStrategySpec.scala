package de.htwg.se.MAEDN.model.strategy

import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util.Color
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class NormalMoveStrategySpec extends AnyWordSpec with Matchers {

  val strategy = new NormalMoveStrategy
  val boardSize: Int = 8
  val dummyPlayer = Player(1, Nil, Color.RED)
  val figure = Figure(1, dummyPlayer, 0, 4)

  def makePlayerWithFigures(id: Int): Player = {
    val dummy = Player(id, Nil, Color.RED)
    val figs = List.tabulate(4)(i => Figure(i + 1, dummy, i, 4))
    dummy.copy(figures = figs)
  }

  "NormalMoveStrategy" should {

    "move figure if canMove is true" in {
      val f = figure.copy(index = 0)
      val figures = List(f)
      val moved = strategy.moveFigure(f, figures, boardSize, 3)
      moved.exists(_.index == 3) shouldBe true
    }

    "not move figure if canMove is false" in {
      val f = figure.copy(index = boardSize * 4 + 4) // beyond goal
      val figures = List(f)
      val moved = strategy.moveFigure(f, figures, boardSize, 3)
      moved shouldBe figures
    }

    "canMove returns true on free normal field" in {
      val f = figure.copy(index = 0)
      val figures = List(f)
      strategy.canMove(f, figures, boardSize, 3) shouldBe true
    }

    "canMove returns false for Goal field if steps >= figures.size / 4" in {
      val player = makePlayerWithFigures(1)
      val figure = Figure(1, player, index = boardSize * 4 + 4, 4) // Goal(4)
      val figures = player.figures
      strategy.canMove(figure, figures, boardSize, 0) shouldBe false
    }

    "canMove returns false for OffBoard and Home positions" in {
      val fOff = figure.copy(index = boardSize * 4 + 4)
      val fHome = figure.copy(index = -1)
      strategy.canMove(fOff, List(fOff), boardSize, 0) shouldBe false
      strategy.canMove(fHome, List(fHome), boardSize, 0) shouldBe false
    }

    "not modify unrelated figures during moveFigure" in {
      val f1 = figure.copy(index = 0)
      val f2 = Figure(2, dummyPlayer, 5, 4)
      val figures = List(f1, f2)

      val moved = strategy.moveFigure(f1, figures, boardSize, 3)

      moved.find(_.id == f1.id).get.index shouldBe f1.index + 3
      moved.find(_.id == f2.id).get.index shouldBe f2.index
    }

    "canMove for Goal field at exact limit returns false (steps == limit)" in {
      val player = makePlayerWithFigures(1)
      val figure = Figure(1, player, index = boardSize * 4 + 1, 4) // Goal(1)
      val figures = player.figures
      strategy.canMove(figure, figures, boardSize, 0) shouldBe false
    }
  }
}
