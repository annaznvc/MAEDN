import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model.{BoardFactory, Player, Figure}
import de.htwg.se.MAEDN.util.Color

class BoardFactorySpec extends AnyWordSpec with Matchers {

  "A BoardFactory" should {

    "cap board size between 8 and 12" in {
      val tooSmall = BoardFactory().withBoardSize(3)
      val tooLarge = BoardFactory().withBoardSize(20)
      val normal = BoardFactory().withBoardSize(10)

      tooSmall.normalFieldCount shouldBe 8
      tooLarge.normalFieldCount shouldBe 12
      normal.normalFieldCount shouldBe 10
    }

    "not allow figure count below 1" in {
      val factory = BoardFactory().withFigureCount(0)
      factory.figureCountPerPlayer shouldBe 1
    }

    "store players via withPlayers" in {
      val player = Player(1, List(Figure(1, null)), Color.RED)
      val factory = BoardFactory().withPlayers(List(player))
      factory.players should contain theSameElementsAs List(player)
    }

    "build a board with correct field count" in {
      val player = Player(1, List(Figure(1, null)), Color.RED)
      val factory = BoardFactory()
        .withBoardSize(8)
        .withFigureCount(2)
        .withPlayers(List(player))

      val board = factory.build()

      board.fields.length shouldBe (4 * 8 + 4 + 4 * 2) // normal + start + goal
      board.homeFields.length shouldBe 2              // 2 Figuren bei 1 Spieler
    }
  }
}
