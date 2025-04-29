package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.{PlayerColor, FieldType}

class Board(playerCount: Int, boardSize: Int, figuresPerPlayer: Int) {

  val fields: List[Field] = createFields()

  private def createFields(): List[Field] = {
    val required = playerCount * (figuresPerPlayer * 2 + 1) // Home + Goal + Start
    require(boardSize >= required, s"Board too small: need at least $required fields")

    var idCounter = 1
    var fieldList: List[Field] = List()

    for (i <- 0 until playerCount) {
      val color = PlayerColor.values.toList(i)
      for (_ <- 1 to figuresPerPlayer) {
        fieldList ::= Field(idCounter, fieldType = FieldType.Home, ownerColor = Some(color)); idCounter += 1
        fieldList ::= Field(idCounter, fieldType = FieldType.Goal, ownerColor = Some(color)); idCounter += 1
      }
    }

    for (_ <- 1 to playerCount) {
      fieldList ::= Field(idCounter, fieldType = FieldType.Start); idCounter += 1
    }

    val remaining = boardSize - fieldList.length
    for (_ <- 1 to remaining) {
      fieldList ::= Field(idCounter, fieldType = FieldType.OnBoard); idCounter += 1
    }

    fieldList.reverse
  }

  def fieldById(id: Int): Option[Field] = {
    fields.find(_.id == id)
  }
}
