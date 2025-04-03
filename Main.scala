@main def run(): Unit =
  val board = Vector(
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

  val output = board.map(_.mkString(" ")).mkString("\n")
  println(output)
