package de.htwg.se.MAEDN.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model.StatesImp.{RunningState, MenuState}
import de.htwg.se.MAEDN.model.BoardImp.Board
import de.htwg.se.MAEDN.model.PlayerImp.Player
import de.htwg.se.MAEDN.model.FigureImp.Figure
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.util.PlayerFactory
import de.htwg.se.MAEDN.controller.Controller

class IManagerSpec extends AnyWordSpec with Matchers {
  "An IManager" should {
    // Dummy-Implementierung für das Interface
    val dummy = new IManager {
      override val moves = 0
      override val board = IBoard(8)
      override val players = PlayerFactory(2, 4)
      override val selectedFigure = 0
      override val rolled = 0
      override val state = State.Menu
      override val controller = null

      override def startGame() = util.Success(this)
      override def quitGame() = util.Success(this)
      override def moveUp() = util.Success(this)
      override def moveDown() = util.Success(this)
      override def increaseFigures() = util.Success(this)
      override def decreaseFigures() = util.Success(this)
      override def increaseBoardSize() = util.Success(this)
      override def decreaseBoardSize() = util.Success(this)
      override def playDice() = util.Success(this)
      override def playNext() = util.Success(this)
      override def moveFigure() = util.Success(this)
      override def createMemento = None
    }

    "have default values and implement all methods" in {
      dummy.moves shouldBe 0
      dummy.board.size shouldBe 8
      dummy.players.size shouldBe 2
      dummy.selectedFigure shouldBe 0
      dummy.rolled shouldBe 0
      dummy.state shouldBe State.Menu
      dummy.getPlayerCount shouldBe 2
      dummy.getFigureCount shouldBe dummy.players.head.figures.size
      dummy.getBoardSize shouldBe 8
      dummy.getCurrentPlayer shouldBe 0
      dummy.getPlayers shouldBe dummy.players
    }

    "return Success for all actions" in {
      dummy.startGame().isSuccess shouldBe true
      dummy.quitGame().isSuccess shouldBe true
      dummy.moveUp().isSuccess shouldBe true
      dummy.moveDown().isSuccess shouldBe true
      dummy.increaseFigures().isSuccess shouldBe true
      dummy.decreaseFigures().isSuccess shouldBe true
      dummy.increaseBoardSize().isSuccess shouldBe true
      dummy.decreaseBoardSize().isSuccess shouldBe true
      dummy.playDice().isSuccess shouldBe true
      dummy.playNext().isSuccess shouldBe true
      dummy.moveFigure().isSuccess shouldBe true
    }

    "call playDice, playNext, quitGame, startGame, moveFigure and createMemento" in {
      dummy.playDice().isSuccess shouldBe true
      dummy.playNext().isSuccess shouldBe true
      dummy.quitGame().isSuccess shouldBe true
      dummy.startGame().isSuccess shouldBe true
      dummy.moveFigure().isSuccess shouldBe true
      dummy.createMemento shouldBe None
    }
  }

  "A RunningState" should {
    "cover playDice, quitGame, startGame, moveFigure, createMemento" in {
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
      val running = RunningState(
        controller,
        0,
        board,
        players,
        rolled = 0,
        selectedFigure = 0
      )

      running.playDice().isSuccess shouldBe true
      running.quitGame().isSuccess shouldBe true
      running.startGame().isSuccess shouldBe true
      running.moveFigure().isSuccess shouldBe true
      running.createMemento.isDefined shouldBe true
    }

    "implement playDice correctly" in {
      val controller = new Controller
      val board = Board(
        8,
        IMoveStrategy.createNormalMoveStrategy(),
        IMoveStrategy.createToBoardStrategy(),
        IMoveStrategy.createKickFigureStrategy()
      )
      val player1 = Player(1, Nil, Color.RED)
      val player2 = Player(2, Nil, Color.BLUE)
      val figures1 = List.tabulate(4)(i => Figure(i + 1, player1, i, 4))
      val figures2 = List.tabulate(4)(i => Figure(i + 5, player2, i, 4))
      val players = List(
        player1.copy(figures = figures1),
        player2.copy(figures = figures2)
      )
      val runningState = RunningState(controller, 0, board, players, 0, 0)
      val result = runningState.playDice()
      result.isSuccess shouldBe true
      result.get.rolled should be > 0
      result.get.rolled should be <= 6
    }

    "implement quitGame correctly" in {
      val controller = new Controller
      val board = Board(
        8,
        IMoveStrategy.createNormalMoveStrategy(),
        IMoveStrategy.createToBoardStrategy(),
        IMoveStrategy.createKickFigureStrategy()
      )
      val player1 = Player(1, Nil, Color.RED)
      val player2 = Player(2, Nil, Color.BLUE)
      val figures1 = List.tabulate(4)(i => Figure(i + 1, player1, i, 4))
      val figures2 = List.tabulate(4)(i => Figure(i + 5, player2, i, 4))
      val players = List(
        player1.copy(figures = figures1),
        player2.copy(figures = figures2)
      )
      val runningState = RunningState(controller, 0, board, players, 3, 0)
      val result = runningState.quitGame()
      result.isSuccess shouldBe true
      result.get.state shouldBe State.Menu
    }

    "implement moveFigure correctly" in {
      val controller = new Controller
      val board = Board(
        8,
        IMoveStrategy.createNormalMoveStrategy(),
        IMoveStrategy.createToBoardStrategy(),
        IMoveStrategy.createKickFigureStrategy()
      )
      val player1 = Player(1, Nil, Color.RED)
      val player2 = Player(2, Nil, Color.BLUE)
      val figures1 = List.tabulate(4)(i => Figure(i + 1, player1, i, 4))
      val figures2 = List.tabulate(4)(i => Figure(i + 5, player2, i, 4))
      val players = List(
        player1.copy(figures = figures1),
        player2.copy(figures = figures2)
      )
      val runningState = RunningState(controller, 0, board, players, 3, 0)
      val result = runningState.moveFigure()
      result.isSuccess shouldBe true
    }

    "return Some(IMemento) for createMemento" in {
      val controller = new Controller
      val board = Board(
        8,
        IMoveStrategy.createNormalMoveStrategy(),
        IMoveStrategy.createToBoardStrategy(),
        IMoveStrategy.createKickFigureStrategy()
      )
      val player1 = Player(1, Nil, Color.RED)
      val player2 = Player(2, Nil, Color.BLUE)
      val figures1 = List.tabulate(4)(i => Figure(i + 1, player1, i, 4))
      val figures2 = List.tabulate(4)(i => Figure(i + 5, player2, i, 4))
      val players = List(
        player1.copy(figures = figures1),
        player2.copy(figures = figures2)
      )
      val runningState = RunningState(controller, 0, board, players, 3, 0)
      val memento = runningState.createMemento
      memento.isDefined shouldBe true
      // Cast auf konkreten Typ, falls Felder geprüft werden sollen:
      val gd =
        memento.get.asInstanceOf[de.htwg.se.MAEDN.model.GameDataImp.GameData]
      gd.moves shouldBe 0
      gd.rolled shouldBe 3
    }
  }

  "MenuState" should {
    "implement startGame and quitGame correctly" in {
      val controller = new Controller
      val menuState = MenuState(controller, 0, IBoard(8), PlayerFactory(2, 4))
      val startResult = menuState.startGame()
      startResult.isSuccess shouldBe true
      startResult.get.state shouldBe State.Config
      val quitResult = menuState.quitGame()
      quitResult.isSuccess shouldBe true
      quitResult.get.state shouldBe State.Menu
    }
    "return None for createMemento" in {
      val controller = new Controller
      val menuState = MenuState(controller, 0, IBoard(8), PlayerFactory(2, 4))
      menuState.createMemento shouldBe None
    }
  }

  "IManager factory methods" should {
    "create MenuState with apply method" in {
      val controller = new Controller
      val manager = IManager(controller)
      manager.state shouldBe State.Menu
      manager.moves shouldBe 0
      manager.board.size shouldBe 8
      manager.players.size shouldBe 2
    }

    "create RunningState with createRunningState method" in {
      val controller = new Controller
      val board = IBoard(8)
      val players = PlayerFactory(2, 4)
      val manager =
        IManager.createRunningState(controller, 5, board, players, 3, 1)
      manager.state shouldBe State.Running
      manager.moves shouldBe 5
      manager.rolled shouldBe 3
      manager.selectedFigure shouldBe 1
    }
  }

  "IManager trait default implementations" should {
    "return Try(this) for default methods" in {
      val controller = new Controller
      val manager = new IManager {
        override val controller = controller
        override val state = State.Menu
        override val moves = 0
        override val board = IBoard(8)
        override val players = PlayerFactory(2, 4)
        override val selectedFigure = 0
        override val rolled = 0
      }
      manager.playDice().isSuccess shouldBe true
      manager.quitGame().isSuccess shouldBe true
      manager.moveFigure().isSuccess shouldBe true
      manager.createMemento shouldBe None
    }
  }
}
