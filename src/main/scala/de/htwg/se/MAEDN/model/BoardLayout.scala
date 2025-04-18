package de.htwg.se.MAEDN.model

object BoardLayout:
  def layout: Vector[Vector[String]] = Vector(
    Vector("RR", "RR", "  ", "  ", "..", "..", "BB", "  ", "  ", "BB", "BB"),
    Vector("RR", "RR", "  ", "  ", "..", "BB", "..", "  ", "  ", "BB", "BB"),
    Vector("  ", "  ", "  ", "  ", "..", "BB", "..", "  ", "  ", "  ", "  "),
    Vector("  ", "  ", "  ", "  ", "..", "BB", "..", "  ", "  ", "  ", "  "),
    Vector("RR", "..", "..", "..", "..", "BB", "..", "..", "..", "..", ".."),
    Vector("..", "RR", "RR", "RR", "RR", "**", "GG", "GG", "GG", "GG", ".."),
    Vector("..", "..", "..", "..", "..", "YY", "..", "..", "..", "..", "GG"),
    Vector("  ", "  ", "  ", "  ", "..", "YY", "..", "  ", "  ", "  ", "  "),
    Vector("  ", "  ", "  ", "  ", "..", "YY", "..", "  ", "  ", "  ", "  "),
    Vector("YY", "YY", "  ", "  ", "..", "YY", "..", "  ", "  ", "GG", "GG"),
    Vector("YY", "YY", "  ", "  ", "YY", "..", "..", "  ", "  ", "GG", "GG")
  )
