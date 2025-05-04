import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model.{Field, FieldType, Figure, Player}
import de.htwg.se.MAEDN.util.Color

class FieldSpec extends AnyWordSpec with Matchers {

  val dummyPlayer = Player(1, Nil, Color.RED)
  val dummyFigure = Figure(1, dummyPlayer)

  "Field" should {
    "report if it is occupied" in {
      val f1 = Field(Some(dummyFigure), FieldType.Normal, Color.RED)
      val f2 = Field(None, FieldType.Normal, Color.RED)

      f1.isOccupied shouldBe true
      f2.isOccupied shouldBe false
    }

    "report if it is a Home field" in {
      Field(None, FieldType.Home, Color.RED).isHome shouldBe true
      Field(None, FieldType.Start, Color.RED).isHome shouldBe false
    }

    "report if it is a Start field" in {
      Field(None, FieldType.Start, Color.RED).isStart shouldBe true
      Field(None, FieldType.Goal, Color.RED).isStart shouldBe false
    }

    "report if it is a Goal field" in {
      Field(None, FieldType.Goal, Color.RED).isGoal shouldBe true
      Field(None, FieldType.Normal, Color.RED).isGoal shouldBe false
    }

    "fail if fieldType is null" in {
      an[IllegalArgumentException] should be thrownBy {
        Field(None, null, Color.RED)
      }
    }
  }

}
