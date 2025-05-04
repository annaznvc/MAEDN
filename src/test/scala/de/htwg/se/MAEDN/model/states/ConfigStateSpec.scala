import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model.states.ConfigState
import de.htwg.se.MAEDN.model.{BoardFactory, Player, Figure, State}
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.controller.Controller

class ConfigStateSpec extends AnyWordSpec with Matchers {

  "A ConfigState" should {

    val controller = new Controller()
    val figure = Figure(1, null)
    val player = Player(1, List(figure), Color.RED)
    val boardFactory = BoardFactory().withPlayers(List(player)).withFigureCount(1)
    val board = boardFactory.build()

    val config = ConfigState(controller, moves = 0, board, List(player), rolled = 0, boardFactory)

    "have state Config" in {
      config.state shouldBe State.Config
    }

    "increase and decrease board size" in {
      val bigger = config.increaseBoardSize()
      bigger.getBoardSize shouldBe (config.getBoardSize + 1)

      val smaller = bigger.decreaseBoardSize()
      smaller.getBoardSize shouldBe config.getBoardSize
    }

    "increase and decrease figures" in {
      val more = config.increaseFigures()
      more.getFigureCount shouldBe (config.getFigureCount + 1)

      val less = more.decreaseFigures()
      less.getFigureCount shouldBe config.getFigureCount
    }

    "add and remove players" in {
        val added1 = config.moveUp()
        val added2 = added1.moveUp()
        added2.getPlayerCount shouldBe 3

        val removed = added2.moveDown()
        removed.getPlayerCount shouldBe 2
    }

    "switch to RunningState on startGame" in {
      val running = config.startGame()
      running.state shouldBe State.Running
    }

    "return to MenuState on quitGame" in {
      val back = config.quitGame()
      back.state shouldBe State.Menu
    }

    "should not add more than 4 players" in {
        val controller = new Controller()
        val fig = Figure(1, null)
        val player = Player(1, List(fig), Color.RED)
        val players = List.fill(4)(player)
        val board = BoardFactory().withPlayers(players).build()
        val config = ConfigState(controller, 0, board, players)

        val result = config.moveUp()
        result shouldBe config // nothing changed → triggers "this"
    }

    "should not remove players if only 2 remain" in {
        val controller = new Controller()
        val fig = Figure(1, null)
        val player = Player(1, List(fig), Color.RED)
        val players = List.fill(2)(player)
        val board = BoardFactory().withPlayers(players).build()
        val config = ConfigState(controller, 0, board, players)

        val result = config.moveDown()
        result shouldBe config // nothing changed → triggers "this"
        }



  }
}
