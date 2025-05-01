package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Color

class BoardFactory(
    val normalFieldCount: Int = 8,
    val figureCountPerPlayer: Int = 1,
    val players: List[Player] = Nil
) {

  def withBoardSize(count: Int): BoardFactory = {
    val cappedCount = count.max(8).min(12)
    new BoardFactory(cappedCount, figureCountPerPlayer, players)
  }

  def withFigureCount(count: Int): BoardFactory = {
    val cappedCount = count.max(1)
    new BoardFactory(normalFieldCount, cappedCount, players)
  }

  def withPlayers(players: List[Player]): BoardFactory = {
    new BoardFactory(normalFieldCount, figureCountPerPlayer, players)
  }

  def build(): Board = {
    val totalFields = 4 * normalFieldCount + 4 + 4 * figureCountPerPlayer
    val (fields, homeFields) = generateFields(totalFields)
    Board(fields, homeFields)
  }

  private def generateFields(
      totalFields: Int
  ): (Vector[Field], Vector[Field]) = {
    val colors = List(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW)

    val startFields =
      (0 until 4).map(i => Field(None, FieldType.Start, colors(i))).toList

    val normalFields = (1 to 4 * normalFieldCount)
      .map(_ => Field(None, FieldType.Normal, Color.WHITE))
      .toList

    val goalFields = (0 until 4 * figureCountPerPlayer)
      .map(idx => {
        val playerIndex = idx / figureCountPerPlayer
        Field(None, FieldType.Goal, colors(playerIndex))
      })
      .toList

    val playerGoalFields = (0 until 4).map(i =>
      goalFields.slice(i * figureCountPerPlayer, (i + 1) * figureCountPerPlayer)
    )

    val fieldsList = (0 until 4)
      .flatMap(i =>
        startFields.slice(i, i + 1) ++
          normalFields
            .slice(i * normalFieldCount, (i + 1) * normalFieldCount) ++
          playerGoalFields(i)
      )
      .toList

    val homeFields = players.flatMap { player =>
      (1 to figureCountPerPlayer).map(i =>
        Field(Some(Figure(i, player)), FieldType.Home, player.color)
      )
    }

    (fieldsList.toVector, homeFields.toVector)
  }
}

object BoardFactory {
  def apply(): BoardFactory = new BoardFactory()
}
