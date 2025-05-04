import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model.states.MenuState
import de.htwg.se.MAEDN.model.{BoardFactory, Player, Figure}
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model.State

class MenuStateSpec extends AnyWordSpec with Matchers {

  "A MenuState" should {

    val controller = new Controller()
    val player = Player(1, List(Figure(1, null)), Color.RED)
    val board = BoardFactory().withPlayers(List(player)).build()
    val menu = MenuState(controller, moves = 0, board, List(player))

    "have state Menu" in {
      menu.state shouldBe State.Menu
    }

    "switch to ConfigState on startGame" in {
      val next = menu.startGame()
      next.state shouldBe State.Config
    }

    "remain in MenuState on quitGame" in {
      val quit = menu.quitGame()
      quit.state shouldBe State.Menu
    }
  }
}
