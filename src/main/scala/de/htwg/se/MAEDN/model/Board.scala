package de.htwg.se.MAEDN.model


import de.htwg.se.MAEDN.util.DifficultyLevel

// The Board contains all the Fields and is built dynamically
class Board(playerCount: Int, difficulty: DifficultyLevel.Value) {

  val fields: List[Field] = createFields()

  // How many normal fields (main track) based on difficulty
  private def fieldsCount: Int = difficulty match {
    case DifficultyLevel.Easy => 20
    case DifficultyLevel.Medium => 40
    case DifficultyLevel.Hard => 60
  }

  // Create the fields list
  private def createFields(): List[Field] = {
    (1 to fieldsCount).map(id => Field(id)).toList
  }

  // Find a field by ID
  def fieldById(id: Int): Option[Field] = {
    fields.find(_.id == id)
  }
}
