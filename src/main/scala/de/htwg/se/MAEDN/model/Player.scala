package de.htwg.se.MAEDN.model

case class Player(
  id: Int,
  name: String,
  color: Color,
  figures: List[Figure],
  status: PlayerStatus = Active
):
  require(name.nonEmpty, "Name must not be empty")
  require(figures.size == 4, "Player must have exactly 4 figures")
  require(figures.forall(_.color == color), "All figures must match the player's color")

  def figureById(figureId: Int): Option[Figure] =
    figures.find(_.id == figureId)

  // 🆕 New helper: get all figures that are OnBoard
  def figuresOnBoard: List[Figure] =
    figures.filter(_.isOnBoard)

  // 🆕 New helper: get all figures that are in Goal
  def figuresInGoal: List[Figure] =
    figures.filter(_.isInGoal)

  // 🆕 New helper: check if player has a figure at a given position
  def hasFigureAt(pos: Position): Boolean =
    figures.exists {
      case Figure(_, _, OnBoard(p)) if p == pos => true
      case Figure(_, _, Goal(p)) if p == pos => true
      case _ => false
    }

  // 🆕 New helper: get a figure at a given position (if any)
  def figureAt(pos: Position): Option[Figure] =
    figures.find {
      case Figure(_, _, OnBoard(p)) if p == pos => true
      case Figure(_, _, Goal(p)) if p == pos => true
      case _ => false
    }