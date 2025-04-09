// BoardHelper.scala
package model

object BoardHelper:
  def generateFields(): List[Field] =
    (0 until 40).toList.map(i => Field(Position(i, 0), FieldType.Board))
