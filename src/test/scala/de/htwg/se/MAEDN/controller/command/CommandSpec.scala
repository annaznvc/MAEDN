package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.model.IManager
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Try, Success, Failure}

class CommandSpec extends AnyWordSpec with Matchers {

  // Dummy-IManager für Tests
  val dummyManager: IManager = new IManager {
    override val controller = null
    override val state = null
    override val rolled = 0
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
      // Identitätsvergleich, da shouldBe für Traits problematisch ist
      (result.get eq dummyManager) shouldBe true
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
