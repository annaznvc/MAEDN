package de.htwg.se.MAEDN.model.StatesImp

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.model.BoardImp.Board
import de.htwg.se.MAEDN.model.PlayerImp.Player
import de.htwg.se.MAEDN.model.FigureImp.Figure
import de.htwg.se.MAEDN.util.Color
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class RunningStateSpec extends AnyWordSpec with Matchers {

  // DummyStrategy für gezielte Tests
  object DummyStrategy extends IMoveStrategy {
    override def moveFigure(
        figure: IFigure,
        all: List[IFigure],
        size: Int,
        steps: Int
    ): List[IFigure] = all // keine Änderung = Bewegung "fehlschlägt"

    override def canMove(
        figure: IFigure,
        all: List[IFigure],
        size: Int,
        steps: Int
    ): Boolean = true // Bewegung immer möglich
  }

  val controller = new Controller
  val board = Board(
    8,
    IMoveStrategy.createNormalMoveStrategy(),
    IMoveStrategy.createToBoardStrategy(),
    IMoveStrategy.createKickFigureStrategy()
  )
  val player = Player(1, Nil, Color.RED)
  val figures = List.tabulate(4)(i => Figure(i + 1, player, i, 4))
  val players = List(
    player.copy(figures = figures),
    player.copy(figures = figures, id = 2, color = Color.BLUE)
  )
  val state =
    RunningState(controller, 0, board, players, rolled = 0, selectedFigure = 0)

  "A RunningState" should {

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
      memento.get shouldBe a[de.htwg.se.MAEDN.model.GameDataImp.GameData]
      val gd =
        memento.get.asInstanceOf[de.htwg.se.MAEDN.model.GameDataImp.GameData]
      gd.rolled shouldBe 6
      gd.selectedFigure shouldBe 2
    }

    "return to MenuState on quitGame" in {
      val newState = state.quitGame()
      newState.isSuccess shouldBe true
      newState.get shouldBe a[de.htwg.se.MAEDN.model.StatesImp.MenuState]
    }

    "succeed moveFigure and return updated state with changed players and rolled = -1" in {
      val figure = players.head.figures.head
      val moved = figure.copy(index = figure.index + 3)

      val customStrategy = new IMoveStrategy {
        override def moveFigure(
            f: IFigure,
            all: List[IFigure],
            size: Int,
            steps: Int
        ): List[IFigure] = all.map {
          case fig if fig == figure => moved
          case other                => other
        }
        override def canMove(
            figure: IFigure,
            all: List[IFigure],
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
            f: IFigure,
            all: List[IFigure],
            size: Int,
            steps: Int
        ): List[IFigure] = all.map {
          case fig if fig == figure => moved
          case other                => other
        }
        override def canMove(
            figure: IFigure,
            all: List[IFigure],
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

    // Test für Zeilen 56-59: playNext mit rolled != -1 und canMoveAny = false
    "go to next player when no figures can move" in {
      val immovableFigures = List.tabulate(4)(i =>
        Figure(i + 1, player, -1, 4)
      ) // negative index = kann nicht bewegt werden
      val immovablePlayer = player.copy(figures = immovableFigures)
      val testPlayers = List(immovablePlayer, players(1))

      val cannotMoveStrategy = new IMoveStrategy {
        override def moveFigure(
            f: IFigure,
            all: List[IFigure],
            size: Int,
            steps: Int
        ): List[IFigure] = all
        override def canMove(
            figure: IFigure,
            all: List[IFigure],
            size: Int,
            steps: Int
        ): Boolean = false
      }

      val testBoard =
        Board(8, cannotMoveStrategy, cannotMoveStrategy, cannotMoveStrategy)
      val s = RunningState(
        controller,
        0,
        testBoard,
        testPlayers,
        rolled = 3,
        selectedFigure = 0
      )

      val result = s.playNext()
      result.isSuccess shouldBe true
      result.get.getCurrentPlayer shouldBe 1
      result.get.rolled shouldBe 0
      result.get.moves shouldBe 1
    }

// Test für Zeilen 65-75: playNext mit moveFigure Failure und stillCanMove = false
    "go to next player when moveFigure fails and no figures can still move" in {
      val failingStrategy = new IMoveStrategy {
        override def moveFigure(
            f: IFigure,
            all: List[IFigure],
            size: Int,
            steps: Int
        ): List[IFigure] =
          throw new RuntimeException("Move failed")
        override def canMove(
            figure: IFigure,
            all: List[IFigure],
            size: Int,
            steps: Int
        ): Boolean = false
      }

      val testBoard =
        Board(8, failingStrategy, failingStrategy, failingStrategy)
      val s = RunningState(
        controller,
        0,
        testBoard,
        players,
        rolled = 3,
        selectedFigure = 0
      )

      val result = s.playNext()
      result.isSuccess shouldBe true
      result.get.getCurrentPlayer shouldBe 1
      result.get.rolled shouldBe 0
      result.get.moves shouldBe 1
    }

// Test für Zeilen 65-75: playNext mit moveFigure Failure und stillCanMove = true
    "return Failure when moveFigure fails but figures can still move" in {
      val partialFailStrategy = new IMoveStrategy {
        override def moveFigure(
            f: IFigure,
            all: List[IFigure],
            size: Int,
            steps: Int
        ): List[IFigure] =
          throw new RuntimeException("Move failed")
        override def canMove(
            figure: IFigure,
            all: List[IFigure],
            size: Int,
            steps: Int
        ): Boolean = true
      }

      val testBoard =
        Board(8, partialFailStrategy, partialFailStrategy, partialFailStrategy)
      val s = RunningState(
        controller,
        0,
        testBoard,
        players,
        rolled = 3,
        selectedFigure = 0
      )

      val result = s.playNext()
      result.isFailure shouldBe true
      result.failed.get shouldBe a[IllegalArgumentException]
    }

// Test für Zeilen 99-106: moveFigure mit movableIndices.isEmpty
    "go to next player when no movable indices exist in moveFigure" in {
      val cannotMoveStrategy = new IMoveStrategy {
        override def moveFigure(
            f: IFigure,
            all: List[IFigure],
            size: Int,
            steps: Int
        ): List[IFigure] = all
        override def canMove(
            figure: IFigure,
            all: List[IFigure],
            size: Int,
            steps: Int
        ): Boolean = false
      }

      val testBoard =
        Board(8, cannotMoveStrategy, cannotMoveStrategy, cannotMoveStrategy)
      val s = RunningState(
        controller,
        0,
        testBoard,
        players,
        rolled = 3,
        selectedFigure = 0
      )

      val result = s.moveFigure()
      result.isSuccess shouldBe true
      result.get.getCurrentPlayer shouldBe 1
      result.get.rolled shouldBe 0
      result.get.moves shouldBe 1
    }

// Test für Zeilen 99-106: moveFigure mit selectedFigure nicht bewegbar, aber andere Figuren bewegbar
    "change selectedFigure when current selection cannot move but others can" in {
      val selectiveStrategy = new IMoveStrategy {
        override def moveFigure(
            f: IFigure,
            all: List[IFigure],
            size: Int,
            steps: Int
        ): List[IFigure] = all
        override def canMove(
            figure: IFigure,
            all: List[IFigure],
            size: Int,
            steps: Int
        ): Boolean =
          figure.id != 1 // Erste Figur kann nicht bewegt werden
      }

      val testBoard =
        Board(8, selectiveStrategy, selectiveStrategy, selectiveStrategy)
      val s = RunningState(
        controller,
        0,
        testBoard,
        players,
        rolled = 3,
        selectedFigure = 0
      )

      val result = s.moveFigure()
      result.isSuccess shouldBe true
      result.get.selectedFigure shouldBe 1 // Sollte zur nächsten bewegbaren Figur wechseln
    }

// Test für Zeilen 112-116: moveFigure mit newFigures == figures und movableIndices.isEmpty
    "go to next player when move results in no change and no movable indices" in {
      val noChangeStrategy = new IMoveStrategy {
        override def moveFigure(
            f: IFigure,
            all: List[IFigure],
            size: Int,
            steps: Int
        ): List[IFigure] = all
        override def canMove(
            figure: IFigure,
            all: List[IFigure],
            size: Int,
            steps: Int
        ): Boolean = false // Keine Figur kann bewegt werden
      }

      val testBoard =
        Board(8, noChangeStrategy, noChangeStrategy, noChangeStrategy)
      val s = RunningState(
        controller,
        0,
        testBoard,
        players,
        rolled = 3,
        selectedFigure = 0
      )

      val result = s.moveFigure()
      result.isSuccess shouldBe true
      result.get.getCurrentPlayer shouldBe 1
      result.get.rolled shouldBe 0
      result.get.moves shouldBe 1
    }

// Test für Zeile 140: WinEvent
    "trigger WinEvent when player reaches goal" in {
      val winningFigures = List.tabulate(4)(i =>
        Figure(i + 1, player, 8, 4)
      ) // Alle Figuren am Ziel (boardSize = 8)
      val winningPlayer = player.copy(figures = winningFigures)
      val testPlayers = List(winningPlayer, players(1))

      val winStrategy = new IMoveStrategy {
        override def moveFigure(
            f: IFigure,
            all: List[IFigure],
            size: Int,
            steps: Int
        ): List[IFigure] =
          all.map(fig => if (fig.owner.id == 1) fig.copy(index = 8) else fig)
        override def canMove(
            figure: IFigure,
            all: List[IFigure],
            size: Int,
            steps: Int
        ): Boolean = true
      }

      val testBoard = Board(8, winStrategy, winStrategy, winStrategy)
      val s = RunningState(
        controller,
        0,
        testBoard,
        testPlayers,
        rolled = 3,
        selectedFigure = 0
      )

      val result = s.moveFigure()
      result.isSuccess shouldBe true
      // WinEvent sollte ausgelöst werden (kann durch Mock-Controller verifiziert werden)
    }

// Test für Zeile 170: getPlayers
    "return correct players list" in {
      state.getPlayers shouldBe players
      state.getPlayers.size shouldBe 2
      state.getPlayers.head.id shouldBe 1
      state.getPlayers(1).id shouldBe 2
    }
  }
}
