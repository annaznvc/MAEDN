package model

import model._

class Board:
  val width = 8
  val height = 5

  private val xRange = 0 until width
  private val yRange = 0 until height

  val fields: List[Field] =
    xRange.flatMap { x =>
      yRange.map { y =>
        Field(Position(x, y), FieldType.Board)
      }
    }.toList

  def getFieldAt(x: Int, y: Int): Option[Field] =
    fields.find(f => f.position == Position(x, y))

  def isValidIndex(x: Int, y: Int): Boolean =
    x >= 0 && x < width && y >= 0 && y < height

  def allPositions: List[Position] = fields.map(_.position)
