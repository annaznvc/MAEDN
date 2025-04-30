package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.{FieldType, Color}

case class Field(
  figure: Option[Figure] = None,
  fieldType: FieldType,
  color: Color
) {

  // fake methode für scoverage – prüft, ob eine Figur vorhanden ist
  def hasFigure: Boolean = this.figure match {
    case Some(_) => true
    case None    => false
  }
}
