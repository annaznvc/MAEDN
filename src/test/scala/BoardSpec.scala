package test

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._

class BoardSpec extends AnyWordSpec with Matchers {

  "A Board" should {

    "contain exactly 40 fields" in {
      val board = new Board
      board.fields.size shouldBe 40
    }

    "initialize all fields with FieldType.Board" in {
      val board = new Board
      all(board.fields.map(_.fieldType)) shouldBe FieldType.Board
    }

    "initialize all fields with Position.y = 0" in {
      val board = new Board
      all(board.fields.map(_.position.y)) shouldBe 0
    }

    "contain fields with x positions from 0 to 39" in {
      val board = new Board
      board.fields.map(_.position.x) shouldBe (0 until 40).toList
    }

    "return a field with getFieldAt for valid index" in {
      val board = new Board
      val fieldOpt = board.getFieldAt(10)
      fieldOpt should not be None
      fieldOpt.get.position shouldBe Position(10, 0)
      fieldOpt.get.fieldType shouldBe FieldType.Board
    }

    "return None from getFieldAt when index is too small or too large" in {
      val board = new Board
      board.getFieldAt(-1) shouldBe None
      board.getFieldAt(100) shouldBe None
    }

    "validate correct indices using isValidIndex" in {
      val board = new Board
      board.isValidIndex(0) shouldBe true
      board.isValidIndex(39) shouldBe true
    }

    "invalidate incorrect indices using isValidIndex" in {
      val board = new Board
      board.isValidIndex(-1) shouldBe false
      board.isValidIndex(40) shouldBe false
    }

    "return all positions with expected coordinates using allPositions" in {
      val board = new Board
      val positions = board.allPositions
      positions.size shouldBe 40
      positions.head shouldBe Position(0, 0)
      positions.last shouldBe Position(39, 0)
      all(positions.map(_.y)) shouldBe 0
    }

    "force generation of fields through generateFields" in {
      val board = new Board
      board.fields.exists(_.position == Position(5, 0)) shouldBe true
    }
  }
}
