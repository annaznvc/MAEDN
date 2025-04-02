//Farben
enum Color:
  case Red, Blue, Green, Yellow

//Position aufm Brett
case class Position(x: Int, y: Int)

//versch. Zustände
sealed trait PositionState
case object Home extends PositionState
case class OnBoard(pos: Position) extends PositionState
case object Finished extends PositionState

//eine einzelne Spielfigur
case class Figure(id: Int, color: Color, state: PositionState)

//Spieler mit ID, Name, Farbe und 4 Figuren
case class Player(id: Int, name: String, color: Color, figures: List[Figure])

//Speilfeldtypen
enum FieldType:
  case Start, Goal, Board

//ein einzelnes Feld auf dem Spielbrett
case class Field(position: Position, fieldType: FieldType)


////////

//beispieldaten
val fig1 = Figure(1, Color.Red, Home)
val fig2 = Figure(2, Color.Red, OnBoard(Position(5, 2)))
val fig3 = Figure(3, Color.Red, Finished)
val fig4 = Figure(4, Color.Red, Home)

val player = Player(1, "Alice", Color.Red, List(fig1, fig2, fig3, fig4))

//accesss

//simple daten
fig1.color
player.id
player.figures

//figuren aufm spielfeld
val onBoardFigures = player.figures.filter {
  case Figure(_, _, OnBoard(_)) => true
  case _ => false
}

//figuren zu hause
val atHome = player.figures.filter(_.state == Home)

//wie viele fertig sind
val finishedCount = player.figures.count(_.state == Finished)

//List(Position(x, y)) von Figuren, die auf dem Brett sind
val positions = player.figures.collect {
  case Figure(_, _, OnBoard(pos)) => pos
}

//abfrage z.b ist figur 2 auf dem feld (5,2)?
val isOn52 = player.figures.exists {
  case Figure(2, _, OnBoard(Position(5, 2))) => true
  case _ => false
}






