package de.htwg.se.MAEDN.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.util.Position
import de.htwg.se.MAEDN.model.IMoveStrategy

class IMoveStrategySpec extends AnyWordSpec with Matchers {
  "An IMoveStrategy" should {
    val strategy = IMoveStrategy.createNormalMoveStrategy()
    val player = IPlayer(1, Nil, Color.RED)
    val figure = IFigure(1, player, 0, 4)
    val figures = List(figure)

    "implement moveFigure and canMove" in {
      noException should be thrownBy strategy.moveFigure(figure, figures, 8, 6)
      noException should be thrownBy strategy.canMove(figure, figures, 8, 6)
    }

    "implement isOnGoal, collidingFigure, isOnLastPossibleField" in {
      strategy.isOnGoal(figure, 1, 8) shouldBe false
      strategy.collidingFigure(figure, figures, 8) shouldBe None
      strategy.isOnLastPossibleField(figure, figures, 1, 8) shouldBe false
    }

    // Zusätzliche Test-Abschnitte für IMoveStrategySpec

    "test isOnLastPossibleField when figure is on last field index" in {
      val size = 8
      val lastFieldIndex = size * 4 - 1 // 31
      val figureOnLastField = IFigure(1, player, lastFieldIndex, 4)

      strategy.isOnLastPossibleField(
        figureOnLastField,
        List(figureOnLastField),
        1,
        size
      ) shouldBe true
    }

    "test isOnLastPossibleField when figure is on goal but goal has gaps" in {
      val size = 8
      val goalCount = 4
      val goalStartIndex = size * 4 - size // 24

      // Erstelle Figuren mit Lücken im Ziel
      val figure1 = IFigure(1, player, goalStartIndex, 4) // index 24
      val figure3 =
        IFigure(3, player, goalStartIndex + 2, 4) // index 26 (Lücke bei 25)
      val testFigure = IFigure(4, player, goalStartIndex + 3, 4) // index 27

      val allFigures = List(figure1, figure3, testFigure)

      strategy.isOnLastPossibleField(
        testFigure,
        allFigures,
        goalCount,
        size
      ) shouldBe false
    }

    "test isOnLastPossibleField when figure is on goal with different colored figures" in {
      val size = 8
      val goalCount = 4
      val goalStartIndex = size * 4 - size // 24
      val bluePlayer = IPlayer(2, Nil, Color.BLUE)

      // Erstelle Figuren verschiedener Farben
      val redFigure1 = IFigure(1, player, goalStartIndex, 4) // Rot, index 24
      val blueFigure =
        IFigure(2, bluePlayer, goalStartIndex + 1, 4) // Blau, index 25
      val testFigure =
        IFigure(3, player, goalStartIndex + 2, 4) // Rot, index 26

      val allFigures = List(redFigure1, blueFigure, testFigure)

      // Nur rote Figuren sollten für die Ziel-Logik berücksichtigt werden
      strategy.isOnLastPossibleField(
        testFigure,
        allFigures,
        goalCount,
        size
      ) shouldBe false
    }

    "test collidingFigure when there is a collision" in {
      val size = 8
      val figure1 = IFigure(1, player, 5, 4)
      val figure2 = IFigure(2, player, 5, 4) // Gleiche Position
      val figures = List(figure1, figure2)

      strategy.collidingFigure(figure1, figures, size) shouldBe Some(figure2)
    }

    "test isOnGoal when figure is in goal area" in {
      val size = 8
      val goalCount = 4
      val goalStartIndex = size * 4 - goalCount // 28
      val figureInGoal = IFigure(1, player, goalStartIndex, 4)

      strategy.isOnGoal(figureInGoal, goalCount, size) shouldBe true
    }

    "test factory method apply()" in {
      val strategy = IMoveStrategy.apply()
      strategy shouldBe a[IMoveStrategy]
    }

    "test all factory methods" in {
      val kickStrategy = IMoveStrategy.createKickFigureStrategy()
      val boardStrategy = IMoveStrategy.createToBoardStrategy()
      val normalStrategy = IMoveStrategy.createNormalMoveStrategy()

      kickStrategy shouldBe a[IMoveStrategy]
      boardStrategy shouldBe a[IMoveStrategy]
      normalStrategy shouldBe a[IMoveStrategy]
    }

    "test isOnLastPossibleField when figure is on goal and goal is properly filled" in {
      val size = 8
      val goalCount = 4
      val goalStartIndex = size * 4 - goalCount // 28

      // Erstelle Figuren die aufeinanderfolgend im Ziel stehen
      // Wichtig: figureCount muss 4 sein, damit isOnGoal() true zurückgibt
      val figure1 = IFigure(1, player, size * 4, 4) // index 32 (im Ziel)
      val figure2 = IFigure(2, player, size * 4 + 1, 4) // index 33 (im Ziel)
      val testFigure = IFigure(3, player, size * 4 + 2, 4) // index 34 (im Ziel)

      val allFigures = List(figure1, figure2, testFigure)

      // goalStartIndex = 28, aber die Figuren sind bei 32, 33, 34
      // goalFigures.map(_.index) = [32, 33] (ohne testFigure)
      // goalIndices.zipWithIndex.forall: (32, 0) -> 32 == 28 + 0? false
      // Daher sollte es false sein
      strategy.isOnLastPossibleField(
        testFigure,
        allFigures,
        goalCount,
        size
      ) shouldBe false
    }

  }
}
