package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.{FieldType, PlayerColor}

// A Field represents a single position on the board
case class Field(
                  id: Int,                       // Unique ID for the field
                  var occupiedBy: Option[Figure] = None, // Optionally occupied by a Figure
                    fieldType: FieldType.Value = FieldType.OnBoard, // Type of field (Home, Start, OnBoard, Goal) 
                    ownerColor: Option[PlayerColor.Value] = None    // Color if the field belongs to a player
                )