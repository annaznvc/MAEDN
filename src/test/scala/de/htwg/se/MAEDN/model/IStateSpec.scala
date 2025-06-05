package de.htwg.se.MAEDN.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.util.{Try, Success}

class IStateSpec extends AnyWordSpec with Matchers {
  "An IState" should {
    // Dummy-Implementierung für das Interface
    val dummy = new IState {
      val moves = 0
      val state = State.Menu
      val controller = null
      val rolled = 0
      val selectedFigure = 0

      def startGame() = Success(null)
      def quitGame() = Success(null)
      def moveUp() = Success(null)
      def moveDown() = Success(null)
      def increaseFigures() = Success(null)
      def decreaseFigures() = Success(null)
      def increaseBoardSize() = Success(null)
      def decreaseBoardSize() = Success(null)
      def playDice() = Success(null)
      def playNext() = Success(null)
      def moveFigure() = Success(null)

      def getPlayers = Nil
      def getPlayerCount = 0
      def getFigureCount = 0
      def getBoardSize = 0
      def getCurrentPlayer = 0
    }

    "have default values and implement all methods" in {
      dummy.moves shouldBe 0
      dummy.state shouldBe State.Menu
      dummy.rolled shouldBe 0
      dummy.selectedFigure shouldBe 0
      dummy.getPlayers shouldBe Nil
      dummy.getPlayerCount shouldBe 0
      dummy.getFigureCount shouldBe 0
      dummy.getBoardSize shouldBe 0
      dummy.getCurrentPlayer shouldBe 0
    }

    "return Success for all actions" in {
      dummy.startGame().isSuccess shouldBe true
      dummy.quitGame().isSuccess shouldBe true
      dummy.moveUp().isSuccess shouldBe true
      dummy.moveDown().isSuccess shouldBe true
      dummy.increaseFigures().isSuccess shouldBe true
      dummy.decreaseFigures().isSuccess shouldBe true
      dummy.increaseBoardSize().isSuccess shouldBe true
      dummy.decreaseBoardSize().isSuccess shouldBe true
      dummy.playDice().isSuccess shouldBe true
      dummy.playNext().isSuccess shouldBe true
      dummy.moveFigure().isSuccess shouldBe true
    }
  }
}
