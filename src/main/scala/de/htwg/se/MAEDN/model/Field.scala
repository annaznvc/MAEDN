package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.{FieldType, PlayerColor}

// A Field represents a single position on the board
case class Field(
                  id: Int,
                  fieldType: FieldType.Value = FieldType.OnBoard,
                  ownerColor: Option[PlayerColor.Value] = None,
                  var occupiedBy: Option[Figure] = None
                )

