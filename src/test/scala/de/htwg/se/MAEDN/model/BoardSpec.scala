import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model.*
import de.htwg.se.MAEDN.util.Color

class BoardSpec extends AnyWordSpec with Matchers {

  val player = Player(1, List.empty, Color.RED)
  val figure = Figure(1, player)

  "A Board" should {

    "return a field or None for getField" in {
      val board = BoardFactory().build()
      board.getField(1).isDefined shouldBe true
      board.getField(0) shouldBe None
      board.getField(board.fields.length + 1) shouldBe None
    }

    "return a home field or None for getHomeField" in {
      val board = BoardFactory().withPlayers(List(player)).build()
      board.getHomeField(1).isDefined shouldBe true
      board.getHomeField(0) shouldBe None
      board.getHomeField(board.homeFields.length + 1) shouldBe None
    }

    "update a field with a figure" in {
      val board = BoardFactory().build()
      val updated = board.updateField(1, Some(figure))
      updated.getField(1).flatMap(_.figure) shouldBe Some(figure)
    }

    "update a home field with a figure" in {
      val board = BoardFactory().withPlayers(List(player)).build()
      val updated = board.updateHomeField(1, Some(figure))
      updated.getHomeField(1).flatMap(_.figure) shouldBe Some(figure)
    }

    "check if a move to a field is allowed" in {
      val board = BoardFactory().build()
      val idx = board.fields.indexWhere(_.fieldType == FieldType.Normal)
      board.canMoveTo(idx + 1) shouldBe true

      val blockedBoard = board.updateField(idx + 1, Some(figure))
      blockedBoard.canMoveTo(idx + 1) shouldBe false
    }

    "move a figure from home to start if rolled 6 and start is free" in {
      val p = Player(1, List(Figure(1, null)), Color.RED)
      val f = p.figures.head.copy(owner = p)
      val factory = BoardFactory().withPlayers(List(p))
      val board = factory.build()
      val startIdx = board.fields.indexWhere(f => f.fieldType == FieldType.Start && f.color == p.color)
      val homeIdx = board.homeFields.indexWhere(_.figure.exists(_.owner == p))

      val boardWithFigureAtHome = board.updateHomeField(homeIdx + 1, Some(f))
      val moved = boardWithFigureAtHome.moveFigure(f, 6)

      moved.fields(startIdx).figure shouldBe Some(f)
      moved.homeFields(homeIdx).figure shouldBe None
    }

    "not move a figure from home if rolled not 6" in {
      val p = Player(1, List(Figure(1, null)), Color.RED)
      val f = p.figures.head.copy(owner = p)
      val factory = BoardFactory().withPlayers(List(p))
      val board = factory.build()
      val homeIdx = board.homeFields.indexWhere(_.figure.exists(_.owner == p))

      val boardWithFigureAtHome = board.updateHomeField(homeIdx + 1, Some(f))
      val moved = boardWithFigureAtHome.moveFigure(f, 4)

      moved shouldBe boardWithFigureAtHome // keine Änderung
    }

    "not move to a field with same color figure" in {
      val p = Player(1, List(Figure(1, null)), Color.RED)
      val f = p.figures.head.copy(owner = p)
      val factory = BoardFactory().withPlayers(List(p))
      val board = factory.build()

      val fromIdx = board.fields.indexWhere(f => f.fieldType == FieldType.Start && f.color == p.color)
      val toIdx = (fromIdx + 1) % board.fields.size

      val occupied = board.updateField(toIdx + 1, Some(f))
      val placed = occupied.updateField(fromIdx + 1, Some(f))

      val moved = placed.moveFigure(f, 1)
      moved shouldBe placed // nicht bewegt
    }

    "render to string without exceptions" in {
      val board = BoardFactory().build()
      noException should be thrownBy board.toString
    }

    "leave all other home fields untouched when updating one" in {
      val p = Player(1, Nil, Color.RED)
      val f1 = Figure(1, p)
      val f2 = Figure(2, p)
      val pWithFigures = p.copy(figures = List(f1, f2))
      val board = BoardFactory().withFigureCount(2).withPlayers(List(pWithFigures)).build()

      // Setze beide Figuren manuell auf Home-Feld 1 und 2
      val boardWithHomes = board
        .updateHomeField(1, Some(f1))
        .updateHomeField(2, Some(f2))

      // Aktualisiere nur Home-Feld 1
      val updated = boardWithHomes.updateHomeField(1, None)

      // Home-Feld 1 ist leer
      updated.getHomeField(1).flatMap(_.figure) shouldBe None

      // Home-Feld 2 ist unverändert → das triggert Zeile 31
      updated.getHomeField(2).flatMap(_.figure) shouldBe Some(f2)
    }

    "return a board state string representation" in {
      val board = BoardFactory().build()
      val state = board.boardState

      state should not be empty
      state should include ("Field") // oder: prüfe auf "(", ")" oder "\n"
    }

    "calculate next index when moving figure over normal fields" in {
      val p = Player(1, Nil, Color.RED)
      val f = Figure(1, p)
      val pWithFigure = p.copy(figures = List(f))
      val factory = BoardFactory().withPlayers(List(pWithFigure))
      val board = factory.build()

      // Position auf Startfeld
      val fromIdx = board.fields.indexWhere(f => f.fieldType == FieldType.Start && f.color == p.color)
      val placed = board.updateField(fromIdx + 1, Some(f))

      val figureOnBoard = placed.fields(fromIdx).figure.get
      val moved = placed.moveFigure(figureOnBoard, 3) // >1 → ruft sicher findNextIndex auf

      moved should not be theSameInstanceAs(placed) // sicher bewegt
    }

    "should return None when landing on foreign goal field (trigger line 56)" in {
      val red = Player(1, Nil, Color.RED)
      val fig = Figure(1, red)
      val redWithFig = red.copy(figures = List(fig))

      val normalField = Field(Some(fig), FieldType.Normal, Color.WHITE) // Startposition mit Figur
      val blueGoalField = Field(None, FieldType.Goal, Color.BLUE)       // Fremdes Zielfeld

      val board = Board(Vector(normalField, blueGoalField), Vector.empty)

      // moveFigure muss erkennen: Ziel ist Goal mit falscher Farbe ⇒ None ⇒ this
      val moved = board.moveFigure(fig, 1)

      moved shouldBe board
    }

    "should not move if destination is blocked by own figure (line 86)" in {
      val p = Player(1, Nil, Color.RED)
      val fig1 = Figure(1, p)
      val fig2 = Figure(2, p)
      val pWithFigures = p.copy(figures = List(fig1, fig2))

      // fig1 steht auf Start, fig2 steht direkt im Ziel
      val start = Field(Some(fig1), FieldType.Normal, Color.WHITE)
      val blocked = Field(Some(fig2), FieldType.Normal, Color.WHITE)

      val board = Board(Vector(start, blocked), Vector.empty)

      val moved = board.moveFigure(fig1, 1)

      moved shouldBe board // Blockiert → keine Bewegung
    }


    "should do nothing if figure is not on board (line 92)" in {
      val p = Player(1, Nil, Color.RED)
      val fig = Figure(1, p)

      val field = Field(None, FieldType.Normal, Color.WHITE)
      val board = Board(Vector(field), Vector.empty)

      val moved = board.moveFigure(fig, 1)

      moved shouldBe board // Figur ist nirgendwo → keine Bewegung
    }

    "should format non-empty home fields in toString" in {
      val p = Player(1, Nil, Color.RED)
      val fig = Figure(1, p)
      val homeField = Field(Some(fig), FieldType.Home, Color.RED)

      val board = Board(Vector.empty, Vector.fill(4)(homeField))

      val str = board.toString

      str should include ("(R")   // Farbe Rot
      str should include ("H)")   // Typ Home
      str should include ("|")    // Trennzeichen aus mkString
    }







  }
}
