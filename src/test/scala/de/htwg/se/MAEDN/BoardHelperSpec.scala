package test

import model._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class BoardHelperSpec extends AnyWordSpec with Matchers:

  "BoardHelper" should {

    "generate a non-empty list of fields from Main.board" in {
      val fields = BoardHelper.generateFields()
      fields should not be empty
    }

    "generate fields at all '..' positions from Main.board" in {
      val expectedPositions = for
        y <- Main.board.indices
        x <- Main.board(y).indices
        if Main.board(y)(x) == ".."
      yield Position(x, y)

      val actualPositions = BoardHelper.generateFields().map(_.position)
      actualPositions.toSet shouldBe expectedPositions.toSet
    }

    "not generate null or duplicate fields" in {
      val fields = BoardHelper.generateFields()
      all(fields) should not be null
      fields.map(_.position).distinct.size shouldBe fields.size
    }

    "explicitly test board(y).indices as a statement" in {
      val board = Main.board

      for (y <- board.indices) {
        val colIndices = board(y).indices
        colIndices.foreach { x =>
          val value = board(y)(x)
          value should not be null
        }
      }
    }


  }
