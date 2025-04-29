package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.controller.GameController
import de.htwg.se.MAEDN.util.FieldType
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameControllerSpec extends AnyWordSpec with Matchers {

  "A GameController" should {

    "allow single-figure test game" in {
      val controller = new GameController(List("Test1", "Test2"), boardSize = 20, figuresPerPlayer = 2)
      val player = controller.currentPlayer
      val figure = player.figures.head

      // Test-Situation setzen: direkt ins Goal setzen
      val goalField = controller.boardFields.find(f =>
        f.fieldType == FieldType.Goal && f.ownerColor.contains(player.color)
      ).get

      goalField.occupiedBy = Some(figure)
      figure.position = Some(goalField)

      figure.isFinished shouldBe false

      // Simuliere Finish
      figure.isFinished = true

      figure.isFinished shouldBe true
    }

  }

}
