import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.aview.Command
import de.htwg.se.MAEDN.model.State

class ControllerSpec extends AnyWordSpec with Matchers {

  "A Controller" should {

    "start the game" in {
      val c = new Controller()
      c.processCommand(Command.StartGame)
      c.manager.state shouldBe State.Config
    }

    "quit the game in menu" in {
      val c = new Controller()
      c.processCommand(Command.QuitGame)
      c.manager.state shouldBe State.Menu
    }

    "increase and decrease board size" in {
      val c = new Controller()
      c.processCommand(Command.StartGame)
      val oldSize = c.manager.getBoardSize
      c.processCommand(Command.IncreaseBoardSize)
      c.manager.getBoardSize shouldBe (oldSize + 1)

      c.processCommand(Command.DecreaseBoardSize)
      c.manager.getBoardSize shouldBe oldSize
    }

    "increase and decrease figures" in {
      val c = new Controller()
      c.processCommand(Command.StartGame)
      val oldCount = c.manager.getFigureCount
      c.processCommand(Command.IncreaseFigures)
      c.manager.getFigureCount shouldBe (oldCount + 1)

      c.processCommand(Command.DecreaseFigures)
      c.manager.getFigureCount shouldBe oldCount
    }

    "move up and down in config" in {
      val c = new Controller()
      c.processCommand(Command.StartGame)
      val oldPlayerCount = c.manager.getPlayerCount
      c.processCommand(Command.MoveUp)
      c.manager.getPlayerCount should (be >= oldPlayerCount)

      c.processCommand(Command.MoveDown)
      c.manager.getPlayerCount should (be <= oldPlayerCount + 1)
    }

    "handle PlayNext and PlayDice safely even in non-running state" in {
      val c = new Controller()
      noException should be thrownBy c.processCommand(Command.PlayNext)
      noException should be thrownBy c.processCommand(Command.PlayDice)
    }

    "do nothing on unknown command" in {
      val c = new Controller()
      noException should be thrownBy c.processCommand(Command.Escape)
    }
  }
}
