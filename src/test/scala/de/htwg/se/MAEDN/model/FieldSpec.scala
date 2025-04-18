package de.htwg.se.MAEDN.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.controller._
import de.htwg.se.MAEDN.util._

class FieldSpec extends AnyWordSpec with Matchers {

  "A Field" should {

    "store the correct position and field type" in {
      val pos = Position(2, 5)
      val field = Field(pos, FieldType.Start)

      field.position shouldBe pos
      field.fieldType shouldBe FieldType.Start
    }

    "be equal to another field with same values" in {
      val f1 = Field(Position(1, 1), FieldType.Goal)
      val f2 = Field(Position(1, 1), FieldType.Goal)

      f1 shouldBe f2
    }

    "not be equal if position differs" in {
      val f1 = Field(Position(1, 1), FieldType.Board)
      val f2 = Field(Position(2, 1), FieldType.Board)

      f1 should not be f2
    }

  }
}
