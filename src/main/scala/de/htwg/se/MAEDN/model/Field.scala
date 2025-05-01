package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Color

enum FieldType:
  case Normal, Start, Goal, Home

case class Field(
    figure: Option[Figure] = None,
    fieldType: FieldType,
    color: Color
)
