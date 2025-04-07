package model

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
