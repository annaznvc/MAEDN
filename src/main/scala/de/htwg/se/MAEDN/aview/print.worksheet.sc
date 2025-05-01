import de.htwg.se.MAEDN.aview.TextDisplay
import de.htwg.se.MAEDN.model.BoardFactory

val board = BoardFactory()
  .withFigureCount(4)
  .withBoardSize(10)
  .build()
println(TextDisplay.printBoard(board))
