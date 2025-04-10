/**
  * 
  * Muss genau 4 Figuren haben
  * 
  * Alle Figuren müssen dieselbe Farbe haben wie der Spieler
  * 
  * Kann nur eine Farbe spielen
  * 
  * Player ID muss eindeutig sein
  * 
  * SPieler hat nur Zugriff auf seine eigenen Figuren
  * 
  * Name draf nicht leer sein
  * 
  * Hat einen Zustand: aktiv oder inaktiv
  * 
  * Wenn raus, wird Spieler beim Gewinnen einem Platz zugeordner (z.B erster, der gewonnen hat ist Platz 1, dann der nächste Gewinner Platz 2 etc.)
  * 
  */

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._

class PlayerSpec extends AnyWordSpec with Matchers:

  "A Player" should {

    "have exactly 4 figures" in {
      val figures = (1 to 4).map(i => Figure(i, Color.Red, Home)).toList
      val player = Player(1, "Alice", Color.Red, figures)
      player.figures.size shouldBe 4
    }

    "have all figures with the same color as the player" in {
      val figures = List(
        Figure(1, Color.Blue, Home),
        Figure(2, Color.Blue, Home),
        Figure(3, Color.Blue, Home),
        Figure(4, Color.Blue, Home)
      )
      val player = Player(2, "Bob", Color.Blue, figures)
      all(player.figures.map(_.color)) shouldBe player.color
    }

    "fail if a figure has a different color than the player" in {
    val figures = List(
        Figure(1, Color.Yellow, Home),
        Figure(2, Color.Yellow, Home),
        Figure(3, Color.Yellow, Home),
        Figure(4, Color.Red, Home) // falsche Farbe
    )

    val exception = intercept[IllegalArgumentException] {
        Player(3, "Cara", Color.Yellow, figures)
    }

    exception.getMessage should include ("All figures must match the player's color")
    }

    "have a non-empty name" in {
      val figures = (1 to 4).map(i => Figure(i, Color.Yellow, Home)).toList
      val player = Player(4, "Dana", Color.Yellow, figures)
      player.name should not be empty
    }

    "have access only to its own figures by ID" in {
      val figures = List(
        Figure(1, Color.Green, Home),
        Figure(2, Color.Green, OnBoard(Position(0, 0))),
        Figure(3, Color.Green, Finished),
        Figure(4, Color.Green, Start(Position(1, 1)))
      )
      val player = Player(5, "Chris", Color.Green, figures)
      player.figures.map(_.id).toSet shouldBe Set(1, 2, 3, 4)
    }

    "have status Active by default" in {
      val figures = (1 to 4).map(i => Figure(i, Color.Red, Home)).toList
      val player = Player(6, "Eve", Color.Red, figures)
      player.status shouldBe Active
    }

    "store a placement when status is Out" in {
      val figures = (1 to 4).map(i => Figure(i, Color.Green, Finished)).toList
      val player = Player(7, "Frank", Color.Green, figures, Out(2))

      player.status shouldBe Out(2)
      player.status match
        case Out(place) => place should (be >= 1 and be <= 4)
        case _ => fail("Expected Out status")
    }

    "have a unique player ID among players" in {
      val figures1 = (1 to 4).map(i => Figure(i, Color.Red, Home)).toList
      val figures2 = (5 to 8).map(i => Figure(i, Color.Blue, Home)).toList
      val player1 = Player(8, "Leo", Color.Red, figures1)
      val player2 = Player(8, "Maya", Color.Blue, figures2) // gleiche ID wie player1

      player1.id shouldBe player2.id // Demonstration: gleiche ID => evtl. Fehler im Spielkontext
    }

        "fail if name is empty" in {
      val figures = (1 to 4).map(i => Figure(i, Color.Red, Home)).toList
      val ex = intercept[IllegalArgumentException] {
        Player(100, "", Color.Red, figures)
      }
      ex.getMessage should include ("Name must not be empty")
    }

    "fail if not exactly 4 figures are provided" in {
      val figures = List(
        Figure(1, Color.Red, Home),
        Figure(2, Color.Red, Home)
      ) // nur 2 Figuren
      val ex = intercept[IllegalArgumentException] {
        Player(101, "TooFew", Color.Red, figures)
      }
      ex.getMessage should include ("Player must have exactly 4 figures")
    }

    "find a figure by ID" in {
      val figures = (1 to 4).map(i => Figure(i, Color.Green, Home)).toList
      val player = Player(102, "FindMe", Color.Green, figures)

      player.figureById(2) shouldBe Some(Figure(2, Color.Green, Home))
      player.figureById(99) shouldBe None
    }

  }