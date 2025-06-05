package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.model.IManager
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Try, Success, Failure}

class CommandSpec extends AnyWordSpec with Matchers {

  // Dummy-Manager für Tests
  val dummyManager: IManager = new IManager {
    override val moves: Int = 0
    override val board = null
    override val players = Nil
    override val selectedFigure: Int = 0
    override val rolled: Int = 0
    override val state = null
    override val controller = null
    override def startGame() = Success(this)
    override def quitGame() = Success(this)
    override def moveUp() = Success(this)
    override def moveDown() = Success(this)
    override def increaseFigures() = Success(this)
    override def decreaseFigures() = Success(this)
    override def increaseBoardSize() = Success(this)
    override def decreaseBoardSize() = Success(this)
    override def playDice() = Success(this)
    override def playNext() = Success(this)
    override def moveFigure() = Success(this)
    override def getPlayers = players
    override def getPlayerCount = 0
    override def getFigureCount = 0
    override def getBoardSize = 0
    override def getCurrentPlayer = 0
    override def createMemento = None
  }

  "A Command trait" should {

    "default isNormal to true" in {
      val cmd = new Command {
        override def execute(): Try[IManager] = Success(dummyManager)
      }
      cmd.isNormal shouldBe true
    }

    "successfully return IManager in execute" in {
      val cmd = new Command {
        override def execute(): Try[IManager] = Success(dummyManager)
      }
      val result = cmd.execute()
      result.isSuccess shouldBe true
      result.get eq dummyManager shouldBe true // referenzielle Gleichheit
    }

    "return Failure in execute if something goes wrong" in {
      val cmd = new Command {
        override def execute(): Try[IManager] =
          Failure(new RuntimeException("fail"))
      }
      val result = cmd.execute()
      result.isFailure shouldBe true
      result.failed.get.getMessage shouldBe "fail"
    }
  }
}
