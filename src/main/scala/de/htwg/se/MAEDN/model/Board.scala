package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Color

case class Board(
    fields: Vector[Field],
    homeFields: Vector[Field]
) {

  val size: Int = fields.count(_.fieldType == FieldType.Normal) / 4

  def getField(index: Int): Option[Field] =
    if index > 0 && index <= fields.length then Some(fields(index - 1))
    else None

  def getHomeField(index: Int): Option[Field] =
    if index > 0 && index <= homeFields.length then Some(homeFields(index - 1))
    else None

  def updateField(index: Int, figure: Option[Figure]): Board = {
    val updatedFields = fields.zipWithIndex.map {
      case (field, idx) if idx == (index - 1) => field.copy(figure = figure)
      case (field, _)                         => field
    }
    copy(fields = updatedFields)
  }

  def updateHomeField(index: Int, figure: Option[Figure]): Board = {
    val updatedHomeFields = homeFields.zipWithIndex.map {
      case (field, idx) if idx == (index - 1) => field.copy(figure = figure)
      case (field, _)                         => field
    }
    copy(homeFields = updatedHomeFields)
  }

  def canMoveTo(index: Int): Boolean =
    getField(index).exists(f =>
      f.figure.isEmpty && f.fieldType != FieldType.Goal
    )

  def boardState: String = fields.map(_.toString).mkString("\n")

  // ! Very important method to move a figure on the board
  def moveFigure(figure: Figure, rolled: Int): Board = {
    def isStartFieldOccupied: Boolean = fields.exists(f =>
      f.fieldType == FieldType.Start && f.color == figure.owner.color && f.figure.isDefined
    )

    def findNextIndex(currentIndex: Int, steps: Int): Option[Int] = {
      val nextIndex = (currentIndex + 1) % fields.length
      val currentField = fields(nextIndex)

      if (steps == 0) {
        if (
          currentField.fieldType == FieldType.Goal && currentField.color != figure.owner.color
        ) None
        else Some(nextIndex)
      } else if (
        currentField.fieldType == FieldType.Goal && currentField.color != figure.owner.color
      ) {
        findNextIndex(nextIndex, steps)
      } else {
        findNextIndex(nextIndex, steps - 1)
      }
    }

    homeFields.indexWhere(_.figure.contains(figure)) match {
      case homeIndex if homeIndex != -1 =>
        if (rolled != 6 || isStartFieldOccupied) this
        else {
          val startFieldIndex = fields.indexWhere(f =>
            f.fieldType == FieldType.Start && f.color == figure.owner.color
          )
          updateHomeField(homeIndex + 1, None)
            .updateField(startFieldIndex + 1, Some(figure))
        }

      case _ =>
        fields.indexWhere(_.figure.contains(figure)) match {
          case fieldIndex if fieldIndex != -1 =>
            findNextIndex(fieldIndex, rolled) match {
              case Some(nextIndex)
                  if fields(nextIndex).figure.exists(
                    _.owner.color == figure.owner.color
                  ) =>
                this
              case Some(nextIndex) =>
                updateField(fieldIndex + 1, None)
                  .updateField(nextIndex + 1, Some(figure))
              case None => this
            }
          case _ => this
        }
    }
  }

  override def toString(): String = {
    val fieldStrings = fields
      .map(f => s"(${f.color.toString.head}${f.fieldType.toString.head})")
      .grouped(10)
      .map(_.mkString(" | "))
      .mkString("\n")
    val homeStrings = homeFields
      .map(f => s"(${f.color.toString.head}H)")
      .grouped(4)
      .map(_.mkString(" | "))
      .mkString("\n")
    s"Board:\n$fieldStrings\nHome Fields:\n$homeStrings"
  }
}
