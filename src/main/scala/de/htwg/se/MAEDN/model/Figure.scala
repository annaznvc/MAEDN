package de.htwg.se.MAEDN.model

// A Figure belongs to a Player and moves across Fields
case class Figure(
                   player: Player,                    // Owner of this figure
                   val id: Int,                       // Unique ID among the player's figures
                   var position: Option[Field] = None, // Current position (None = in start area)
                   var isFinished: Boolean = false     // True if the figure reached the end
                 )
