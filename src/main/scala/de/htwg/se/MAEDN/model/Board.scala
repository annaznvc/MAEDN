package model

import model._

class Board:
  val width = 11
  val height = 11

  private val xRange = 0 until width
  private val yRange = 0 until height

  val fields: List[Field] = //für jedes x,y paar ein feld erstellen
    xRange.flatMap { x =>
      yRange.map { y =>
        Field(Position(x, y), FieldType.Board)
      }
    }.toList

  def getFieldAt(x: Int, y: Int): Option[Field] =
    fields.find(f => f.position == Position(x, y)) //f ist einzelnes feld in liste, f.position gibt position dieses feldes

  def isValidIndex(x: Int, y: Int): Boolean =
    x >= 0 && x < width && y >= 0 && y < height

  def allPositions: List[Position] = fields.map(_.position)