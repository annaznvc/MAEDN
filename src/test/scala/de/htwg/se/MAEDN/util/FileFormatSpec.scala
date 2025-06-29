package de.htwg.se.MAEDN.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class FileFormatSpec extends AnyWordSpec with Matchers {
  "FileFormat" should {
    "have extension 'json' for JSON" in {
      FileFormat.JSON.extension shouldBe "json"
    }

    "have extension 'xml' for XML" in {
      FileFormat.XML.extension shouldBe "xml"
    }

    "have mimeType 'application/json' for JSON" in {
      FileFormat.JSON.mimeType shouldBe "application/json"
    }

    "have mimeType 'application/xml' for XML" in {
      FileFormat.XML.mimeType shouldBe "application/xml"
    }
  }
}
