package test

import model._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class BoardHelperSpec extends AnyWordSpec with Matchers {

  "BoardHelper" should {

    "generate exactly 40 fields using (0 until 40).toList" in {
      val fields = BoardHelper.generateFields()
      fields.size shouldBe 40
      fields.map(_.position.x) shouldBe (0 until 40).toList
    }

    "generate all fields with y = 0 and type = Board" in {
      val fields = BoardHelper.generateFields()
      all (fields.map(_.position.y)) shouldBe 0
      all (fields.map(_.fieldType)) shouldBe FieldType.Board
    }
  }
}
