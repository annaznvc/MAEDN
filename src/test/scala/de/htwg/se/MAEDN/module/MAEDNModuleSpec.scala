package de.htwg.se.MAEDN.module

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder

import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.model.{Board, Player, IManager, IMemento}
import de.htwg.se.MAEDN.util.{Dice, FileIO, PlayerFactory, Event, Observer}
import de.htwg.se.MAEDN.module.MAEDNModule
import de.htwg.se.MAEDN.aview.tui.TUI
import de.htwg.se.MAEDN.aview.gui.GUI
import de.htwg.se.MAEDN.model.strategy.{
  NormalMoveStrategy,
  ToBoardStrategy,
  KickFigureStrategy
}
import de.htwg.se.MAEDN.controller.command.Command
import scala.collection.mutable.{PriorityQueue, Stack}

class MAEDNModuleSpec extends AnyWordSpec with Matchers {

  // Dummy Controller stub for provider tests
  object DummyController extends IController {
    // manager is not used by providers under test
    var manager: IManager = _
    def executeCommand(command: Command): Unit = ()
    def add(observer: Observer): Unit = ()
    def remove(observer: Observer): Unit = ()
    def undoStack: Stack[IMemento] = Stack.empty[IMemento]
    def redoStack: Stack[IMemento] = Stack.empty[IMemento]
    def eventQueue: PriorityQueue[Event] =
      PriorityQueue.empty(Ordering.by[Event, Int](_ => 0))
    def enqueueEvent(event: Event): Unit = ()
  }

  private val module = MAEDNModule()

  "MAEDNModule providers" should {

    "provide a TUI instance" in {
      val terminal: Terminal = TerminalBuilder.builder().system(true).build()
      val tui: TUI = module.provideTUI(DummyController, terminal)
      tui shouldBe a[TUI]
    }

    "provide a GUI instance" in {
      val gui: GUI = module.provideGUI(DummyController)
      gui shouldBe a[GUI]
    }

    "provide a Board with default size and strategies" in {
      val board = module.provideBoard(
        new NormalMoveStrategy(),
        new ToBoardStrategy(),
        new KickFigureStrategy()
      )
      board shouldBe a[Board]
      board.size shouldBe 8
      board.moveStrategy shouldBe a[NormalMoveStrategy]
      board.toBoardStrategy shouldBe a[ToBoardStrategy]
      board.kickFigureStrategy shouldBe a[KickFigureStrategy]
    }

    "provide a default list of Players" in {
      val players: List[Player] = module.providePlayers()
      players should have size 2
      players.foreach { player =>
        player.figures should have size 4
      }
    }

    "provide Dice as a singleton" in {
      val d1 = module.provideDice()
      val d2 = module.provideDice()
      d1 shouldBe theSameInstanceAs(d2)
    }

    "companion apply and create produce module instances" in {
      MAEDNModule() shouldBe a[MAEDNModule]
      MAEDNModule.create() shouldBe a[MAEDNModule]
    }
  }
}
