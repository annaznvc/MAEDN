package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.model.statesImp.{ConfigState, RunningState, MenuState}
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.controller.IController
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

// Dummy-Controller für Tests
class ConfigDummyController extends IController {
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
object ConfigTestHelper {
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

class ConfigStateSpec extends AnyWordSpec with Matchers {

  "A ConfigState" should {

    val controller = new ConfigDummyController
    val board = Board(8)
    val players = ConfigTestHelper.createPlayers(2, 4)
    val state = ConfigState(controller, 0, board, players)

    "have state == Config" in {
      state.state shouldBe State.Config
    }

    "start game and transition to RunningState" in {
      val next = state.startGame()
      next.isSuccess shouldBe true
      next.get shouldBe a[RunningState]
    }

    "quit game and return to MenuState" in {
      val next = state.quitGame()
      next.isSuccess shouldBe true
      next.get shouldBe a[MenuState]
    }

    "increase board size but not exceed 12" in {
      val newState = state.copy(board = Board(11)).increaseBoardSize()
      newState.isSuccess shouldBe true
      newState.get.board.size shouldBe 12

      val capped = newState.get.increaseBoardSize()
      capped.get.board.size shouldBe 12 // cap reached
    }

    "decrease board size but not go below 8" in {
      val newState = state.copy(board = Board(9)).decreaseBoardSize()
      newState.isSuccess shouldBe true
      newState.get.board.size shouldBe 8

      val capped = newState.get.decreaseBoardSize()
      capped.get.board.size shouldBe 8 // min reached
    }

    "increase number of figures per player but not exceed board size" in {
      val s = state.copy(
        players = ConfigTestHelper.createPlayers(2, 7),
        board = Board(8)
      )
      val newState = s.increaseFigures()
      newState.isSuccess shouldBe true
      newState.get.players.head.figures should have size 8

      val capped = newState.get.increaseFigures()
      capped.get.players.head.figures should have size 8
    }

    "decrease number of figures per player but not go below 1" in {
      val s = state.copy(players = ConfigTestHelper.createPlayers(2, 2))
      val newState = s.decreaseFigures()
      newState.isSuccess shouldBe true
      newState.get.players.head.figures should have size 1

      val capped = newState.get.decreaseFigures()
      capped.get.players.head.figures should have size 1
    }

    "increase number of players up to 4" in {
      val s = state.copy(players = ConfigTestHelper.createPlayers(2, 3))
      val newState = s.moveUp()
      newState.isSuccess shouldBe true
      newState.get.players should have size 3

      val capped = newState.get.moveUp().get.moveUp()
      capped.get.players should have size 4
    }

    "decrease number of players down to 2" in {
      val s = state.copy(players = ConfigTestHelper.createPlayers(4, 3))
      val newState = s.moveDown()
      newState.isSuccess shouldBe true
      newState.get.players should have size 3

      val capped = newState.get.moveDown().get.moveDown()
      capped.get.players should have size 2
    }
  }
}
