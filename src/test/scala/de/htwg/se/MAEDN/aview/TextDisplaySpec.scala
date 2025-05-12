import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.aview.TextDisplay
import de.htwg.se.MAEDN.model.{
  Board,
  Field,
  FieldType,
  Player,
  Figure,
  IState,
  State
}
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.model.Manager
import de.htwg.se.MAEDN.model.BoardFactory

class TextDisplaySpec extends AnyWordSpec with Matchers {

  "TextDisplay" should {

    "clear the terminal with ANSI code" in {
      TextDisplay.clearTerminal() should include("\u001b[2J")
    }

    "print cover for Config state" in {
      val dummy = new de.htwg.se.MAEDN.model.Manager {
        override val controller = null
        override val moves = 7
        override val rolled = 0
        override val state = de.htwg.se.MAEDN.model.State.Config
        override val board =
          de.htwg.se.MAEDN.model.Board(Vector.empty, Vector.empty)
        override val players = List.empty

        override def getPlayerCount = 3
        override def getFigureCount = 1
        override def getBoardSize = 9
        override def getCurrentPlayer = 0

        override def startGame() = this
        override def quitGame() = this
        override def moveUp() = this
        override def moveDown() = this
        override def increaseFigures() = this
        override def decreaseFigures() = this
        override def increaseBoardSize() = this
        override def decreaseBoardSize() = this
        override def playDice() = this
        override def playNext() = this
        override def moveFigure() = this
      }

      val output = de.htwg.se.MAEDN.aview.TextDisplay.printCover(dummy)
      output should include("Config")
      output should include("3 players")
      output should include("9x9 board")
    }

    "print cover for Menu state" in {
      val dummy = new de.htwg.se.MAEDN.model.Manager {
        override val controller = null
        override val moves = 5
        override val rolled = 0
        override val state = de.htwg.se.MAEDN.model.State.Menu
        override val board =
          de.htwg.se.MAEDN.model.Board(Vector.empty, Vector.empty)
        override val players = List.empty

        override def getPlayerCount = 2
        override def getFigureCount = 1
        override def getBoardSize = 8
        override def getCurrentPlayer = 0

        override def startGame() = this
        override def quitGame() = this
        override def moveUp() = this
        override def moveDown() = this
        override def increaseFigures() = this
        override def decreaseFigures() = this
        override def increaseBoardSize() = this
        override def decreaseBoardSize() = this
        override def playDice() = this
        override def playNext() = this
        override def moveFigure() = this
      }

      val output = de.htwg.se.MAEDN.aview.TextDisplay.printCover(dummy)
      output should include("Menu")
      output should include("2 players")
      output should include("8x8 board")
    }

    "print cover for Running state" in {
      val dummy = new de.htwg.se.MAEDN.model.Manager {
        override val controller = null
        override val moves = 10
        override val rolled = 0
        override val state = de.htwg.se.MAEDN.model.State.Running
        override val board =
          de.htwg.se.MAEDN.model.Board(Vector.empty, Vector.empty)
        override val players = List.empty

        override def getPlayerCount = 4
        override def getFigureCount = 1
        override def getBoardSize = 11
        override def getCurrentPlayer = 0

        override def startGame() = this
        override def quitGame() = this
        override def moveUp() = this
        override def moveDown() = this
        override def increaseFigures() = this
        override def decreaseFigures() = this
        override def increaseBoardSize() = this
        override def decreaseBoardSize() = this
        override def playDice() = this
        override def playNext() = this
        override def moveFigure() = this
      }

      val output = de.htwg.se.MAEDN.aview.TextDisplay.printCover(dummy)
      output should include("Running")
      output should include("4 players")
      output should include("11x11 board")
    }

    //

    "print config layout" in {
      val output = TextDisplay.printConfig(2, 2, 8)
      output should include("Press [Space] to start a new game")
      output should include("2")
    }

    "print board with fields and homes" in {
      val p = Player(1, List(Figure(1, null)), Color.RED)
      val home = Field(Some(p.figures.head), FieldType.Home, Color.RED)
      val field = Field(None, FieldType.Normal, Color.WHITE)
      val board = Board(Vector.fill(4)(field), Vector.fill(4)(home))

      val output = TextDisplay.printBoard(board)
      output should include("Home Fields")
      output should include("Main Board")
      output should include("Red:")
      output should include("F1") // die Figur-ID
    }

    "render all field colors via colorCode in printBoard" in {
      val red = Player(1, List(Figure(1, null)), Color.RED)
      val blue = Player(2, List(Figure(1, null)), Color.BLUE)
      val green = Player(3, List(Figure(1, null)), Color.GREEN)
      val yellow = Player(4, List(Figure(1, null)), Color.YELLOW)

      val redField = Field(None, FieldType.Normal, Color.RED)
      val blueField = Field(None, FieldType.Normal, Color.BLUE)
      val greenField = Field(None, FieldType.Normal, Color.GREEN)
      val yellowField = Field(None, FieldType.Normal, Color.YELLOW)

      val board = Board(
        fields = Vector(redField, blueField, greenField, yellowField),
        homeFields = Vector.empty
      )

      val output = TextDisplay.printBoard(board)

      output should include("Red")
      output should include("Blue")
      output should include("Green")
      output should include("Yellow")
    }

    "render all FieldTypes in printBoard" in {
      val homeFields = Vector(
        Field(None, FieldType.Home, Color.RED),
        Field(None, FieldType.Home, Color.BLUE),
        Field(None, FieldType.Home, Color.GREEN),
        Field(None, FieldType.Home, Color.YELLOW)
      )

      val startField = Field(None, FieldType.Start, Color.RED)
      val goalField = Field(None, FieldType.Goal, Color.RED)

      val board = Board(
        fields = Vector(startField, goalField),
        homeFields = homeFields
      )

      val output = TextDisplay.printBoard(board)

      output should include("H ")
      output should include("S ")
      output should include("G ")
    }

    "render fallback player labels beyond labels list" in {
      val homeFields = Vector.fill(5)(Field(None, FieldType.Home, Color.RED))
      val board = Board(Vector.empty, homeFields)

      val output = TextDisplay.printBoard(board)

      output should include("Player5")
    }

  }

}
