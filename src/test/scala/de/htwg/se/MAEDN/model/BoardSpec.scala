import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model.*
import de.htwg.se.MAEDN.util.*

class BoardSpec extends AnyWordSpec with Matchers {

  "A Board" should {

    val board = Board.defaultBoard(normalFieldCount = 4, figureCountPerPlayer = 2)

    "have the correct size" in {
      // 4 Farben × (1 Start + 4 Normal + 2 Goal) = 28
      board.toString should include ("Board:")
    }

    "return Some(Field) when getField is called with a valid index" in {
      val field = board.getField(1)
      field should not be empty
      field.get shouldBe a [Field]
    }

    "return None when getField is called with an invalid index" in {
      board.getField(0) shouldBe None
      board.getField(999) shouldBe None
    }

    "update a field with a figure" in {
      val player = Player("Test", Nil, Color.Red)
      val figure = Figure(0, player)

      val updatedBoard = board.updateField(1, Some(figure))
      updatedBoard.getField(1).get.figure shouldBe Some(figure)
    }

    "detect if a field is moveable (empty and not a Goal)" in {
      val normalIndex = board.fields.indexWhere(f => f.fieldType == FieldType.Normal)
      normalIndex should be >= 0
      val canMove = board.canMoveTo(normalIndex + 1)
      canMove shouldBe true
    }

    "prevent movement to a goal field" in {
      val goalIndex = board.fields.indexWhere(f => f.fieldType == FieldType.Goal)
      goalIndex should be >= 0
      val canMove = board.canMoveTo(goalIndex + 1)
      canMove shouldBe false
    }

    "generate a board with exactly the expected number of fields" in {
      val board = Board.defaultBoard(4, 2)
      // Diese Zeile sorgt dafür, dass die `assert(fieldsList.size == totalFields)` aufgerufen wird
      board.fields.length shouldBe (4 * 4 + 4 + 4 * 2) // = 28
    }

    "provide a string representation via boardState" in {
      val board = Board.defaultBoard(4, 2)
      val state = board.boardState
      state should include ("Field") // oder prüfe auf konkreten Inhalt
    }


    "throw an error if the number of fields is incorrect" in {
        val exception = intercept[IllegalArgumentException] {
            // 1 Figur pro Spieler ist erlaubt, aber es wird zu wenig erzeugt
            // Wir tricksen: wir rufen generateFields direkt auf mit falschen Parametern
            val fields = Board.generateFields(3, 1, 1) // 3 Felder ist viel zu wenig
        }
        exception.getMessage should include ("Expected")
    }



    

  }
}
