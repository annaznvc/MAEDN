package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.controller.controllerImp.Controller
import de.htwg.se.MAEDN.util.Color
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.MAEDN.model.gameDataImp.GameData
import play.api.libs.json.{Json, JsValue}
import scala.xml.{Elem, XML}
import scala.util.{Try, Success, Failure}

class GameDataSpec extends AnyWordSpec with Matchers {

  def sampleBoard: Board = Board(8)
  def samplePlayer: Player = {
    val p = Player(1, Nil, Color.RED)
    val figs = List.tabulate(4)(i => Figure(i + 1, p, i, 4))
    p.copy(figures = figs)
  }
  def samplePlayers(n: Int): List[Player] = List.fill(n)(samplePlayer)
  val controller = new Controller

  def samplePlayersWithDifferentColors: List[Player] = {
    val colors = List(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW)
    colors.zipWithIndex.map { case (color, idx) =>
      val p = Player(idx + 1, Nil, color)
      val figs = List.tabulate(4)(i => Figure(i + 1, p, i * 10 + idx, 4))
      p.copy(figures = figs)
    }
  }

  //  Fake-List, die isEmpty true liefert, aber size >= 2 vortäuscht
  def fakeEmptyPlayers: List[Player] =
    LazyList.empty[Player].toList

  "GameData" should {

    "restore a valid RunningState" in {
      val gd = GameData(3, sampleBoard, samplePlayers(2), 0, 6)
      val result = gd.restoreManager(controller)
      result.isSuccess shouldBe true
      result.get shouldBe a[IManager]
    }

    "fail if players.size < 2" in {
      val gd = GameData(0, sampleBoard, samplePlayers(1), 0, 1)
      val result = gd.restoreManager(controller)
      result.isFailure shouldBe true
      result.failed.get.getMessage should include("Players size")
    }

    "fail if players.size > 4" in {
      val gd = GameData(0, sampleBoard, samplePlayers(5), 0, 1)
      val result = gd.restoreManager(controller)
      result.isFailure shouldBe true
      result.failed.get.getMessage should include("Players size")
    }

    "fail if selectedFigure index is negative" in {
      val gd = GameData(0, sampleBoard, samplePlayers(2), -1, 1)
      val result = gd.restoreManager(controller)
      result.isFailure shouldBe true
      result.failed.get.getMessage should include("Selected figure index")
    }

    "fail if selectedFigure index is too large" in {
      val gd = GameData(0, sampleBoard, samplePlayers(2), 3, 1)
      val result = gd.restoreManager(controller)
      result.isFailure shouldBe true
      result.failed.get.getMessage should include("Selected figure index")
    }

    "fail if rolled is < -1" in {
      val gd = GameData(0, sampleBoard, samplePlayers(2), 0, -2)
      val result = gd.restoreManager(controller)
      result.isFailure shouldBe true
      result.failed.get.getMessage should include("Rolled value")
    }

    "fail if players list is empty" in {
      val gd = GameData(0, sampleBoard, fakeEmptyPlayers, 0, 1)
      val result = gd.restoreManager(controller)
      result.isFailure shouldBe true
      result.failed.get.getMessage should include(
        "Players size must be between 2 and 4"
      )
    }

    "fail if rolled is > 6" in {
      val gd = GameData(0, sampleBoard, samplePlayers(2), 0, 7)
      val result = gd.restoreManager(controller)
      result.isFailure shouldBe true
      result.failed.get.getMessage should include("Rolled value")
    }

    "call restoreIManager which delegates to restoreManager" in {
      val gd = GameData(3, sampleBoard, samplePlayers(2), 0, 6)
      val result1 = gd.restoreManager(controller)
      val result2 = gd.restoreIManager(controller)

      result1.isSuccess shouldBe true
      result2.isSuccess shouldBe true
      // Both should return the same type of manager
      result1.get.getClass shouldBe result2.get.getClass
    }

    "serialize to JSON correctly" in {
      val players = samplePlayersWithDifferentColors.take(2)
      val gd = GameData(5, sampleBoard, players, 1, 4)
      val json = gd.toJson

      (json \ "moves").as[Int] shouldBe 5
      (json \ "board" \ "size").as[Int] shouldBe 8
      (json \ "selectedFigure").as[Int] shouldBe 1
      (json \ "rolled").as[Int] shouldBe 4

      val playersJson = (json \ "players").as[List[JsValue]]
      playersJson should have size 2

      val firstPlayer = playersJson.head
      (firstPlayer \ "id").as[Int] shouldBe 1
      (firstPlayer \ "color" \ "name").as[String] shouldBe "RED"
      (firstPlayer \ "color" \ "offset").as[Int] shouldBe Color.RED.offset

      val figuresJson = (firstPlayer \ "figures").as[List[JsValue]]
      figuresJson should have size 4

      val firstFigure = figuresJson.head
      (firstFigure \ "id").as[Int] shouldBe 1
      (firstFigure \ "index").as[Int] shouldBe 0
      (firstFigure \ "figureCount").as[Int] shouldBe 4
    }

    "serialize to XML correctly" in {
      val players = samplePlayersWithDifferentColors.take(3)
      val gd = GameData(7, sampleBoard, players, 2, 3)
      val xml = gd.toXml

      (xml \ "moves").text.toInt shouldBe 7
      (xml \ "board" \ "size").text.toInt shouldBe 8
      (xml \ "selectedFigure").text.toInt shouldBe 2
      (xml \ "rolled").text.toInt shouldBe 3

      val playersXml = xml \ "players" \ "player"
      playersXml should have size 3

      val firstPlayer = playersXml.head
      (firstPlayer \ "id").text.toInt shouldBe 1
      (firstPlayer \ "color" \ "name").text shouldBe "RED"
      (firstPlayer \ "color" \ "offset").text.toInt shouldBe Color.RED.offset

      val figuresXml = firstPlayer \ "figures" \ "figure"
      figuresXml should have size 4

      val firstFigure = figuresXml.head
      (firstFigure \ "id").text.toInt shouldBe 1
      (firstFigure \ "index").text.toInt shouldBe 0
      (firstFigure \ "figureCount").text.toInt shouldBe 4
    }

    "deserialize from JSON with all color types" in {
      val colors =
        List(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.WHITE)
      colors.foreach { color =>
        val player = Player(1, List(Figure(1, null, 0, 4)), color)
        val playerWithFixedFigure =
          player.copy(figures = player.figures.map(_.copy(owner = player)))
        val gd = GameData(
          1,
          sampleBoard,
          List(playerWithFixedFigure, samplePlayer),
          0,
          1
        )
        val json = gd.toJson

        val result = GameData.fromJson(json)
        result.isSuccess shouldBe true
        result.get.players.head.color shouldBe color
      }
    }

    "fail to deserialize from JSON with unknown color" in {
      val json = Json.obj(
        "moves" -> 1,
        "board" -> Json.obj("size" -> 8),
        "players" -> Json.arr(
          Json.obj(
            "id" -> 1,
            "color" -> Json.obj(
              "name" -> "PURPLE",
              "offset" -> 0
            ),
            "figures" -> Json.arr()
          )
        ),
        "selectedFigure" -> 0,
        "rolled" -> 1
      )

      val result = GameData.fromJson(json)
      result.isFailure shouldBe true
      result.failed.get.getMessage should include("Unknown color: PURPLE")
    }

    "deserialize from XML with all color types" in {
      val colors =
        List(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.WHITE)
      colors.foreach { color =>
        val player = Player(1, List(Figure(1, null, 0, 4)), color)
        val playerWithFixedFigure =
          player.copy(figures = player.figures.map(_.copy(owner = player)))
        val gd = GameData(
          1,
          sampleBoard,
          List(playerWithFixedFigure, samplePlayer),
          0,
          1
        )
        val xml = gd.toXml

        val result = GameData.fromXml(xml)
        result.isSuccess shouldBe true
        result.get.players.head.color shouldBe color
      }
    }

    "fail to deserialize from XML with unknown color" in {
      val xml =
        <gamedata>
          <moves>1</moves>
          <board>
            <size>8</size>
          </board>
          <players>
            <player>
              <id>1</id>
              <color>
                <name>PURPLE</name>
                <offset>0</offset>
              </color>
              <figures>
              </figures>
            </player>
          </players>
          <selectedFigure>0</selectedFigure>
          <rolled>1</rolled>
        </gamedata>

      val result = GameData.fromXml(xml)
      result.isFailure shouldBe true
      result.failed.get.getMessage should include("Unknown color: PURPLE")
    }

    "handle edge case with selectedFigure equal to players.size" in {
      val players = samplePlayers(2)
      val gd = GameData(
        0,
        sampleBoard,
        players,
        2,
        1
      ) // selectedFigure == players.size
      val result = gd.restoreManager(controller)
      result.isSuccess shouldBe true // This should be valid according to the code (idx <= players.size)
    }

    "handle edge case with rolled value -1" in {
      val gd = GameData(0, sampleBoard, samplePlayers(2), 0, -1)
      val result = gd.restoreManager(controller)
      result.isSuccess shouldBe true // -1 should be valid
    }

    "handle edge case with rolled value 6" in {
      val gd = GameData(0, sampleBoard, samplePlayers(2), 0, 6)
      val result = gd.restoreManager(controller)
      result.isSuccess shouldBe true // 6 should be valid
    }

    "roundtrip JSON serialization/deserialization" in {
      val originalPlayers = samplePlayersWithDifferentColors
      val originalGd = GameData(42, sampleBoard, originalPlayers, 3, 4)

      val json = originalGd.toJson
      val restored = GameData.fromJson(json).get

      restored.moves shouldBe originalGd.moves
      restored.board.size shouldBe originalGd.board.size
      restored.selectedFigure shouldBe originalGd.selectedFigure
      restored.rolled shouldBe originalGd.rolled
      restored.players.size shouldBe originalGd.players.size
    }

    "roundtrip XML serialization/deserialization" in {
      val originalPlayers = samplePlayersWithDifferentColors
      val originalGd = GameData(99, sampleBoard, originalPlayers, 2, 1)

      val xml = originalGd.toXml
      val restored = GameData.fromXml(xml).get

      restored.moves shouldBe originalGd.moves
      restored.board.size shouldBe originalGd.board.size
      restored.selectedFigure shouldBe originalGd.selectedFigure
      restored.rolled shouldBe originalGd.rolled
      restored.players.size shouldBe originalGd.players.size
    }
  }
}
