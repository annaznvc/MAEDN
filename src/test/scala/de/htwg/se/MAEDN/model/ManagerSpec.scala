package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.controller.controllerImp.Controller
import de.htwg.se.MAEDN.model.statesImp.MenuState
import de.htwg.se.MAEDN.util.PlayerFactory
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ManagerSpec extends AnyWordSpec with Matchers {

  "A Manager (via MenuState)" should {

    val controller = new Controller
    val defaultBoard = Board(8)
    val defaultPlayers = PlayerFactory(2, 4)

    val manager: IManager = MenuState(
      controller,
      moves = 0,
      board = defaultBoard,
      players = defaultPlayers
    )

    "have a default number of players" in {
      manager.getPlayerCount shouldBe 2
    }

    "have a default number of figures per player" in {
      manager.getFigureCount shouldBe 4
    }

    "have a default board size of 8" in {
      manager.getBoardSize shouldBe 8
    }

    "return itself or a valid next state for default implementations" in {
      manager.increaseBoardSize().get shouldBe a[IManager]
      manager.decreaseBoardSize().get shouldBe a[IManager]
      manager.increaseFigures().get shouldBe a[IManager]
      manager.decreaseFigures().get shouldBe a[IManager]
      manager.moveUp().get shouldBe a[IManager]
      manager.moveDown().get shouldBe a[IManager]
      manager.playDice().get shouldBe a[IManager]
      manager.playNext().get shouldBe a[IManager]
      manager.quitGame().get shouldBe a[IManager]
      manager.startGame().get shouldBe a[IManager]
      manager.moveFigure().get shouldBe a[IManager]
    }

    "have no memento by default" in {
      manager.createMemento shouldBe None
    }
  }

  "A DummyManager using default Manager trait" should {

    class DummyManager extends IManager {
      override val controller: Controller = new Controller
      override val moves: Int = 0
      override val board: Board = Board(8)
      override val players: List[Player] = PlayerFactory(2, 4)
      override val rolled: Int = 0
      override val state: State = State.Menu
      override val selectedFigure: Int = 0
    }

    val dummy = new DummyManager

    "return itself when calling quitGame or startGame" in {
      dummy.quitGame().get shouldBe theSameInstanceAs(dummy)
      dummy.startGame().get shouldBe theSameInstanceAs(dummy)
    }

    "have a working fallback implementation for all trait methods" in {
      dummy.increaseBoardSize().get shouldBe theSameInstanceAs(dummy)
      dummy.decreaseBoardSize().get shouldBe theSameInstanceAs(dummy)
      dummy.increaseFigures().get shouldBe theSameInstanceAs(dummy)
      dummy.decreaseFigures().get shouldBe theSameInstanceAs(dummy)
      dummy.moveUp().get shouldBe theSameInstanceAs(dummy)
      dummy.moveDown().get shouldBe theSameInstanceAs(dummy)
      dummy.playDice().get shouldBe theSameInstanceAs(dummy)
      dummy.playNext().get shouldBe theSameInstanceAs(dummy)
      dummy.moveFigure().get shouldBe theSameInstanceAs(dummy)
    }

    "have no memento" in {
      dummy.createMemento shouldBe None
    }
  }
}
