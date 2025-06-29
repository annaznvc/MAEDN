package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.{Color, Position}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BoardSpec extends AnyWordSpec with Matchers {

  val figureCount = 4

  // Dummy-MoveStrategy für Bewegung auf dem Board
  class DummyMoveStrategy extends MoveStrategy {
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
  class DummyKickStrategy extends MoveStrategy {
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
  class DummyToBoardStrategy extends MoveStrategy {
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
      val movedFigure = Figure(0, redPlayer, 6, figureCount) // ← moved result

      val moveStrategy = new DummyStrategy(moves = List(movedFigure))
      val kickStrategy =
        new DummyStrategy(moves = List(movedFigure.copy(index = 7)))

      val board = Board(
        size = 4,
        moveStrategy = moveStrategy,
        toBoardStrategy = new DummyStrategy(),
        kickFigureStrategy = kickStrategy
      )

      val startFigure = Figure(0, redPlayer, 0, figureCount)

      val result = board.moveFigure(startFigure, List(startFigure), rolled = 6)

      moveStrategy.moveCalled shouldBe true
      kickStrategy.moveCalled shouldBe true
      result.head.index shouldBe 7
    }

    "call moveStrategy.canMove for on-board figure in checkIfMoveIsPossible" in {
      val redPlayer = Player(1, Nil, Color.RED)
      val figure =
        Figure(0, redPlayer, 0, figureCount) // index = 0 → isOnBoard = true

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

  "canFigureMove" should {
    val size = 4
    val figureCount = 4
    val redPlayer = Player(1, Nil, Color.RED)

    "return true when figure is in goal zone and target unoccupied" in {
      // Index 16..19 ist Goal-Bereich bei size=4
      val inGoal = Figure(0, redPlayer, size * 4 + 1, figureCount)
      val figures = List(inGoal)
      val board = Board(
        size,
        new DummyStrategy(),
        new DummyStrategy(),
        new DummyStrategy()
      )
      board.canFigureMove(inGoal, figures, rolled = 1) shouldBe true
    }

    "return false when figure is in goal zone and target occupied by own figure" in {
      val inGoal = Figure(0, redPlayer, size * 4 + 1, figureCount)
      // neuer Index = 17, hier setzen wir einen zweiten eigenen Stein hin
      val blocker = Figure(1, redPlayer, size * 4 + 2, figureCount)
      val figures = List(inGoal, blocker)
      val board = Board(
        size,
        new DummyStrategy(),
        new DummyStrategy(),
        new DummyStrategy()
      )
      board.canFigureMove(inGoal, figures, rolled = 1) shouldBe false
    }

    "return false when figure is in goal zone but newIndex out of goal range" in {
      val inGoal = Figure(0, redPlayer, size * 4 + 1, figureCount)
      // rolled=4 ⇒ newIndex = 16+1+4 = 21 (>= size*4+figureCount = 20)
      val board = Board(
        size,
        new DummyStrategy(),
        new DummyStrategy(),
        new DummyStrategy()
      )
      board.canFigureMove(inGoal, List(inGoal), rolled = 4) shouldBe false
    }

    "invoke toBoardStrategy.canMove for figures not yet on board" in {
      val homeFig = Figure(0, redPlayer, -1, figureCount)
      val toBoardStr = new DummyStrategy(canMoveReturn = true)
      val board =
        Board(size, new DummyStrategy(), toBoardStr, new DummyStrategy())
      board.canFigureMove(homeFig, List(homeFig), rolled = 6) shouldBe true
      toBoardStr.canMoveCalled shouldBe true
    }

    "invoke moveStrategy.canMove for on-board normal moves" in {
      val fig = Figure(0, redPlayer, 2, figureCount) // 2 < size*4
      val moveStr = new DummyStrategy(canMoveReturn = false)
      val board = Board(size, moveStr, new DummyStrategy(), new DummyStrategy())
      board.canFigureMove(fig, List(fig), rolled = 3) shouldBe false
      moveStr.canMoveCalled shouldBe true
    }
  }

  "checkIfMoveIsPossible" should {
    val size = 4
    val figureCount = 4
    val redPlayer = Player(1, Nil, Color.RED)

    "return true if any home-figure can enter board (toBoardStrategy)" in {
      val homeFig = Figure(0, redPlayer, -1, figureCount)
      val toBoardStr = new DummyStrategy(canMoveReturn = true)
      val board =
        Board(size, new DummyStrategy(), toBoardStr, new DummyStrategy())
      val result = board.checkIfMoveIsPossible(
        List(homeFig),
        rolled = 6,
        color = Color.RED
      )
      result shouldBe true
      toBoardStr.canMoveCalled shouldBe true
    }

    "return false if no figure (weder home noch onBoard) can move" in {
      val homeFig = Figure(0, redPlayer, -1, figureCount)
      val toBoardStr = new DummyStrategy(canMoveReturn = false)
      val moveStr = new DummyStrategy(canMoveReturn = false)
      val board = Board(size, moveStr, toBoardStr, new DummyStrategy())
      board.checkIfMoveIsPossible(
        List(homeFig),
        rolled = 6,
        color = Color.RED
      ) shouldBe false
    }
  }

}

// Muss außerhalb der Klasse stehen!
class DummyStrategy(
    val moves: List[Figure] = Nil,
    val canMoveReturn: Boolean = false
) extends MoveStrategy {
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
