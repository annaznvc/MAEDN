package de.htwg.se.MAEDN.model.gameDataImp

import de.htwg.se.MAEDN.model.{IMemento, IManager, Player, Board, Figure}
import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.util.{Serializable, Deserializable, Color}
import play.api.libs.json.{Json, JsValue, JsObject, JsNumber, JsArray}
import scala.xml.{Elem, Node}
import scala.util.{Try, Success, Failure}

case class GameData(
    moves: Int,
    board: Board,
    players: List[Player],
    selectedFigure: Int,
    rolled: Int
) extends IMemento
    with Serializable[GameData] {

  def restoreManager(controller: IController): Try[IManager] = {
    (players.size, selectedFigure, rolled, players.isEmpty) match {
      case (size, _, _, _) if size < 2 || size > 4 =>
        Failure(
          new IllegalArgumentException("Players size must be between 2 and 4")
        )
      case (_, idx, _, _) if idx < 0 || idx > players.size =>
        Failure(
          new IllegalArgumentException("Selected figure index out of bounds")
        )
      case (_, _, r, _) if r < -1 || r > 6 =>
        Failure(
          new IllegalArgumentException("Rolled value must be between -1 and 6")
        )
      case (_, _, _, true) =>
        Failure(new IllegalArgumentException("Players list must not be empty"))
      case _ =>
        Success(
          IManager.createRunningState(
            controller,
            moves,
            board,
            players,
            selectedFigure,
            rolled
          )
        )
    }
  }

  // Implementierung für IMemento
  override def restoreIManager(controller: IController): Try[IManager] =
    restoreManager(controller)

  // Serialization implementations
  override def toJson: JsValue = {
    Json.obj(
      "moves" -> moves,
      "board" -> Json.obj(
        "size" -> board.size
        // Note: Strategies are not serialized as they are injected dependencies
      ),
      "players" -> JsArray(players.map { player =>
        Json.obj(
          "id" -> player.id,
          "color" -> Json.obj(
            "name" -> player.color.toString,
            "offset" -> player.color.offset
          ),
          "figures" -> JsArray(player.figures.map { figure =>
            Json.obj(
              "id" -> figure.id,
              "index" -> figure.index,
              "figureCount" -> figure.figureCount
            )
          })
        )
      }),
      "selectedFigure" -> selectedFigure,
      "rolled" -> rolled
    )
  }

  override def toXml: Elem = {
    <gamedata>
      <moves>{moves}</moves>
      <board>
        <size>{board.size}</size>
      </board>
      <players>
        {
      players.map { player =>
        <player>
            <id>{player.id}</id>            <color>
              <name>{player.color.toString}</name>
              <offset>{player.color.offset}</offset>
            </color>
            <figures>
              {
          player.figures.map { figure =>
            <figure>
                  <id>{figure.id}</id>
                  <index>{figure.index}</index>
                  <figureCount>{figure.figureCount}</figureCount>
                </figure>
          }
        }
            </figures>
          </player>
      }
    }
      </players>
      <selectedFigure>{selectedFigure}</selectedFigure>
      <rolled>{rolled}</rolled>
    </gamedata>
  }
}

object GameData extends Deserializable[GameData] {

  override def fromJson(json: JsValue): Try[GameData] = Try {
    val moves = (json \ "moves").as[Int]
    val boardSize = (json \ "board" \ "size").as[Int]
    val selectedFigure = (json \ "selectedFigure").as[Int]
    val rolled = (json \ "rolled").as[Int]

    // Parse players
    val playersJson = (json \ "players").as[JsArray]
    val players = playersJson.value.map { playerJson =>
      val playerId = (playerJson \ "id").as[Int]
      val colorName = (playerJson \ "color" \ "name").as[String]
      val colorOffset = (playerJson \ "color" \ "offset").as[Int]

      // Reconstruct color from name
      val color = colorName match {
        case "RED"    => Color.RED
        case "BLUE"   => Color.BLUE
        case "GREEN"  => Color.GREEN
        case "YELLOW" => Color.YELLOW
        case "WHITE"  => Color.WHITE
        case _ =>
          throw new IllegalArgumentException(s"Unknown color: $colorName")
      }

      val figuresJson = (playerJson \ "figures").as[JsArray]
      val figures = figuresJson.value.map { figureJson =>
        val figureId = (figureJson \ "id").as[Int]
        val index = (figureJson \ "index").as[Int]
        val figureCount = (figureJson \ "figureCount").as[Int]

        // Create figure with placeholder player (will be fixed below)
        Figure(figureId, null, index, figureCount)
      }.toList

      Player(playerId, figures, color)
    }.toList

    // Fix figure owner references
    val playersWithFixedFigures = players.map { player =>
      val fixedFigures =
        player.figures.map(figure => figure.copy(owner = player))
      player.copy(figures = fixedFigures)
    }

    // Create board with default strategies (they will be injected)
    import de.htwg.se.MAEDN.model.strategy._
    val board = Board(
      boardSize,
      new NormalMoveStrategy(),
      new ToBoardStrategy(),
      new KickFigureStrategy()
    )

    GameData(moves, board, playersWithFixedFigures, selectedFigure, rolled)
  }

  override def fromXml(xml: Elem): Try[GameData] = Try {
    val moves = (xml \ "moves").text.toInt
    val boardSize = (xml \ "board" \ "size").text.toInt
    val selectedFigure = (xml \ "selectedFigure").text.toInt
    val rolled = (xml \ "rolled").text.toInt

    // Parse players
    val playersXml = xml \ "players" \ "player"
    val players = playersXml.map { playerXml =>
      val playerId = (playerXml \ "id").text.toInt
      val colorName = (playerXml \ "color" \ "name").text
      val colorOffset = (playerXml \ "color" \ "offset").text.toInt

      // Reconstruct color from name
      val color = colorName match {
        case "RED"    => Color.RED
        case "BLUE"   => Color.BLUE
        case "GREEN"  => Color.GREEN
        case "YELLOW" => Color.YELLOW
        case "WHITE"  => Color.WHITE
        case _ =>
          throw new IllegalArgumentException(s"Unknown color: $colorName")
      }

      val figuresXml = playerXml \ "figures" \ "figure"
      val figures = figuresXml.map { figureXml =>
        val figureId = (figureXml \ "id").text.toInt
        val index = (figureXml \ "index").text.toInt
        val figureCount = (figureXml \ "figureCount").text.toInt

        // Create figure with placeholder player (will be fixed below)
        Figure(figureId, null, index, figureCount)
      }.toList

      Player(playerId, figures, color)
    }.toList

    // Fix figure owner references
    val playersWithFixedFigures = players.map { player =>
      val fixedFigures =
        player.figures.map(figure => figure.copy(owner = player))
      player.copy(figures = fixedFigures)
    }

    // Create board with default strategies (they will be injected)
    import de.htwg.se.MAEDN.model.strategy._
    val board = Board(
      boardSize,
      new NormalMoveStrategy(),
      new ToBoardStrategy(),
      new KickFigureStrategy()
    )

    GameData(moves, board, playersWithFixedFigures, selectedFigure, rolled)
  }
}
