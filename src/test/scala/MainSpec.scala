package test

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.Main

class MainSpec extends AnyWordSpec with Matchers {

  "Das Spielbrett" should {

    "der gesamte Board-Vektor sollte exakt mit der erwarteten Struktur übereinstimmen" in {
    val expected = Vector(
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
  
    Main.board shouldBe expected
  }


    "korrekte Dimensionen haben" in {
      Main.board.size shouldBe 11
      Main.board.foreach(row => row.size shouldBe 11)
    }

    "die Board-Struktur korrekt als Vector von Vectors anlegen" in {
      Main.board shouldBe a [Vector[?]]
      all(Main.board) shouldBe a [Vector[?]]
    }


    "aus Vektoren bestehen" in {
      Main.board shouldBe a [Vector[?]]
      all(Main.board) shouldBe a [Vector[?]]
    }

    "spezifische Zellenwerte korrekt enthalten" when {

      "Ecken überprüft werden" in {
        Main.board(0)(0) shouldBe "RR"     // Obere linke Ecke
        Main.board(10)(10) shouldBe "GG"   // Untere rechte Ecke
      }

      "das Zentrum überprüft wird" in {
        Main.board(5)(5) shouldBe "**"
      }
    }

    "Pfad- und Leerfelder korrekt darstellen" in {
      Main.board(2)(2) shouldBe "  "     // Leeres Feld
      Main.board(4)(1) shouldBe ".."     // Pfad
      Main.board(9)(3) shouldBe "  "     // Leeres Feld
    }

    "nur gültige Zellwerte enthalten" in {
      val allowedPattern = """[A-Z]{2}|\.\.|  |\*\*""".r
      for row <- Main.board do
        for cell <- row do
          cell should fullyMatch regex allowedPattern
    }
  }

  "Die runGame-Methode" should {
    "den korrekten Lade-Status zurückgeben" in {
      Main.runGame() shouldBe "Spielbrett geladen."
    }
  }

  "Die Startpositionen" should {
    "für alle Farben korrekt sein" in {
      // Rote Basis
      Main.board(0)(0) shouldBe "RR"
      Main.board(1)(0) shouldBe "RR"

      // Blaue Basis
      Main.board(0)(9) shouldBe "BB"
      Main.board(1)(5) shouldBe "BB"

      // Gelbe Basis
      Main.board(10)(4) shouldBe "YY"
      Main.board(9)(5) shouldBe "YY"

      // Grüne Basis
      Main.board(6)(10) shouldBe "GG"
      Main.board(10)(10) shouldBe "GG"
    }
  }
}
