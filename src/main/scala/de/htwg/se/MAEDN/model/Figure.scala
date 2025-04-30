package de.htwg.se.MAEDN.model

// A Figure belongs to a Player and moves across Fields
case class Figure(
                   id: Int,
                    owner: Player
                 )
