package de.htwg.se.MAEDN.model

// A Field represents a single position on the board
case class Field(
                  id: Int,                       // Unique ID for the field
                  var occupiedBy: Option[Figure] = None // Optionally occupied by a Figure
                )