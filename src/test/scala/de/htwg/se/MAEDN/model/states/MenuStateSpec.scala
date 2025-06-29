package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.model.statesImp.MenuState
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.controller.IController
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

// Dummy-Controller für Tests
class MenuDummyController extends IController {
  var manager: IManager = null

  override def executeCommand(
      command: de.htwg.se.MAEDN.controller.command.Command
  ): Unit = ()
  override def add(observer: de.htwg.se.MAEDN.util.Observer): Unit = ()
  override def remove(observer: de.htwg.se.MAEDN.util.Observer): Unit = ()
  override def undoStack = scala.collection.mutable.Stack.empty[IMemento]
  override def redoStack = scala.collection.mutable.Stack.empty[IMemento]
  override def eventQueue = scala.collection.mutable.PriorityQueue
    .empty[de.htwg.se.MAEDN.util.Event](Ordering.by(_.priority))
  override def enqueueEvent(event: de.htwg.se.MAEDN.util.Event): Unit = ()
}

// Hilfsobjekt für PlayerFactory
object MenuTestHelper {
  def createPlayers(playerCount: Int, figureCount: Int): List[Player] = {
    (1 to playerCount).toList.map { pid =>
      val color = Color.values((pid - 1) % Color.values.size)
      val player = Player(pid, Nil, color)
      val figures =
        (0 until figureCount).map(i => Figure(i, player, i, figureCount)).toList
      player.copy(figures = figures)
    }
  }
}

class MenuStateSpec extends AnyWordSpec with Matchers {
  "A MenuState" should {
    val controller = new MenuDummyController
    val board = Board(8)
    val players = MenuTestHelper.createPlayers(2, 4)
    val state = MenuState(controller, 0, board, players)

    "have state == Menu" in {
      state.state shouldBe State.Menu
    }

    "return correct player count" in {
      state.getPlayerCount shouldBe 2
    }

    "return correct figure count" in {
      state.getFigureCount shouldBe 4
    }

    "return correct board size" in {
      state.getBoardSize shouldBe 8
    }

    "return correct players list" in {
      state.getPlayers shouldBe players
    }

    "return current player as 0" in {
      state.getCurrentPlayer shouldBe 0
    }
  }
}
