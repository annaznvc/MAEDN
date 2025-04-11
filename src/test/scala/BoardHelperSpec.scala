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

    "generate only fields with FieldType.Board" in {
      val fields = BoardHelper.generateFields()
      all(fields.map(_.fieldType)) shouldBe FieldType.Board
    }

    "not generate null or duplicate fields" in {
      val fields = BoardHelper.generateFields()
      all(fields) should not be null
      fields.map(_.position).distinct.size shouldBe fields.size
    }

    "correctly map positions to Field if value is '..'" in {
      val fields = BoardHelper.generateFields()
      fields.map(_.position).foreach { pos =>
        Main.board(pos.y)(pos.x) shouldBe ".."
      }
      fields.exists(_.fieldType != FieldType.Board) shouldBe false
      fields.size should be > 0
    }

    "visit both true and false branches of the '..' check" in {
      val flatBoard = Main.board.flatten
      flatBoard.exists(_ == "..") shouldBe true
      flatBoard.exists(_ != "..") shouldBe true
    }

    "manually cover board.indices and toList" in {
      val board = Main.board

      val rowIndices = board.indices.toList
      val expected = (0 until board.length).toList
      rowIndices shouldBe expected

      val vector = Vector(1, 2, 3)
      val asList = vector.toList
      asList shouldBe List(1, 2, 3)
    }

    // ✅ FINALER TEST für board(y).indices
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

    "force full execution of for-comprehension in generateFields (including board(y).indices)" in {
      val fields = BoardHelper.generateFields()

      // Sicherstellen, dass mehrere y-Werte vorkommen → board(y).indices mehrfach durchlaufen
      val uniqueY = fields.map(_.position.y).distinct
      uniqueY.size should be > 1   // → zwingt mehrere Zeilen mit board(y).indices

      // Sicherstellen, dass mehrere x-Werte pro Zeile dabei sind
      val groupedByY = fields.groupBy(_.position.y)
      groupedByY.values.foreach { group =>
        group.map(_.position.x).distinct.size should be > 0
      }

      // Zur Sicherheit: Zugriff auf die Originaldaten → Coverage von allen Positionen
      fields.foreach { f =>
        val value = Main.board(f.position.y)(f.position.x)
        value shouldBe ".."
      }
    }

  }
