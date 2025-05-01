package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Color

enum FieldType:
  case Normal, Start, Goal, Home

// A Field represents a single position on the board
case class Field(
    figure: Option[Figure] = None,
    fieldType: FieldType,
    color: Color
)
