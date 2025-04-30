package de.htwg.se.MAEDN.model

class Board(playerCount: Int, boardSize: Int, figuresPerPlayer: Int) {
  val fields: List[Field] = BoardFactory.initializeGameBoard(playerCount, boardSize, figuresPerPlayer)

  def fieldById(id: Int): Option[Field] = fields.find(_.id == id)
}
