import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model.states.RunningState
import de.htwg.se.MAEDN.model.{Player, Figure, Board, Field, FieldType}
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.util.Event


class RunningStateSpec extends AnyWordSpec with Matchers {

  val controller = new Controller()
  val red = Player(1, Nil, Color.RED)
  val fig = Figure(1, red)
  val redWithFig = red.copy(figures = List(fig))
  val board = Board(Vector(Field(Some(fig), FieldType.Start, Color.RED)), Vector.empty)

  val state = RunningState(controller, moves = 0, board, List(redWithFig), rolled = 0)

  "RunningState" should {

    "roll a dice only if allowed" in {
      val withRoll = state.playDice()
      withRoll.rolled should (be >= 1 and be <= 6)
    }

    "not roll if already rolled" in {
      val alreadyRolled = state.copy(allowedRollDice = false)
      alreadyRolled.playDice().rolled shouldBe 0
    }

    "move to next player on playNext" in {
      val s = state.copy(moves = 0, players = List(redWithFig, redWithFig))
      val next = s.playNext()
      next.getCurrentPlayer shouldBe 1
    }

    "change selected figure with moveUp and moveDown" in {
      val multiFigPlayer = redWithFig.copy(figures = List(Figure(1, red), Figure(2, red), Figure(3, red)))
      val s = state.copy(players = List(multiFigPlayer))

      val up = s.copy(selectedFigure = 0).moveUp()
      up.asInstanceOf[RunningState].selectedFigure shouldBe 1

      val down = s.copy(selectedFigure = 0).moveDown()
      down.asInstanceOf[RunningState].selectedFigure shouldBe 2
    }

    "return to menu on quitGame" in {
      val newState = state.quitGame()
      newState.state.toString shouldBe "Menu"
    }

    "not move a figure if dice not rolled" in {
      val notRolled = state.copy(allowedRollDice = true)
      val result = notRolled.moveFigure()
      result shouldBe notRolled
    }

    "should enqueue MoveFigureEvent when move is valid" in {
        val controller = new Controller()
        val p = Player(1, Nil, Color.RED)
        val f = Figure(1, p)
        val pWithFig = p.copy(figures = List(f))

        val start = Field(Some(f), FieldType.Start, Color.RED)
        val empty = Field(None, FieldType.Normal, Color.WHITE)
        val board = Board(Vector(start, empty), Vector.empty)

        val state = RunningState(controller, 0, board, List(pWithFig), rolled = 1, allowedRollDice = false)

        controller.eventQueue.clear()
        val _ = state.moveFigure()

        val event = controller.eventQueue.dequeueFirst(_ => true)
        event match {
            case Some(Event.MoveFigureEvent(0)) => succeed
            case other => fail(s"Unexpected event: $other")
        }
    }

   "should enqueue RollDiceEvent when rolled was 6" in {
        val controller = new Controller()
        val p = Player(1, Nil, Color.RED)
        val f = Figure(1, p)
        val pWithFig = p.copy(figures = List(f))

        val start = Field(Some(f), FieldType.Start, Color.RED)
        val empty = Field(None, FieldType.Normal, Color.WHITE)
        val board = Board(Vector(start, empty), Vector.empty)

        val state = RunningState(controller, 0, board, List(pWithFig), rolled = 6, allowedRollDice = false)

        controller.eventQueue.clear()
        state.moveFigure()

        val events = controller.eventQueue.dequeueAll(_ => true)
        events should contain (Event.MoveFigureEvent(0))
        events should contain (Event.RollDiceEvent(6))
    }

    "should call playNext if rolled was not 6" in {
        val controller = new Controller()
        val p = Player(1, Nil, Color.RED)
        val f = Figure(1, p)
        val pWithFig = p.copy(figures = List(f))

        val start = Field(Some(f), FieldType.Start, Color.RED)
        val empty = Field(None, FieldType.Normal, Color.WHITE)
        val board = Board(Vector(start, empty), Vector.empty)

        val state = RunningState(controller, 0, board, List(pWithFig), rolled = 2, allowedRollDice = false)

        controller.eventQueue.clear()
        state.moveFigure()

        val events = controller.eventQueue.dequeueAll(_ => true)
        events should contain (Event.MoveFigureEvent(0))
    }
    
  }
}
