package de.htwg.se.MAEDN.model

import de.htwg.se.MAEDN.controller.Controller
import de.htwg.se.MAEDN.util.Color
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameDataSpec extends AnyWordSpec with Matchers {

  def sampleBoard: Board = Board(8)
  def samplePlayer: Player = {
    val p = Player(1, Nil, Color.RED)
    val figs = List.tabulate(4)(i => Figure(i + 1, p, i))
    p.copy(figures = figs)
  }
  def samplePlayers(n: Int): List[Player] = List.fill(n)(samplePlayer)
  val controller = new Controller

  //  Fake-List, die isEmpty true liefert, aber size >= 2 vortäuscht
  def fakeEmptyPlayers: List[Player] =
    LazyList.empty[Player].toList

  "GameData" should {

    "restore a valid RunningState" in {
      val gd = GameData(3, sampleBoard, samplePlayers(2), 0, 6)
      val result = gd.restoreManager(controller)
      result.isSuccess shouldBe true
      result.get shouldBe a[Manager]
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

    "fail if rolled is > 6" in {
      val gd = GameData(0, sampleBoard, samplePlayers(2), 0, 7)
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
  }
}
