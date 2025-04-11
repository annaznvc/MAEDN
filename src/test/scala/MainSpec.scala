package model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class MainSpec extends AnyWordSpec with Matchers:

  "The Main object" should {

    "provide a board with 11 rows" in {
      Main.board.length shouldBe 11
    }

    "have 11 columns in each row" in {
      all(Main.board.map(_.length)) shouldBe 11
    }

    "contain the center field '**'" in {
      Main.board(5)(5) shouldBe "**"
    }

    "contain all four player colors" in {
      val flat = Main.board.flatten
      flat should contain allOf ("RR", "BB", "YY", "GG")
    }

    "contain empty fields and path markers" in {
      val flat = Main.board.flatten
      flat should contain ("  ")
      flat should contain ("..")
    }

    "only contain allowed field patterns" in {
      Main.board.flatten.foreach { cell =>
        cell should fullyMatch regex """[A-Z]{2}|\.\.|  |\*\*"""
      }
    }

    "explicitly evaluate every board cell" in {
      for (i <- Main.board.indices; j <- Main.board(i).indices) {
        val cell = Main.board(i)(j)
        cell.length should (be >= 1 and be <= 2)
      }
    }

    "consist of exactly 11 Vector rows" in {
      val board = Main.board

      board(0).isInstanceOf[Vector[String]] shouldBe true
      board(1).isInstanceOf[Vector[String]] shouldBe true
      board(2).isInstanceOf[Vector[String]] shouldBe true
      board(3).isInstanceOf[Vector[String]] shouldBe true
      board(4).isInstanceOf[Vector[String]] shouldBe true
      board(5).isInstanceOf[Vector[String]] shouldBe true
      board(6).isInstanceOf[Vector[String]] shouldBe true
      board(7).isInstanceOf[Vector[String]] shouldBe true
      board(8).isInstanceOf[Vector[String]] shouldBe true
      board(9).isInstanceOf[Vector[String]] shouldBe true
      board(10).isInstanceOf[Vector[String]] shouldBe true
    }

    "return the game start message" in {
      Main.runGame() shouldBe "Spielbrett geladen."
    }

    "explicitly evaluate all Vector(...) constructor calls in Main.board" in {
  // Zugriff auf jede einzelne Zeile, damit Vector(...) ausgeführt wird
  val rows = Main.board

  rows.foreach { row =>
    row.foreach { cell =>
      cell.length should (be >= 1 and be <= 2)
    }
  }
}

  }
