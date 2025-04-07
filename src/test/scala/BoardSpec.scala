package test

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._

class BoardSpec extends AnyWordSpec with Matchers {

  "A Board" should {

    "contain exactly 40 fields" in {
      val board = new Board
      board.fields.length shouldBe 40
    }

    "initialize all fields to None" in {
      val board = new Board
      all(board.fields) shouldBe None
    }

    "allow setting and getting a figure in a specific field" in {
      val board = new Board
      val figure = Figure(1, Color.Red, Home)
      board.fields(10) = Some(figure)

      board.fields(10) shouldBe Some(figure)
    }

    "still have other fields as None after setting one" in {
      val board = new Board
      val figure = Figure(1, Color.Blue, Home)
      board.fields(5) = Some(figure)

      board.fields(4) shouldBe None
      board.fields(6) shouldBe None
    }
  }
}