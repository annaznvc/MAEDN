package de.htwg.se.MAEDN

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class MAEDNMainSpec extends AnyWordSpec with Matchers {
  "The MAEDN main method" should {
    "not throw exceptions when called" in {
      noException should be thrownBy {
        // Führe main mit leeren args aus
        maedn.main(Array.empty)
      }
    }
  }
}
