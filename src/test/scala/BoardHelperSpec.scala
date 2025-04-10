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

    "match the full expected board layout exactly" in {
      val board = Main.board
      val expectedBoard = Vector(
        Vector("RR", "RR", "  ", "  ", "..", "..", "BB", "  ", "  ", "BB", "BB"),
        Vector("RR", "RR", "  ", "  ", "..", "BB", "..", "  ", "  ", "BB", "BB"),
        Vector("  ", "  ", "  ", "  ", "..", "BB", "..", "  ", "  ", "  ", "  "),
        Vector("  ", "  ", "  ", "  ", "..", "BB", "..", "  ", "  ", "  ", "  "),
        Vector("RR", "..", "..", "..", "..", "BB", "..", "..", "..", "..", ".."),
        Vector("..", "RR", "RR", "RR", "RR", "**", "GG", "GG", "GG", "GG", ".."),
        Vector("..", "..", "..", "..", "..", "YY", "..", "..", "..", "..", "GG"),
        Vector("  ", "  ", "  ", "  ", "..", "YY", "..", "  ", "  ", "  ", "  "),
        Vector("  ", "  ", "  ", "  ", "..", "YY", "..", "  ", "  ", "  ", "  "),
        Vector("YY", "YY", "  ", "  ", "..", "YY", "..", "  ", "  ", "GG", "GG"),
        Vector("YY", "YY", "  ", "  ", "YY", "..", "..", "  ", "  ", "GG", "GG")
      )

      board.length shouldBe 11
      all(board.map(_.length)) shouldBe 11

      for (y <- 0 until 11; x <- 0 until 11) {
        withClue(s"at position ($y,$x): ") {
          board(y)(x) shouldBe expectedBoard(y)(x)
        }
      }
    }

  }
}
