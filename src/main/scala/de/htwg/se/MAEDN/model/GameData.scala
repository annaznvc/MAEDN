package de.htwg.se.MAEDN.model

case class GameData(
    moves: Int,
    board: Board,
    players: List[Player],
    selectedFigure: Int = 0,
    rolled: Int = 0
)
