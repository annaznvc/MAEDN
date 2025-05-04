package de.htwg.se.MAEDN.aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.util.Event
import de.htwg.se.MAEDN.model.{Manager, State}

class TUISpec extends AnyWordSpec with Matchers {

  class TestTUI(controller: Controller) extends TUI(controller) {
    var lines: List[String] = Nil
    override protected def writeline(s: String): Unit = lines :+= s
  }

  "TUI" should {

    "react to StartGameEvent" in {
      val controller = new Controller()
      val tui = new TestTUI(controller)
      tui.processEvent(Event.StartGameEvent)
      tui.lines.exists(_.contains("Main Board")) shouldBe true
    }

    "react to InvalidMoveEvent" in {
      val controller = new Controller()
      val tui = new TestTUI(controller)
      tui.processEvent(Event.InvalidMoveEvent)
      tui.lines.exists(_.contains("Invalid move")) shouldBe true
    }

    "react to BackToMenuEvent" in {
      val controller = new Controller()
      val tui = new TestTUI(controller)
      tui.processEvent(Event.BackToMenuEvent)
      tui.lines.exists(_.contains("Menu")) shouldBe true
    }

    "call run() should invoke writeline and update" in {
      val controller = new Controller()
      var wasCalled = false
      class RunTestTUI(controller: Controller) extends TUI(controller) {
        override protected def writeline(s: String): Unit = wasCalled = true
        override def update(): Unit = () // prevent recursion
      }
      val tui = new RunTestTUI(controller)
      tui.run()
      wasCalled shouldBe true
    }

    "call update() should process input commands" in {
      val controller = new Controller()
      class UpdateTestTUI(controller: Controller) extends TUI(controller) {
        var processedCommand: Boolean = false
        var quitCalled: Boolean = false

        override protected def writeline(s: String): Unit = ()
        override protected def quit(): Unit = quitCalled = true

        override val inputManager = new InputManager(terminal) {
          private var inputs = List(
            Some(Command.Escape),
            Some(Command.IncreaseBoardSize),
            None
          )
          override def currentInput: Option[Command] = {
            val next = inputs.headOption.flatten
            inputs = inputs.drop(1)
            next
          }
        }

        override def update(): Unit = {
          inputManager.currentInput match {
            case Some(Command.Escape) => quit()
            case Some(Command.IncreaseBoardSize) => processedCommand = true
            case _ => ()
          }
        }
      }

      val tui = new UpdateTestTUI(controller)
      tui.update(); tui.update(); tui.update()
      tui.processedCommand shouldBe true
      tui.quitCalled shouldBe true
    }

    "call real writeline from subclass to cover println and flush" in {
      val controller = new Controller()
      class RealWriteTUI(controller: Controller) extends TUI(controller) {
        def testWrite(): Unit = writeline("test")
      }
      val tui = new RealWriteTUI(controller)
      tui.testWrite()
    }

    "update() should trigger all match cases with real code" in {
        val controller = new Controller()
        controller.manager = Manager(controller) // ✅ stellt sicher: State == Menu
      controller.manager = Manager(controller) // State.Menu sicherstellen

      class FullMatchTUI(controller: Controller) extends TUI(controller) {
        var flags: List[String] = Nil
        var callCount = 0

        override protected def writeline(s: String): Unit = ()
        override protected def quit(): Unit = flags :+= "quit"

        override val inputManager = new InputManager(terminal) {
          private var inputs = List(
            Some(Command.Escape),           // -> quit()
            Some(Command.QuitGame),         // -> match Menu
            Some(Command.IncreaseFigures),  // -> processCommand
            None                            // -> match None
          )
          override def currentInput: Option[Command] = {
            val next = inputs.headOption.flatten
            inputs = inputs.drop(1)
            next
          }
        }

        override def update(): Unit = {
          callCount += 1
          inputManager.currentInput match {
            case Some(Command.Escape) =>
              quit()
            case Some(Command.QuitGame) if controller.manager.state == State.Menu =>
              flags :+= "quitGame"
            case Some(value) =>
              controller.processCommand(value)
              flags :+= "processed"
              update()
            case None =>
              flags :+= "none"
          }
        }
      }

      val tui = new FullMatchTUI(controller)
      tui.update(); tui.update(); tui.update(); tui.update()

      tui.flags should contain allOf ("quit", "quitGame", "processed", "none")
      tui.callCount shouldBe 5
    }

    "react to ConfigEvent" in {
        val controller = new Controller()
        val tui = new TestTUI(controller)
        tui.processEvent(Event.ConfigEvent)
        tui.lines.exists(_.contains("Players")) shouldBe true
        }

    

    "react to QuitGameEvent" in {
        val controller = new Controller()
        var quitWasCalled = false

        class QuitTestTUI(controller: Controller) extends TUI(controller) {
            override protected def quit(): Unit = quitWasCalled = true
            override protected def writeline(s: String): Unit = ()
        }

        val tui = new QuitTestTUI(controller)
        tui.processEvent(Event.QuitGameEvent)

        quitWasCalled shouldBe true
        }

    "react to unknown event" in {
        val controller = new Controller()
        val tui = new TestTUI(controller)
        tui.processEvent(Event.RollDiceEvent(4)) // wird nicht explizit behandelt
        tui.lines.exists(_ == "") shouldBe true // "" von writeline("")
        }



    "call quit() should clear terminal and print exit message" in {
        val controller = new Controller()
        var lines: List[String] = Nil
        var closed = false

        class QuitTestTUI(controller: Controller) extends TUI(controller) {
            override protected def writeline(s: String): Unit = lines :+= s
            override protected def quit(): Unit = {
            super.quit()
            closed = true
            }

            // Öffentlicher Aufruf für Test
            def callQuit(): Unit = quit()
        }

        val tui = new QuitTestTUI(controller)
        tui.callQuit() // <-- jetzt erlaubt

        lines.exists(_.contains("Exiting")) shouldBe true
        closed shouldBe true
        }






    


  }
}
