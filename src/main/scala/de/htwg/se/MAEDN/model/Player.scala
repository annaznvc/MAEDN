package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Color

// A Player owns multiple Figures
case class Player(
                   id: Int,
                   figures: List[Figure],
                   color: Color
                 )
