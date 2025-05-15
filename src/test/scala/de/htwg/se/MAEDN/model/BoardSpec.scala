package de.htwg.se.MAEDN.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.model.IMoveStrategy

class BoardSpec extends AnyWordSpec with Matchers {

  "A Board" should {

    "use toBoardStrategy and KickFigureStrategy if figure is not on board" in {
      val toBoard = new DummyStrategy(List(Figure(0, DummyPlayer, 0)))
      val kicker = new DummyStrategy(List(Figure(0, DummyPlayer, 1)))
      val board = Board(4, moveStrategy = new DummyStrategy(), toBoard, kicker)

      val result = board.moveFigure(Figure(0, DummyPlayer, -1), Nil, 6)

      toBoard.moveCalled shouldBe true
      kicker.moveCalled shouldBe true
      result.head.index.shouldBe(1)
    }

    "use moveStrategy and KickFigureStrategy if figure is on board" in {
      val move = new DummyStrategy(List(Figure(0, DummyPlayer, 5)))
      val kicker = new DummyStrategy(List(Figure(0, DummyPlayer, 6)))
      val board = Board(4, move, new DummyStrategy(), kicker)

      val result = board.moveFigure(Figure(0, DummyPlayer, 0), Nil, 3)

      move.moveCalled shouldBe true
      kicker.moveCalled shouldBe true
      result.head.index.shouldBe(6)
    }

    "use toBoardStrategy in checkIfMoveIsPossible if figure is not on board" in {
      val toBoard = new DummyStrategy(canMoveReturn = true)
      val board = Board(4, new DummyStrategy(), toBoard, new DummyStrategy())

      val result = board.checkIfMoveIsPossible(
        List(Figure(0, DummyPlayer, -1)),
        6,
        DummyPlayer.color
      )

      toBoard.canMoveCalled shouldBe true
      result.shouldBe(true)
    }

    "use moveStrategy in checkIfMoveIsPossible if figure is on board" in {
      val move = new DummyStrategy(canMoveReturn = true)
      val board = Board(4, move, new DummyStrategy(), new DummyStrategy())

      val result = board.checkIfMoveIsPossible(
        List(Figure(0, DummyPlayer, 0)),
        4,
        DummyPlayer.color
      )

      move.canMoveCalled shouldBe true
      result.shouldBe(true)
    }

    "return false if no figure can move" in {
      val board = Board(
        4,
        new DummyStrategy(canMoveReturn = false),
        new DummyStrategy(canMoveReturn = false),
        new DummyStrategy()
      )

      val result = board.checkIfMoveIsPossible(
        List(Figure(0, DummyPlayer, -1)),
        4,
        DummyPlayer.color
      )

      result.shouldBe(false)
    }

      "correctly detect if a figure is on a goal field" in {
        val size = 4       // boardSize = 4 → 4 * 4 = 16 Felder
        val goalCount = 4  // z. B. 4 Zielfelder
        val threshold = size * 4 - goalCount // → 12

        val player = DummyPlayer

        val figureInGoal = Figure(0, player, index = 13)
        val figureNotInGoal = Figure(1, player, index = 10)

        Board.isOnGoal(figureInGoal, goalCount, size) shouldBe true
        Board.isOnGoal(figureNotInGoal, goalCount, size) shouldBe false
    }







  }

  val DummyPlayer: Player = Player(1, Nil, Color.RED)
}

// ✅ DummyStrategy muss außerhalb der Klasse stehen!
class DummyStrategy(
    val moves: List[Figure] = Nil,
    val canMoveReturn: Boolean = false
) extends IMoveStrategy {
  var moveCalled = false
  var canMoveCalled = false

  override def moveFigure(
      figure: Figure,
      figures: List[Figure],
      size: Int,
      rolled: Int
  ): List[Figure] = {
    moveCalled = true
    moves
  }

  override def canMove(
      figure: Figure,
      figures: List[Figure],
      size: Int,
      rolled: Int
  ): Boolean = {
    canMoveCalled = true
    canMoveReturn
  }
}
