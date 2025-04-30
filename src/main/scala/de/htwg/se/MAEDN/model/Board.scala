package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.{FieldType, Color}

class Board(val fields: Vector[Field]) {

  def getField(index: Int): Option[Field] = {
    if (index > 0 && index <= fields.length) Some(fields(index - 1)) else None
  }

  def updateField(index: Int, figure: Option[Figure]): Board = {
    val updatedFields = fields.zipWithIndex.map {
      case (field, idx) if idx == (index - 1) => field.copy(figure = figure)
      case (field, _) => field
    }
    new Board(updatedFields)
  }

  def canMoveTo(index: Int): Boolean = {
    getField(index).exists(f => f.figure.isEmpty && f.fieldType != FieldType.Goal)
  }

  def boardState: String = fields.map(_.toString).mkString("\n")

  override def toString(): String = {
    val fieldStrings = fields.map(f => s"(${f.color.toString.head}${f.fieldType.toString.head})")
      .grouped(10)
      .map(_.mkString(" | "))
      .mkString("\n")
    s"Board:\n$fieldStrings"
  }
}

object Board {

  def defaultBoard(normalFieldCount: Int, figureCountPerPlayer: Int): Board = {
    require(figureCountPerPlayer >= 1, "Figure count per player must be at least 1")
    val totalFields = 4 * normalFieldCount + 4 + 4 * figureCountPerPlayer
    val fields = generateFields(totalFields, normalFieldCount, figureCountPerPlayer)
    new Board(fields)
  }

  def generateFields(totalFields: Int, normalFieldCount: Int, figureCountPerPlayer: Int): Vector[Field] = {
    val colors = List(Color.Red, Color.Blue, Color.Green, Color.Yellow)

    val startFields = (1 to 4).map { idx =>
      Field(None, FieldType.Start, colors(idx - 1))
    }.toList

    val normalFields = (1 to (4 * normalFieldCount)).map { _ =>
      Field(None, FieldType.Normal, Color.White)
    }.toList

    val goalFields = (1 to (4 * figureCountPerPlayer)).map { idx =>
      Field(None, FieldType.Goal, colors(((((idx - 1) / 4) + 1) % 4)))
    }.toList

    val playerGoalFields = (1 to 4).map { idx =>
      goalFields.slice((idx - 1) * figureCountPerPlayer, idx * figureCountPerPlayer)
    }.toList

    val fieldsList = (0 until 4).flatMap { i =>
      startFields.slice(i, i + 1) ++
      normalFields.slice(i * normalFieldCount, (i + 1) * normalFieldCount) ++
      playerGoalFields(i)
    }.toList

    require(fieldsList.size == totalFields, s"Expected $totalFields fields, but got ${fieldsList.size}")
    fieldsList.toVector
  }
}
