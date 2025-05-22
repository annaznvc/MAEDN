package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.{Color, Position}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BoardSpec extends AnyWordSpec with Matchers {

  // Dummy-MoveStrategy für Bewegung auf dem Board
  class DummyMoveStrategy extends IMoveStrategy {
    override def moveFigure(
        figure: Figure,
        figures: List[Figure],
        size: Int,
        rolled: Int
    ): List[Figure] = {
      val moved = figure.copy(index = figure.index + rolled)
      moved :: figures.filterNot(_.id == figure.id)
    }

    override def canMove(
        figure: Figure,
        figures: List[Figure],
        size: Int,
        rolled: Int
    ): Boolean = true
  }

  // Dummy-KickStrategy: kickt Gegner (setzt sie auf -1)
  class DummyKickStrategy extends IMoveStrategy {
    override def moveFigure(
        figure: Figure,
        figures: List[Figure],
        size: Int,
        rolled: Int
    ): List[Figure] = {
      figures.map {
        case f
            if f.owner.color != figure.owner.color &&
              f.adjustedIndex(size) == figure.adjustedIndex(size) =>
          f.copy(index = -1)
        case f => f
      }
    }

    override def canMove(
        figure: Figure,
        figures: List[Figure],
        size: Int,
        rolled: Int
    ): Boolean = true
  }

  // Dummy für toBoard (nicht relevant für diese Tests)
  class DummyToBoardStrategy extends IMoveStrategy {
    override def moveFigure(
        figure: Figure,
        figures: List[Figure],
        size: Int,
        rolled: Int
    ): List[Figure] = figures

    override def canMove(
        figure: Figure,
        figures: List[Figure],
        size: Int,
        rolled: Int
    ): Boolean = false
  }

  "Board" should {
    "use moveStrategy and KickFigureStrategy if figure is on board" in {
      val redPlayer = Player(1, Nil, Color.RED)
      val movedFigure = Figure(0, redPlayer, 6) // ← moved result

      val moveStrategy = new DummyStrategy(moves = List(movedFigure))
      val kickStrategy =
        new DummyStrategy(moves = List(movedFigure.copy(index = 7)))

      val board = Board(
        size = 4,
        moveStrategy = moveStrategy,
        toBoardStrategy = new DummyStrategy(),
        kickFigureStrategy = kickStrategy
      )

      val startFigure = Figure(0, redPlayer, 0)

      val result = board.moveFigure(startFigure, List(startFigure), rolled = 6)

      moveStrategy.moveCalled shouldBe true
      kickStrategy.moveCalled shouldBe true
      result.head.index shouldBe 7
    }

    "call moveStrategy.canMove for on-board figure in checkIfMoveIsPossible" in {
      val redPlayer = Player(1, Nil, Color.RED)
      val figure = Figure(0, redPlayer, 0) // index = 0 → isOnBoard = true

      val moveStrategy = new DummyStrategy(canMoveReturn = true)
      val board = Board(
        size = 4,
        moveStrategy = moveStrategy,
        toBoardStrategy = new DummyStrategy(),
        kickFigureStrategy = new DummyStrategy()
      )

      val result =
        board.checkIfMoveIsPossible(List(figure), rolled = 1, color = Color.RED)

      moveStrategy.canMoveCalled shouldBe true
      result shouldBe true
    }

  }

}

// Muss außerhalb der Klasse stehen!
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
