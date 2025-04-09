package model

class Board:
  val fields: List[Field] = BoardHelper.generateFields()

  def getFieldAt(x: Int): Option[Field] =
    fields.find(_.position.x == x)

  def isValidIndex(i: Int): Boolean =
    i >= 0 && i < fields.size

  def allPositions: List[Position] =
    fields.map(_.position)