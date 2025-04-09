package test

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._
import org.scalatest.OptionValues.convertOptionToValuable

class BoardSpec extends AnyWordSpec with Matchers {

  "A Board" should {

    val board = new Board

    "have 40 fields" in {
      board.fields.size shouldBe 40
    }

    "initialize all fields as FieldType.Board" in {
      board.fields.forall(_.fieldType == FieldType.Board) shouldBe true
    }

    "initialize all fields with Position.y = 0" in {
      board.fields.forall(_.position.y == 0) shouldBe true
    }

    "have consecutive x positions from 0 to 39" in {
      board.fields.map(_.position.x) shouldBe (0 to 39).toList
    }

    "return correct field for valid x in getFieldAt" in {
      board.getFieldAt(0).value.position shouldBe Position(0, 0)
      board.getFieldAt(39).value.position shouldBe Position(39, 0)
      board.getFieldAt(10).value.position shouldBe Position(10, 0)
    }

    "return None for invalid x in getFieldAt" in {
      board.getFieldAt(-1) shouldBe None
      board.getFieldAt(40) shouldBe None
    }

    "validate indices correctly" in {
      board.isValidIndex(0) shouldBe true
      board.isValidIndex(39) shouldBe true
      board.isValidIndex(-1) shouldBe false
      board.isValidIndex(40) shouldBe false
    }

    "return all positions via allPositions" in {
      board.allPositions should contain theSameElementsAs (0 to 39).map(Position(_, 0)).toList
    }

    "correctly generate fields from (0 until 40) range" in {
      val expectedFields = (0 until 40).map(i => Field(Position(i, 0), FieldType.Board)).toList
      board.fields should contain theSameElementsAs expectedFields
    }

    // 🆕 Direkter Test der Map-Transformation zur Sicherheit
    "explicitly test the structure of generated fields" in {
      val generated = board.fields
      generated.head shouldBe Field(Position(0, 0), FieldType.Board)
      generated.last shouldBe Field(Position(39, 0), FieldType.Board)
    }
    "generate exactly 40 fields using the correct range" in {
    val board = new Board
    val expectedXPositions = (0 until 40).toList
    val actualXPositions = board.fields.map(_.position.x)
    
    actualXPositions shouldBe expectedXPositions
    }

  }
}
