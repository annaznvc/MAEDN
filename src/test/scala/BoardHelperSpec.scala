package test

import model._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class BoardHelperSpec extends AnyWordSpec with Matchers {

  "BoardHelper" should {

    "generate exactly 40 fields using (0 until 40).toList" in {
      val fields = BoardHelper.generateFields()
      fields.size shouldBe 40
    }

    "generate fields with correct structure per element to satisfy coverage" in {
      val fields = BoardHelper.generateFields()
      for (i <- 0 until 40) {
        val field = fields(i)
        field.position.x shouldBe i
        field.position.y shouldBe 0
        field.fieldType shouldBe FieldType.Board
      }
    }
  }
}
