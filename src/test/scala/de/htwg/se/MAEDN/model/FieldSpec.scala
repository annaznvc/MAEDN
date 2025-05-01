package de.htwg.se.MAEDN.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.util.*

class FieldSpec extends AnyWordSpec with Matchers {

  "A Field" should {

        "report whether it has a figure" in {
      val player = Player("Anna", Nil, Color.Red)
      val figure = Figure(1, player)

      val fieldWithFigure = Field(Some(figure), FieldType.Normal, Color.Red)
      val emptyField = Field(None, FieldType.Normal, Color.Red)

      fieldWithFigure.hasFigure shouldBe true
      emptyField.hasFigure shouldBe false
    }

    "support equality between fields" in {
    val field1 = Field(None, FieldType.Normal, Color.Red)
    val field2 = Field(None, FieldType.Normal, Color.Red)

    field1 shouldEqual field2
    }


  }
}
