package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.model.{Manager, Board, PlayerFactory}
import de.htwg.se.MAEDN.controller.Controller
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Try, Success, Failure}

class CommandSpec extends AnyWordSpec with Matchers {

  // Dummy-Manager für Tests
  val dummyManager: Manager = new Manager {
    override val controller = new Controller
    override val state = de.htwg.se.MAEDN.model.State.Menu
    override val rolled = 0
  }

  "A Command trait" should {

    "default isNormal to true" in {
      val cmd = new Command {
        override def execute(): Try[Manager] = Success(dummyManager)
      }
      cmd.isNormal shouldBe true
    }

    "successfully return Manager in execute" in {
      val cmd = new Command {
        override def execute(): Try[Manager] = Success(dummyManager)
      }
      val result = cmd.execute()
      result.isSuccess shouldBe true
      result.get shouldBe dummyManager
    }

    "return Failure in execute if something goes wrong" in {
      val cmd = new Command {
        override def execute(): Try[Manager] =
          Failure(new RuntimeException("fail"))
      }
      val result = cmd.execute()
      result.isFailure shouldBe true
      result.failed.get.getMessage shouldBe "fail"
    }
  }
}
