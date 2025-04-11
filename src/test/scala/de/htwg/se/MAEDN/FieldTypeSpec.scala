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

  }
