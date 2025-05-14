import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.State
import de.htwg.se.MAEDN.controller.command._

class ControllerSpec extends AnyWordSpec with Matchers {

  "A Controller" should {

    "start the game" in {
      val c = new Controller()
      StartGameCommand(c).execute()
      c.manager.state shouldBe State.Config
    }

    "quit the game in menu" in {
      val c = new Controller()
      QuitGameCommand(c).execute()
      c.manager.state shouldBe State.Menu
    }

    "increase and decrease board size" in {
      val c = new Controller()
      StartGameCommand(c).execute()
      val oldSize = c.manager.getBoardSize
      IncreaseBoardSizeCommand(c).execute()
      c.manager.getBoardSize shouldBe (oldSize + 1)

      DecreaseBoardSizeCommand(c).execute()
      c.manager.getBoardSize shouldBe oldSize
    }

    "increase and decrease figures" in {
      val c = new Controller()
      StartGameCommand(c).execute()
      val oldCount = c.manager.getFigureCount
      IncreaseFiguresCommand(c).execute()
      c.manager.getFigureCount shouldBe (oldCount + 1)

      DecreaseFiguresCommand(c).execute()
      c.manager.getFigureCount shouldBe oldCount
    }

    "move up and down in config" in {
      val c = new Controller()
      StartGameCommand(c).execute()
      val oldPlayerCount = c.manager.getPlayerCount
      MoveUpCommand(c).execute()
      c.manager.getPlayerCount should (be >= oldPlayerCount)

      MoveDownCommand(c).execute()
      c.manager.getPlayerCount should (be <= oldPlayerCount + 1)
    }

    "handle PlayNext and PlayDice safely even in non-running state" in {
      val c = new Controller()
      noException should be thrownBy PlayNextCommand(c).execute()
      noException should be thrownBy PlayDiceCommand(c).execute()
    }
  }
}
