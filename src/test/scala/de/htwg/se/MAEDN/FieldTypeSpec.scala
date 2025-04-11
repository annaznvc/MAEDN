package model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class FieldTypeSpec extends AnyWordSpec with Matchers:

  "FieldType" should {
    "have the values Start, Goal, Board, Home" in {
      FieldType.Start.toString shouldBe "Start"
      FieldType.Goal.toString shouldBe "Goal"
      FieldType.Board.toString shouldBe "Board"
      FieldType.Home.toString shouldBe "Home"
    }

    "support pattern matching" in {
      def describe(fieldType: FieldType): String = fieldType match
        case FieldType.Start => "This is the start field."
        case FieldType.Goal  => "This is the goal field."
        case FieldType.Board => "This is a board field."
        case FieldType.Home  => "This is a home field."

      describe(FieldType.Start) shouldBe "This is the start field."
      describe(FieldType.Goal) shouldBe "This is the goal field."
    }
  }
