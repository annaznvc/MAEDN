package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.model._

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

  // 🆕 Start positions for each player (you can adjust as needed)
  def startPosition(color: Color): Position = color match
    case Color.Red    => Position(0, 5)
    case Color.Blue   => Position(5, 0)
    case Color.Green  => Position(10, 5)
    case Color.Yellow => Position(5, 10)

  // 🆕 Goal entry positions (just before entering goal path)
  def goalEntryPosition(color: Color): Position = color match
    case Color.Red    => Position(1, 5)
    case Color.Blue   => Position(5, 1)
    case Color.Green  => Position(9, 5)
    case Color.Yellow => Position(5, 9)

  def goalPath(color: Color): List[Position] = color match
    case Color.Red    => List(Position(1, 5), Position(2, 5), Position(3, 5), Position(4, 5))
    case Color.Blue   => List(Position(5, 1), Position(5, 2), Position(5, 3), Position(5, 4))
    case Color.Green  => List(Position(9, 5), Position(8, 5), Position(7, 5), Position(6, 5))
    case Color.Yellow => List(Position(5, 9), Position(5, 8), Position(5, 7), Position(5, 6))


  def boardPath: List[Position] = List(
  Position(4, 0), Position(5, 0),
  Position(5, 1), Position(5, 2), Position(5, 3), Position(5, 4),
  Position(6, 4), Position(7, 5), Position(8, 5), Position(9, 5), Position(10, 5),
  Position(10, 6),
  Position(9, 6), Position(8, 6), Position(7, 6), Position(6, 6),
  Position(6, 7), Position(6, 8), Position(6, 9), Position(6, 10),
  Position(5, 10), Position(4, 10),
  Position(4, 9), Position(4, 8), Position(4, 7), Position(4, 6),
  Position(3, 6), Position(2, 6), Position(1, 6), Position(0, 6),
  Position(0, 5),
  Position(1, 5), Position(2, 5), Position(3, 5), Position(4, 5)
)


