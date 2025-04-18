package de.htwg.se.MAEDN.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class PlayerStatusSpec extends AnyWordSpec with Matchers:

  "PlayerStatus" should {

    "allow Active status" in {
      val status: PlayerStatus = Active
      status shouldBe Active
    }

    "allow Out status with valid placement" in {
      val status = Out(1)
      status shouldBe a [Out]
      status.placement shouldBe 1
    }

    "throw an exception for Out status with invalid placement" in {
      an [IllegalArgumentException] should be thrownBy {
        Out(0)  // invalid: less than 1
      }

      an [IllegalArgumentException] should be thrownBy {
        Out(5)  // invalid: greater than 4
      }
    }
  }
