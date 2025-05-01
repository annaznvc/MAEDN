package de.htwg.se.MAEDN.util

import scala.util.Random

object Dice {
  private val random = new Random()

  def roll(): Int = {
    random.nextInt(6) + 1
  }

  def rollMultiple(times: Int): List[Int] = {
    (1 to times).map(_ => roll()).toList
  }
}
