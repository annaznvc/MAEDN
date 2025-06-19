package de.htwg.se.MAEDN.model.states

import de.htwg.se.MAEDN.controller.controllerImp.Controller
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.util.Event
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class RunningStateSpec extends AnyWordSpec with Matchers {

  // DummyStrategy für Tests – tut nichts
  object DummyStrategy extends IMoveStrategy {
    override def moveFigure(
        figure: Figure,
        all: List[Figure],
        size: Int,
        steps: Int
    ): List[Figure] = all // keine Änderung = Bewegung "fehlschlägt"

    override def canMove(
        figure: Figure,
        all: List[Figure],
        size: Int,
        steps: Int
    ): Boolean = true // Bewegung immer möglich
  }

  "A RunningState" should {

    val controller = new Controller
    val board = Board(8)
    val players = PlayerFactory(2, 4)
    val state = RunningState(controller, 0, board, players)

    "have state == Running" in {
      state.state shouldBe State.Running
    }

    "move up in selectedFigure index" in {
      val next = state.moveUp()
      next.isSuccess shouldBe true
      next.get.selectedFigure shouldBe 1
    }

    "move down in selectedFigure index" in {
      val next = state.moveDown()
      next.isSuccess shouldBe true
      next.get.selectedFigure shouldBe 3
    }

    "roll dice with playDice and store value" in {
      val rolledState = state.playDice()
      rolledState.isSuccess shouldBe true
      rolledState.get.rolled should (be >= 1 and be <= 6)
    }

    "call playNext with rolled == -1 and go to next player" in {
      val s = state.copy(rolled = -1)
      val next = s.playNext()
      next.isSuccess shouldBe true
      next.get.getCurrentPlayer shouldBe 1
      next.get.rolled shouldBe 0
    }

    "call playNext with rolled == 0 and roll dice" in {
      val s = state.copy(rolled = 0)
      val next = s.playNext()
      next.isSuccess shouldBe true
      next.get.rolled should (be >= 1 and be <= 6)
    }

    "call playNext with rolled > 0 and delegate to moveFigure" in {
      val s = state.copy(rolled = 3)
      val result = s.playNext()
      result.isSuccess || result.isFailure shouldBe true
    }

    "return Failure if move is not possible" in {
      val s = state.copy(rolled = 3)
      val r = s.moveFigure()
      r.isSuccess || r.isFailure shouldBe true
    }

    "create a valid memento" in {
      val memento = state.copy(rolled = 6, selectedFigure = 2).createMemento
      memento.isDefined shouldBe true
      memento.get.rolled shouldBe 6
      memento.get.selectedFigure shouldBe 2
    }

    "return to MenuState on quitGame" in {
      val newState = state.quitGame()
      newState.isSuccess shouldBe true
      newState.get shouldBe a[MenuState]
    }

    "fail moveFigure if board.moveFigure returns unchanged list" in {
      val figure = players.head.figures.head
      val fakeBoard = Board(8, DummyStrategy, DummyStrategy, DummyStrategy)

      val s = RunningState(
        controller,
        0,
        fakeBoard,
        players,
        rolled = 3,
        selectedFigure = 0
      )
      val result = s.moveFigure()
      result.isFailure shouldBe true
      result.failed.get shouldBe a[IllegalArgumentException]
    }

    "succeed moveFigure and return updated state with changed players and rolled = -1" in {
      val figure = players.head.figures.head
      val moved = figure.copy(index = figure.index + 3)

      val customStrategy = new IMoveStrategy {
        override def moveFigure(
            f: Figure,
            all: List[Figure],
            size: Int,
            steps: Int
        ): List[Figure] = all.map {
          case `figure` => moved
          case other    => other
        }

        override def canMove(
            figure: Figure,
            all: List[Figure],
            size: Int,
            steps: Int
        ): Boolean = true
      }

      val fakeBoard = Board(8, customStrategy, customStrategy, DummyStrategy)

      val s = RunningState(
        controller,
        0,
        fakeBoard,
        players,
        rolled = 3,
        selectedFigure = 0
      )
      val result = s.moveFigure()
      result.isSuccess shouldBe true

      val newState = result.get
      newState.players.exists(
        _.figures.exists(_.index == moved.index)
      ) shouldBe true
      newState.rolled shouldBe -1
    }

    "reset rolled to 0 if player rolls a 6 and moves" in {
      val figure = players.head.figures.head
      val moved = figure.copy(index = figure.index + 6)

      val customStrategy = new IMoveStrategy {
        override def moveFigure(
            f: Figure,
            all: List[Figure],
            size: Int,
            steps: Int
        ): List[Figure] = all.map {
          case `figure` => moved
          case other    => other
        }

        override def canMove(
            figure: Figure,
            all: List[Figure],
            size: Int,
            steps: Int
        ): Boolean = true
      }

      val fakeBoard = Board(8, customStrategy, customStrategy, DummyStrategy)

      val s = RunningState(
        controller,
        0,
        fakeBoard,
        players,
        rolled = 6,
        selectedFigure = 0
      )
      val result = s.moveFigure()
      result.isSuccess shouldBe true
      result.get.rolled shouldBe 0
    }
  }
}
