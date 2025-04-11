package model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class MainSpec extends AnyWordSpec with Matchers:

  "The Main object" should {

    "provide a board with 11 rows" in {
      Main.board.length shouldBe 11
    }

    "have 11 columns in each row" in {
      all(Main.board.map(_.length)) shouldBe 11 //für jede Zeile _ die Länge der Zeile prüfen, also die Anzahl der Spalten
    }

    "contain the center field '**'" in {
      Main.board(5)(5) shouldBe "**"
    }

    /////
    "contain all four player colors" in {
      val flat = Main.board.flatten
      flat should contain allOf ("RR", "BB", "YY", "GG")
    }

    "contain empty fields and path markers" in {
      val flat = Main.board.flatten
      flat should contain ("  ")
      flat should contain ("..")
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
