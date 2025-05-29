package de.htwg.se.MAEDN.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.util.{Color, Position}

class FigureSpec extends AnyWordSpec with Matchers {

  val boardSize = 4
  val figureCount = 4

  val owner = {
    val dummy = Player(1, Nil, Color.RED)
    val f = Figure(99, dummy, 0, figureCount)
    dummy.copy(figures = List(f))
  }

  "A Figure" should {

    "calculate adjustedIndex correctly" in {
      Figure(0, owner, -1, figureCount).adjustedIndex(
        boardSize
      ) shouldBe Position.Home(0)
      Figure(0, owner, 16, figureCount).adjustedIndex(
        boardSize
      ) shouldBe Position.Goal(0)
      Figure(0, owner, 21, figureCount).adjustedIndex(
        boardSize
      ) shouldBe Position.OffBoard(0)
      Figure(0, owner, 5, figureCount).adjustedIndex(
        boardSize
      ) shouldBe Position.Normal(
        (5 + owner.color.offset * boardSize) % (boardSize * 4)
      )
    }

    "calculate newAdjustedIndex correctly" in {
      Figure(0, owner, -1, figureCount).newAdjustedIndex(
        boardSize,
        1
      ) shouldBe Position.Home(0)
      Figure(0, owner, 17, figureCount).newAdjustedIndex(
        boardSize,
        10
      ) shouldBe Position.OffBoard(0)
      Figure(0, owner, 15, figureCount).newAdjustedIndex(
        boardSize,
        1
      ) shouldBe Position.Goal(0)
      Figure(0, owner, 5, figureCount).newAdjustedIndex(
        boardSize,
        2
      ) shouldBe Position.Normal(
        ((7 + owner.color.offset * boardSize) % (boardSize * 4))
      )
    }

    "identify board status correctly" in {
      Figure(0, owner, -1, figureCount).isOnBoard shouldBe false
      Figure(0, owner, 0, figureCount).isOnBoard shouldBe true
      Figure(0, owner, 0, figureCount).isOnStart shouldBe true
      Figure(0, owner, 1, figureCount).isOnStart shouldBe false
      Figure(0, owner, 16, figureCount).isOnGoal(boardSize) shouldBe true
      Figure(0, owner, 10, figureCount).isOnGoal(boardSize) shouldBe false
    }

    "detect collision correctly" in {
      val f1 = Figure(0, Player(1, Nil, Color.RED), 5, figureCount)
      val f2 = Figure(1, Player(1, Nil, Color.RED), 5, figureCount)
      val f3 = Figure(2, Player(2, Nil, Color.BLUE), 1, figureCount)

      f1.checkForCollision(f1, boardSize) shouldBe Collision.NoCollision
      f1.checkForCollision(f2, boardSize) shouldBe Collision.OwnCollision
      f1.checkForCollision(f3, boardSize) shouldBe Collision.EnemyCollision
      f1.checkForCollision(
        f3.copy(index = 2),
        boardSize
      ) shouldBe Collision.NoCollision
    }

    "detect possible collision correctly" in {
      val f1 = Figure(0, Player(1, Nil, Color.RED), 5, figureCount)
      val f2 = Figure(1, Player(1, Nil, Color.RED), 5, figureCount)
      val f3 = Figure(2, Player(2, Nil, Color.BLUE), 1, figureCount)

      val target = f1.adjustedIndex(boardSize)

      f1.checkForPossibleCollision(
        f1,
        boardSize,
        target
      ) shouldBe Collision.NoCollision
      f1.checkForPossibleCollision(
        f2,
        boardSize,
        target
      ) shouldBe Collision.OwnCollision
      f1.checkForPossibleCollision(
        f3,
        boardSize,
        target
      ) shouldBe Collision.EnemyCollision
    }

  }
}
