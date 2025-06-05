package de.htwg.se.MAEDN.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.util.Color

class IBoardSpec extends AnyWordSpec with Matchers {
  "An IBoard" should {
    val board = IBoard(
      8,
      IMoveStrategy.createNormalMoveStrategy(),
      IMoveStrategy.createToBoardStrategy(),
      IMoveStrategy.createKickFigureStrategy()
    )

    "have a size and strategies" in {
      board.size shouldBe 8
      board.moveStrategy should not be null
      board.toBoardStrategy should not be null
      board.kickFigureStrategy should not be null
    }

    "implement moveFigure, checkIfMoveIsPossible and canFigureMove" in {
      // Dummy-Figuren und Spieler
      val player = IPlayer(1, Nil, Color.RED)
      val figure = IFigure(1, player, -1, 4)
      val figures = List(figure)
      noException should be thrownBy board.moveFigure(figure, figures, 6)
      noException should be thrownBy board.checkIfMoveIsPossible(
        figures,
        6,
        Color.RED
      )
      noException should be thrownBy board.canFigureMove(figure, figures, 6)
    }
  }
}
