package de.htwg.se.MAEDN.model.BoardImp

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.model.BoardImp.Board
import de.htwg.se.MAEDN.model.IMoveStrategy
import de.htwg.se.MAEDN.model.IFigure
import de.htwg.se.MAEDN.model.IPlayer

class BoardSpec extends AnyWordSpec with Matchers {
  // Dummy-Strategien
  object DummyStrategy extends IMoveStrategy {
    def moveFigure(
        figure: IFigure,
        figures: List[IFigure],
        size: Int,
        rolled: Int
    ): List[IFigure] = figures
    def canMove(
        figure: IFigure,
        figures: List[IFigure],
        size: Int,
        rolled: Int
    ): Boolean = true
  }

  val board = Board(
    size = 8,
    moveStrategy = DummyStrategy,
    toBoardStrategy = DummyStrategy,
    kickFigureStrategy = DummyStrategy
  )

  val player = IPlayer(1, Nil, Color.RED)
  val figure = IFigure(1, player, 0, 4)
  val figures = List(figure)

  "A Board" should {
    "move a figure" in {
      board.moveFigure(figure, figures, 6) shouldBe figures
    }

    "check if move is possible for a color" in {
      board.checkIfMoveIsPossible(figures, 6, Color.RED) shouldBe true
    }

    "check if a specific figure can move" in {
      board.canFigureMove(figure, figures, 6) shouldBe true
    }

  }

  "check if figure can move in goal area" should {

    "allow movement within goal area when target is free" in {
      val figureInGoal = IFigure(1, player, 32, 4) // index = size*4 = 32
      val rolled = 2
      val newIndex = 34 // 32 + 2, innerhalb des Zielbereichs (32-35)

      board.canFigureMove(
        figureInGoal,
        List(figureInGoal),
        rolled
      ) shouldBe true
    }

    "prevent movement within goal area when target is occupied by own figure" in {
      val figureInGoal1 = IFigure(1, player, 32, 4) // bei Index 32
      val figureInGoal2 =
        IFigure(2, player, 34, 4) // bei Index 34 (Zielposition)
      val figures = List(figureInGoal1, figureInGoal2)
      val rolled = 2 // würde zu Index 34 führen

      board.canFigureMove(figureInGoal1, figures, rolled) shouldBe false
    }

    "prevent movement when new position would exceed goal area" in {
      val figureInGoal =
        IFigure(1, player, 35, 4) // letzter gültiger Index im Zielbereich
      val rolled = 1 // würde zu Index 36 führen (außerhalb 32-35)

      board.canFigureMove(
        figureInGoal,
        List(figureInGoal),
        rolled
      ) shouldBe false
    }
  }
}
