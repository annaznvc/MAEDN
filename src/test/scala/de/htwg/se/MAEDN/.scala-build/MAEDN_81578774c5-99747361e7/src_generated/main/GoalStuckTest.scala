

final class GoalStuckTest$_ {
def args = GoalStuckTest_sc.args$
def scriptPath = """d:\AIN3\SE\MAEDN\src\test\scala\de\htwg\se\MAEDN\GoalStuckTest.sc"""
/*<script>*/
import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.controller._

// Setup: Blue player with one figure in the goal path at Position(5,7)
val board = new Board()
val blueGoalPath = board.goalPath(Color.Blue)

val b1GoalPos = blueGoalPath(2) // Position(5,7)
val b0GoalBlockerPos = blueGoalPath(3) // Position(5,6)

// Create blocking figure (B0) sitting ahead
val blockingFigure = Figure(0, Color.Blue, Goal(b0GoalBlockerPos))

// Create the figure that should auto-finish (B1)
val stuckFigure = Figure(1, Color.Blue, Goal(b1GoalPos))

// Other Blue figures
val finishedFigure = Figure(2, Color.Blue, Finished)
val atHome = Figure(3, Color.Blue, Home)

// Construct player
val player = Player(1, "anna", Color.Blue, List(blockingFigure, stuckFigure, finishedFigure, atHome))

// Add dummy opponents to avoid empty list issues
val dummyOpponent = Player(2, "bot", Color.Red, List.fill(4)(Figure(0, Color.Red, Home)))

// Set up game and controller
val game = new Game(List(player, dummyOpponent))
val controller = GameController(game)

// Simulate the turn for B1 (figure 1), roll = 2
val updatedPlayer = game.moveFigure(player, 1, 2)

// Print result
updatedPlayer.figures.map(_.state)

/*</script>*/ /*<generated>*//*</generated>*/
}

object GoalStuckTest_sc {
  private var args$opt0 = Option.empty[Array[String]]
  def args$set(args: Array[String]): Unit = {
    args$opt0 = Some(args)
  }
  def args$opt: Option[Array[String]] = args$opt0
  def args$: Array[String] = args$opt.getOrElse {
    sys.error("No arguments passed to this script")
  }

  lazy val script = new GoalStuckTest$_

  def main(args: Array[String]): Unit = {
    args$set(args)
    val _ = script.hashCode() // hashCode to clear scalac warning about pure expression in statement position
  }
}

export GoalStuckTest_sc.script as `GoalStuckTest`

