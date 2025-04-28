package de.htwg.se.MAEDN.util

import scala.util.Random

// Dice can roll numbers between 1 and a given number of sides
class Dice(val sides: Int = 6) {
  private val random = new Random()

  // Roll the dice, return a number between 1 and sides
  def roll(): Int = random.nextInt(sides) + 1
}
