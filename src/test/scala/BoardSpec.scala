package test

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._
import org.scalatest.OptionValues._

class BoardSpec extends AnyWordSpec with Matchers {

  "A Board" should {

    val board = new Board

    "have 40 fields (8x5)" in {
      board.fields.size shouldBe 40
    }

    "cover all x positions from 0 to 7" in {
      board.fields.map(_.position.x).distinct.sorted shouldBe (0 to 7).toList
    }

    "cover all y positions from 0 to 4" in {
      board.fields.map(_.position.y).distinct.sorted shouldBe (0 to 4).toList
    }

    "have every field with FieldType.Board" in {
      board.fields.map(_.fieldType).distinct shouldBe List(FieldType.Board)
    }

    "return correct field for valid coordinates in getFieldAt" in {
      board.getFieldAt(0, 0).value.position shouldBe Position(0, 0)
      board.getFieldAt(7, 4).value.position shouldBe Position(7, 4)
      board.getFieldAt(3, 2).value.position shouldBe Position(3, 2)
    }

    "return None for invalid coordinates in getFieldAt" in {
      board.getFieldAt(-1, 0) shouldBe None
      board.getFieldAt(0, -1) shouldBe None
      board.getFieldAt(8, 0) shouldBe None
      board.getFieldAt(0, 5) shouldBe None
    }

    "validate indices correctly" in {
      board.isValidIndex(0, 0) shouldBe true
      board.isValidIndex(7, 4) shouldBe true
      board.isValidIndex(-1, 0) shouldBe false
      board.isValidIndex(0, 5) shouldBe false
    }

    "generate correct field at each (x, y) coordinate using until" in {
      val board = new Board

      for (x <- 0 until board.width; y <- 0 until board.height) {
        withClue(s"Missing field at ($x, $y): ") {
          val fieldOpt = board.getFieldAt(x, y)
          fieldOpt.isDefined shouldBe true
          fieldOpt.get.position shouldBe Position(x, y)
          fieldOpt.get.fieldType shouldBe FieldType.Board
        }
      }
    }
    
    "return all positions via allPositions" in {
      val expected = for {
        x <- 0 to 7
        y <- 0 to 4
      } yield Position(x, y)
      board.allPositions should contain theSameElementsAs expected
    }
  }
}
