package model

object BoardHelper:
  def generateFields(): List[Field] =
    val indices = (0 until 40).toList
    indices.map(i => Field(Position(i, 0), FieldType.Board))