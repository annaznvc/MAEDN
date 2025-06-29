package de.htwg.se.MAEDN.util

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsValue, Json}
import scala.util.Try
import scala.xml.Elem

class SerializableDeserializableSpec extends AnyWordSpec with Matchers {

  // Ein einfacher Dummy-Typ, der beides implementiert
  case class Dummy(x: Int) extends Serializable[Dummy] {
    override def toJson: JsValue = Json.obj("x" -> x)
    override def toXml: Elem = <dummy><x>{x}</x></dummy>
  }

  object Dummy extends Deserializable[Dummy] {
    override def fromJson(json: JsValue): Try[Dummy] =
      Try(Dummy((json \ "x").as[Int]))

    override def fromXml(xml: Elem): Try[Dummy] =
      Try(Dummy((xml \ "x").text.toInt))
  }

  "Serializable trait" should {
    val d = Dummy(42)

    "convert to JSON correctly" in {
      (d.toJson \ "x").as[Int] shouldBe 42
    }

    "convert to XML correctly" in {
      val xml = d.toXml
      (xml \\ "x").text.toInt shouldBe 42
      xml.label shouldBe "dummy"
    }

    "toString with JSON format" in {
      d.toString(FileFormat.JSON) shouldBe Json.prettyPrint(d.toJson)
    }

    "toString with XML format" in {
      d.toString(FileFormat.XML) shouldBe d.toXml.toString()
    }
  }

  "Deserializable trait" should {

    "fromJson returns Success for valid JSON" in {
      val js = Json.obj("x" -> 13)
      Dummy.fromJson(js).get shouldBe Dummy(13)
    }

    "fromXml returns Success for valid XML" in {
      val xml = <dummy><x>13</x></dummy>
      Dummy.fromXml(xml).get shouldBe Dummy(13)
    }

    "fromString returns Success for JSON input" in {
      val content = Json.stringify(Json.obj("x" -> 7))
      Dummy.fromString(content, FileFormat.JSON).get shouldBe Dummy(7)
    }

    "fromString returns Success for XML input" in {
      val content = "<dummy><x>7</x></dummy>"
      Dummy.fromString(content, FileFormat.XML).get shouldBe Dummy(7)
    }

    "fromString returns Failure for invalid JSON" in {
      val bad = "not a json"
      Dummy.fromString(bad, FileFormat.JSON).isFailure shouldBe true
    }

    "fromString returns Failure for malformed XML" in {
      val badXml = "<dummy><x>notAnInt</x></dummy>"
      Dummy.fromString(badXml, FileFormat.XML).isFailure shouldBe true
    }
  }
}
