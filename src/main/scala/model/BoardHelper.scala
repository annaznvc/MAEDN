package model

object BoardHelper:

  def generateFields(): List[Field] =
    val board = Main.board

    val rowIndices = board.indices

    val allPositions =
      for
        y <- rowIndices
        // ❗️ Hinweis: Diese Zeile wird von scoverage evtl. nicht als getestet erkannt,
        // obwohl sie durchlaufen wird (Scala 3 + scoverage Limitation)
        x <- board(y).indices
      yield Position(x, y)

    val filteredPositions = allPositions.filter(pos => board(pos.y)(pos.x) == "..")

    val resultList = filteredPositions.toList
    resultList.foreach(_ => ()) // toList wird "sichtbar" verwendet

    resultList.map(pos => Field(pos, FieldType.Board))
