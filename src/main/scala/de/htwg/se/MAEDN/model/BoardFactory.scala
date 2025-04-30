package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.{FieldType, PlayerColor}

object BoardFactory {

  def initializeGameBoard(playerCount: Int, boardSize: Int, figuresPerPlayer: Int): List[Field] = {
    val totalHome = figuresPerPlayer * playerCount
    val totalGoal = figuresPerPlayer * playerCount
    val totalStart = playerCount
    val totalSpecial = totalHome + totalGoal + totalStart

    require(boardSize >= totalSpecial, s"Board too small. Needs at least $totalSpecial fields.")

    var fields: List[Field] = List()
    var index = 0

    // Home fields
    for (colorIndex <- 0 until playerCount) {
      val color = PlayerColor.values.toList(colorIndex)
      for (_ <- 0 until figuresPerPlayer) {
        fields ::= Field(index, FieldType.Home, Some(color))
        index += 1
      }
    }

    // Goal fields
    for (colorIndex <- 0 until playerCount) {
      val color = PlayerColor.values.toList(colorIndex)
      for (_ <- 0 until figuresPerPlayer) {
        fields ::= Field(index, FieldType.Goal, Some(color))
        index += 1
      }
    }

    // Start fields
    for (_ <- 0 until playerCount) {
      fields ::= Field(index, FieldType.Start)
      index += 1
    }

    // Game fields
    while (index < boardSize) {
      fields ::= Field(index, FieldType.OnBoard)
      index += 1
    }

    fields.reverse
  }
}