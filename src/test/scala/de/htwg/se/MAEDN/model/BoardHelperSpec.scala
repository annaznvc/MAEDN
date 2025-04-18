package de.htwg.se.MAEDN.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model._

class BoardHelperSpec extends AnyWordSpec with Matchers:

  "BoardHelper" should {
    "generate a list of fields from the board" in {
      val fields = BoardHelper.generateFields()

      fields should not be empty
      fields.foreach { field =>
        field.fieldType shouldBe FieldType.Board
      }

      // Optional: check dimensions (Board is 11x11 = 121 fields)
      fields.size shouldBe 121
    }
  }
