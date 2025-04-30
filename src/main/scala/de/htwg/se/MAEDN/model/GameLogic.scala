package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.FieldType

object GameLogic {

  def moveFigure(board: Board, figure: Figure, steps: Int): Boolean = {
    if (figure.isFinished) return false

    figure.position match {
      case Some(currentField) =>
        currentField.fieldType match {
          case FieldType.Home => tryLeaveHome(board, figure, currentField, steps)
          case FieldType.Start | FieldType.OnBoard | FieldType.Goal => moveAlongBoard(board, figure, currentField, steps)
          case _ => false
        }

      case None => false
    }
  }

  private def tryLeaveHome(board: Board, figure: Figure, currentField: Field, steps: Int): Boolean = {
    if (steps != 6) return false

    val startFieldOpt = board.fields.find(f =>
      f.fieldType == FieldType.Start && f.occupiedBy.isEmpty
    )

    startFieldOpt match {
      case Some(startField) =>
        currentField.occupiedBy = None
        startField.occupiedBy = Some(figure)
        figure.position = Some(startField)
        true
      case None => false
    }
  }

  private def moveAlongBoard(board: Board, figure: Figure, currentField: Field, steps: Int): Boolean = {
    val newPosition = currentField.id + steps

    if (newPosition > board.fields.length) {
      false // Overshoot
    } else if (newPosition == board.fields.length) {
      currentField.occupiedBy = None
      figure.position = None
      figure.isFinished = true
      true
    } else {
      val newField = board.fieldById(newPosition).get

      newField.occupiedBy.foreach(_.position = None) // Kick opponent

      currentField.occupiedBy = None
      newField.occupiedBy = Some(figure)
      figure.position = Some(newField)
      true
    }
  }
}
