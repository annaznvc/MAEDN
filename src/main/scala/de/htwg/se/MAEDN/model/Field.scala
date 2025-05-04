package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Color

enum FieldType:
  case Normal, Start, Goal, Home

case class Field(
    figure: Option[Figure] = None,
    fieldType: FieldType,
    color: Color
) {
  require(fieldType != null, "fieldType must not be null")

  def isOccupied: Boolean = figure.isDefined
  def isHome: Boolean = fieldType == FieldType.Home
  def isStart: Boolean = fieldType == FieldType.Start
  def isGoal: Boolean = fieldType == FieldType.Goal
}

