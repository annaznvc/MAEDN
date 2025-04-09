package model

class Board:
  val fields: List[Field] = generateFields()

  private def generateFields(): List[Field] =
    (0 until 40).toList.map(i => Field(Position(i, 0), FieldType.Board))

  def getFieldAt(x: Int): Option[Field] =
    fields.find(_.position.x == x)

  def isValidIndex(i: Int): Boolean =
    i >= 0 && i < fields.size

  def allPositions: List[Position] =
    fields.map(_.position)
