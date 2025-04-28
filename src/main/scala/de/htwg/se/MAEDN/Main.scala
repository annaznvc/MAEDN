import de.htwg.se.MAEDN.aview.TUI
import de.htwg.se.MAEDN.controller.GameController
import de.htwg.se.MAEDN.util.DifficultyLevel

object Main extends App {
  val controller = new GameController(List("Alice", "Bob"), DifficultyLevel.Medium)
  val tui = new TUI(controller)
  tui.run()
}
