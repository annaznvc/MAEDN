package model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.Main.*

class BoardSpec extends AnyWordSpec with Matchers:

  "The predefined board" should {
    "have 11 rows" in {
      board.length shouldBe 11
    }

    "have 11 columns in each row" in {
      all(board.map(_.length)) shouldBe 11
    }

    "contain the center field '**'" in {
      board.flatten should contain ("**")
    }

    "contain all four player colors" in {
      val flat = board.flatten
      flat should contain allOf ("RR", "BB", "YY", "GG")
    }

    "contain empty fields and path markers" in {
      val flat = board.flatten
      flat should contain ("  ")
      flat should contain ("..")
    }
  }

  "runGame" should {
    "return the start confirmation message" in {
      runGame() shouldBe "Spielbrett geladen."
    }

      "all cells in the board" should {
    "match allowed field patterns" in {
      for row <- board do
        for cell <- row do
          cell should fullyMatch regex """[A-Z]{2}|\.\.|  |\*\*"""
    }
  }

  }
