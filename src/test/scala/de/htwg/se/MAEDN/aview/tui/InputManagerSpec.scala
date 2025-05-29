package de.htwg.se.MAEDN.aview.tui

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.jline.terminal.Terminal
import org.jline.terminal.impl.DumbTerminal
import org.jline.keymap.{BindingReader, KeyMap}
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.nio.charset.StandardCharsets
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.controller.command._
import de.htwg.se.MAEDN.util.{Event, Observer}
import de.htwg.se.MAEDN.model.Board
import de.htwg.se.MAEDN.aview.tui.InputManager
import de.htwg.se.MAEDN.aview.tui.TextDisplay

class InputManagerSpec extends AnyWordSpec with Matchers {

  // Test-Controller mit minimalen Implementierungen
  class TestController extends Controller {
    var lastCommand: Option[Command] = None

    override def executeCommand(command: Command): Unit = {
      lastCommand = Some(command)
      notifyObservers() // Muss aufgerufen werden, damit Manager gesetzt wird
    }
  }

  // Mock-Terminal mit konfigurierbarem Input-Stream
  def createMockTerminal(input: String): Terminal = {
    val inputStream = new ByteArrayInputStream(input.getBytes)
    val outputStream = new ByteArrayOutputStream()
    new DumbTerminal(
      "mock",
      "xterm",
      inputStream,
      outputStream,
      StandardCharsets.UTF_8
    )
  }

  "An InputManager" should {

    "bind PlayNextCommand to 'x'" in {
      val controller = new TestController()
      val terminal = createMockTerminal("x")
      val inputManager = new InputManager(controller, terminal)

      val result = inputManager.currentInput
      result.isDefined should be(true)
      result.get should be(a[PlayNextCommand])
    }

    "bind MoveUpCommand to 'w'" in {
      val controller = new TestController()
      val terminal = createMockTerminal("w")
      val inputManager = new InputManager(controller, terminal)

      val result = inputManager.currentInput
      result.isDefined should be(true)
      result.get should be(a[MoveUpCommand])
    }

    "bind MoveDownCommand to 's'" in {
      val controller = new TestController()
      val terminal = createMockTerminal("s")
      val inputManager = new InputManager(controller, terminal)

      val result = inputManager.currentInput
      result.isDefined should be(true)
      result.get should be(a[MoveDownCommand])
    }

    "bind IncreaseFiguresCommand to 'e'" in {
      val controller = new TestController()
      val terminal = createMockTerminal("e")
      val inputManager = new InputManager(controller, terminal)

      val result = inputManager.currentInput
      result.isDefined should be(true)
      result.get should be(a[IncreaseFiguresCommand])
    }

    "bind DecreaseFiguresCommand to 'd'" in {
      val controller = new TestController()
      val terminal = createMockTerminal("d")
      val inputManager = new InputManager(controller, terminal)

      val result = inputManager.currentInput
      result.isDefined should be(true)
      result.get should be(a[DecreaseFiguresCommand])
    }

    "bind IncreaseBoardSizeCommand to 'r'" in {
      val controller = new TestController()
      val terminal = createMockTerminal("r")
      val inputManager = new InputManager(controller, terminal)

      val result = inputManager.currentInput
      result.isDefined should be(true)
      result.get should be(a[IncreaseBoardSizeCommand])
    }

    "bind DecreaseBoardSizeCommand to 'f'" in {
      val controller = new TestController()
      val terminal = createMockTerminal("f")
      val inputManager = new InputManager(controller, terminal)

      val result = inputManager.currentInput
      result.isDefined should be(true)
      result.get should be(a[DecreaseBoardSizeCommand])
    }

    "bind QuitGameCommand to 'q'" in {
      val controller = new TestController()
      val terminal = createMockTerminal("q")
      val inputManager = new InputManager(controller, terminal)

      val result = inputManager.currentInput
      result.isDefined should be(true)
      result.get should be(a[QuitGameCommand])
    }

    "bind StartGameCommand to 'n'" in {
      val controller = new TestController()
      val terminal = createMockTerminal("n")
      val inputManager = new InputManager(controller, terminal)

      val result = inputManager.currentInput
      result.isDefined should be(true)
      result.get should be(a[StartGameCommand])
    }

    "bind UndoCommand to 'u'" in {
      val controller = new TestController()
      val terminal = createMockTerminal("u")
      val inputManager = new InputManager(controller, terminal)

      val result = inputManager.currentInput
      result.isDefined should be(true)
      result.get should be(a[UndoCommand])
    }

    "bind RedoCommand to 'i'" in {
      val controller = new TestController()
      val terminal = createMockTerminal("i")
      val inputManager = new InputManager(controller, terminal)

      val result = inputManager.currentInput
      result.isDefined should be(true)
      result.get should be(a[RedoCommand])
    }

    "return None for unbound key" in {
      val controller = new TestController()
      val terminal = createMockTerminal("z") // z ist nicht gebunden
      val inputManager = new InputManager(controller, terminal)

      val result = inputManager.currentInput
      result should be(None)
    }

    "detect ESC key with isEscape method" in {
      val controller = new TestController()

      // Wir erstellen ein spezielles Mock-Terminal für ESC-Tests
      val escByte = 27 // ASCII für ESC
      val escInput = new ByteArrayInputStream(Array[Byte](escByte.toByte))
      val outputStream = new ByteArrayOutputStream()

      val mockTerminal = new DumbTerminal(
        "mock",
        "xterm",
        escInput,
        outputStream,
        StandardCharsets.UTF_8
      ) {
        // Überschreiben der input()-Methode statt reader()
        override def input(): java.io.InputStream =
          new ByteArrayInputStream(Array[Byte](escByte.toByte)) {
            override def read(): Int = escByte
          }
      }

      val inputManager = new InputManager(controller, mockTerminal)
      inputManager.isEscape should be(true)
    }

    "have controller accessible as property" in {
      val controller = new TestController()
      val terminal = createMockTerminal("")
      val inputManager = new InputManager(controller, terminal)

      inputManager.controller should be(controller)
    }

    "have terminal accessible as property" in {
      val controller = new TestController()
      val terminal = createMockTerminal("")
      val inputManager = new InputManager(controller, terminal)

      inputManager.terminal should be(terminal)
    }

    "execute else content by rendering 'N ' for a neutral field" in {
      val board2 = Board(4)
      val result = TextDisplay.printBoard(
        board = board2,
        selectedFigure = -1,
        currentPlayerIndex = -1,
        players = Nil // keine Spieler → keine Startfelder, keine Figuren
      )

      assert(result.contains("N "))
    }

  }
}
