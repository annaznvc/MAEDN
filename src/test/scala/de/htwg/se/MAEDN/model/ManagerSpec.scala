import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.Manager

class ManagerSpec extends AnyWordSpec with Matchers {

  "The Manager object" should {

    "create a default MenuState via apply" in {
      val controller = new Controller()
      val manager = Manager(controller)

      manager.getPlayerCount shouldBe 2
      manager.getFigureCount shouldBe 4
      manager.getBoardSize should be >= 8
    }

    "have default no-op implementations in base Manager trait" in {
        val dummy = new Manager {
            override val controller = null
            override val moves = 0
            override val rolled = 0
            override val board = null
            override val players = Nil
            override val state = null

            override def getPlayerCount = 0
            override def getFigureCount = 0
            override def getBoardSize = 0
            override def getCurrentPlayer = 0
        }

        dummy.increaseBoardSize() shouldBe dummy
        dummy.decreaseBoardSize() shouldBe dummy
        dummy.increaseFigures() shouldBe dummy
        dummy.decreaseFigures() shouldBe dummy
        dummy.moveUp() shouldBe dummy
        dummy.moveDown() shouldBe dummy
        dummy.quitGame() shouldBe dummy
        dummy.startGame() shouldBe dummy
        dummy.moveFigure() shouldBe dummy
        }

  }
}
