package de.htwg.se.MAEDN.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.MAEDN.util.PlayerFactory
import de.htwg.se.MAEDN.model.GameDataImp.GameData

class IMementoSpec extends AnyWordSpec with Matchers {
  "An IMemento" should {
    val board = IBoard(8)
    val players = PlayerFactory(2, 4)
    val memento: IMemento = GameData(0, board, players, 0, 0)

    "restore a manager via restoreManager and restoreIManager" in {
      val controller = null // oder ein Dummy-Controller, falls benötigt
      val result = memento.restoreManager(controller)
      result.isSuccess shouldBe true

      val result2 = memento.restoreIManager(controller)
      result2.isSuccess shouldBe true
    }
  }
}
