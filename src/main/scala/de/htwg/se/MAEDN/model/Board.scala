package de.htwg.se.MAEDN.model


import de.htwg.se.MAEDN.util.{DifficultyLevel, PlayerColor, FieldType}

// The Board contains all the Fields and is built dynamically
class Board(playerCount: Int, difficulty: DifficultyLevel.Value) {

  val fields: List[Field] = createFields()

  // How many normal fields (main track) based on difficulty
  private def fieldsCount: Int = difficulty match {
    case DifficultyLevel.Easy => 20
    case DifficultyLevel.Medium => 40
    case DifficultyLevel.Hard => 60
  }

  // Create all fields: homes, starts, goals, and on-board fields
  private def createFields(): List[Field] = {
    var idCounter = 1
    var fieldList: List[Field] = List()

    // For each player: create 4 Home fields and 4 Goal fields
    for (i <- 0 until playerCount) {
      val playerColor = PlayerColor.values.toList(i)

      // Home Fields
      for (_ <- 1 to 4) {
        fieldList ::= Field(
          id = idCounter,
          fieldType = FieldType.Home,
          ownerColor = Some(playerColor)
        )
        idCounter += 1
      }

      // Goal Fields
      for (_ <- 1 to 4) {
        fieldList ::= Field(
          id = idCounter,
          fieldType = FieldType.Goal,
          ownerColor = Some(playerColor)
        )
        idCounter += 1
      }
    }

    // Shared Start Fields (one for each player)
    for (_ <- 1 to playerCount) {
      fieldList ::= Field(
        id = idCounter,
        fieldType = FieldType.Start,
        ownerColor = None // Start fields do not inherit color
      )
      idCounter += 1
    }

    // Remaining OnBoard fields
    val remainingFields = fieldsCount - (playerCount * (4 + 4 + 1))
    for (_ <- 1 to remainingFields) {
      fieldList ::= Field(
        id = idCounter,
        fieldType = FieldType.OnBoard,
        ownerColor = None
      )
      idCounter += 1
    }

    fieldList.reverse
  }

  // Find a field by ID
  def fieldById(id: Int): Option[Field] = {
    fields.find(_.id == id)
  }
}
