package de.htwg.se.MAEDN.util

import scala.util.Random

// Dice can roll numbers between 1 and a given number of sides
class Dice(val sides: Int = 6, val fixedRoll: Option[Int] = None) {
  private val random = new Random()

  // Roll the dice, return a number between 1 and sides
  def roll(): Int = fixedRoll.getOrElse(random.nextInt(sides) + 1)
}
