package util 

object Dice:
  def roll(): Int = scala.util.Random.between(1, 7)
