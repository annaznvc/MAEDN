package de.htwg.se.MAEDN.model.PlayerImp

import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.model.{IPlayer, IFigure}

// A Player owns multiple Figures
case class Player(
    id: Int,
    var figures: List[IFigure],
    color: Color
) extends IPlayer {
  override def startPosition(size: Int): Int = color.offset * size
}
