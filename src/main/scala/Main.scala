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


<<<<<<< HEAD
// Kommentar von Layth
//Anna hat keine Ahnung
=======
// Kommentar von Anna #2
//Anna hat keine Ahnung
>>>>>>> aba679e1e2e98c3a83db5417deef309ff7e2f991
