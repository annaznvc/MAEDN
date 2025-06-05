package de.htwg.se.MAEDN.model.GameDataImp

import de.htwg.se.MAEDN.model._
import de.htwg.se.MAEDN.model.BoardImp.Board
import de.htwg.se.MAEDN.model.PlayerImp.Player
import de.htwg.se.MAEDN.model.FigureImp.Figure
import de.htwg.se.MAEDN.util.Color
import de.htwg.se.MAEDN.controller.Controller
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.model.IPlayer

class GameDataSpec extends AnyWordSpec with Matchers {

  def sampleBoard: Board = Board(
    8,
    IMoveStrategy.createNormalMoveStrategy(),
    IMoveStrategy.createToBoardStrategy(),
    IMoveStrategy.createKickFigureStrategy()
  )
  def samplePlayer: Player = {
    val p = Player(1, Nil, Color.RED)
    val figs = List.tabulate(4)(i => Figure(i + 1, p, i, 4))
    p.copy(figures = figs).asInstanceOf[Player]
  }
  def samplePlayers(n: Int): List[Player] = List.fill(n)(samplePlayer)
  val controller = new Controller

  "GameData" should {

    "restore a valid RunningState" in {
      val gd = GameData(3, sampleBoard, samplePlayers(2), 0, 6)
      val result = gd.restoreManager(controller)
      result.isSuccess shouldBe true
      // result.get shouldBe a[Manager] // ggf. anpassen, falls Manager nicht sichtbar
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

    "fail if rolled is > 6" in {
      val gd = GameData(0, sampleBoard, samplePlayers(2), 0, 7)
      val result = gd.restoreManager(controller)
      result.isFailure shouldBe true
      result.failed.get.getMessage should include("Rolled value")
    }

    "fail if players list is empty" in {
      val gd = GameData(0, sampleBoard, List.empty, 0, 1)
      val result = gd.restoreManager(controller)
      result.isFailure shouldBe true
      result.failed.get.getMessage should include("Players size")
    }
  }
}
