package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.util.Color

// A Player owns multiple Figures
case class Player(
                   name: String, // Name of the player
                   figures: List[Figure], // List of Figures belonging to the player
                   color: Color
                 )
