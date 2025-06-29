package de.htwg.se.MAEDN.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.util.{Success, Failure, Try}
import java.io.File
import play.api.libs.json.{JsValue, Json}
import scala.xml.Elem
import java.nio.file.Paths

// Dummy serializable and deserializable for testing
case class DummyData(value: String) extends Serializable[DummyData] {
  override def toString(format: FileFormat): String = format match {
    case FileFormat.JSON => s"""{"value":"$value"}"""
    case FileFormat.XML  => s"<DummyData><value>$value</value></DummyData>"
  }
  override def toJson: JsValue = Json.obj("value" -> value)
  override def toXml: Elem = <DummyData><value>{value}</value></DummyData>
}
object DummyData extends Deserializable[DummyData] {
  override def fromString(str: String, format: FileFormat): Try[DummyData] =
    format match {
      case FileFormat.JSON =>
        val v = "\"value\":\"([^\"]*)\"".r
          .findFirstMatchIn(str)
          .map(_.group(1))
          .getOrElse("")
        Success(DummyData(v))
      case FileFormat.XML =>
        val v = "<value>([^<]*)</value>".r
          .findFirstMatchIn(str)
          .map(_.group(1))
          .getOrElse("")
        Success(DummyData(v))
    }
  override def fromJson(json: JsValue): Try[DummyData] =
    Try(DummyData((json \ "value").as[String]))
  override def fromXml(xml: Elem): Try[DummyData] =
    Try(DummyData((xml \ "value").text))
}

class FileIOSpec extends AnyWordSpec with Matchers {
  val fileIO = new FileIO
  val testFilename = "testfile"
  val testData = DummyData("abc123")

  "FileIO" should {
    "save and load JSON" in {
      val saveResult = fileIO.save(testData, testFilename, FileFormat.JSON)
      saveResult shouldBe a[Success[_]]
      val loadResult = fileIO.load(testFilename, DummyData)
      loadResult shouldBe Success(testData)
    }

    "save and load XML" in {
      val saveResult = fileIO.save(testData, testFilename, FileFormat.XML)
      saveResult shouldBe a[Success[_]]
      val loadResult = fileIO.load(testFilename, DummyData)
      loadResult shouldBe Success(testData)
    }

    "save and load encrypted JSON" in {
      val saveResult =
        fileIO.save(testData, testFilename, FileFormat.JSON, encrypt = true)
      saveResult shouldBe a[Success[_]]
      val loadResult = fileIO.load(testFilename, DummyData)
      loadResult shouldBe Success(testData)
    }

    "save and load encrypted XML" in {
      val saveResult =
        fileIO.save(testData, testFilename, FileFormat.XML, encrypt = true)
      saveResult shouldBe a[Success[_]]
      val loadResult = fileIO.load(testFilename, DummyData)
      loadResult shouldBe Success(testData)
    }

    "list save files" in {
      fileIO.save(testData, testFilename, FileFormat.JSON)
      val files = fileIO.listSaveFiles()
      files shouldBe a[Success[_]]
      files.get.exists(_.contains(testFilename)) shouldBe true
    }

    "delete save file" in {
      fileIO.save(testData, testFilename, FileFormat.JSON)
      val delResult = fileIO.deleteSaveFile(s"$testFilename.json")
      delResult shouldBe a[Success[_]]
      fileIO.saveFileExists(s"$testFilename.json") shouldBe false
    }

    "create backup" in {
      fileIO.save(testData, testFilename, FileFormat.JSON)
      val backupResult = fileIO.createBackup(s"$testFilename.json")
      backupResult shouldBe a[Success[_]]
      val backupFile = backupResult.get
      fileIO.saveFileExists(backupFile) shouldBe true
      // Clean up backup
      fileIO.deleteSaveFile(backupFile)
    }

    "fail to load non-existent file" in {
      val result = fileIO.load("doesnotexist", DummyData)
      result shouldBe a[Failure[_]]
    }

    "loadWithFormat works for JSON" in {
      fileIO.save(testData, testFilename, FileFormat.JSON)
      val result =
        fileIO.loadWithFormat(s"$testFilename.json", FileFormat.JSON, DummyData)
      result shouldBe Success(testData)
    }

    "loadWithFormat works for XML" in {
      fileIO.save(testData, testFilename, FileFormat.XML)
      val result =
        fileIO.loadWithFormat(s"$testFilename.xml", FileFormat.XML, DummyData)
      result shouldBe Success(testData)
    }

    "loadWithFormat should load from an absolute or relative path without adding the saves directory" in {
      // erstelle normal eine Datei
      fileIO.save(testData, testFilename, FileFormat.JSON)
      // baue einen relativen Pfad zusammen
      val relativePath = Paths.get("saves", s"$testFilename.json").toString
      val result =
        fileIO.loadWithFormat(relativePath, FileFormat.JSON, DummyData)
      result shouldBe Success(testData)
    }

    "loadWithFormat should decrypt encrypted files via FileEncryption.decryptWithHeader" in {
      // speichere verschlüsselt
      val encSave =
        fileIO.save(testData, testFilename, FileFormat.JSON, encrypt = true)
      encSave shouldBe a[Success[_]]
      val encPath = encSave.get
      // lade mit explizitem Format
      val loadEnc = fileIO.loadWithFormat(encPath, FileFormat.JSON, DummyData)
      loadEnc shouldBe Success(testData)
    }

    "listSaveFiles returns empty list when 'saves' exists as a file (nicht als Verzeichnis)" in {
      // vorheriges 'saves'-Verzeichnis entfernen
      val dir = new File("saves")
      if (dir.exists()) {
        if (dir.isDirectory) dir.listFiles().foreach(_.delete())
        dir.delete()
      }
      // lege jetzt eine Datei namens 'saves' an
      dir.createNewFile()
      val files = fileIO.listSaveFiles()
      files shouldBe Success(List.empty)
      // aufräumen
      dir.delete()
    }

    "apply factory method returns a FileIO instance" in {
      val fo = FileIO()
      fo shouldBe a[FileIO]
    }

    "quickSave and quickLoad shortcut methods work correctly" in {
      val quickName = s"${testFilename}_quick"
      val saveRes = FileIO.quickSave(testData, quickName, FileFormat.XML)
      saveRes shouldBe a[Success[_]]
      val loadRes = FileIO.quickLoad(quickName, DummyData)
      loadRes shouldBe Success(testData)
    }

    // Clean up after all tests
    // (Falls du wirklich aufräumen willst, nutze ein AfterAll- oder AfterEach-Mixin von ScalaTest)
    // Hier als Methode, aber nicht als override!
    def cleanup(): Unit = {
      val dir = new File("saves")
      if (dir.exists && dir.isDirectory) {
        dir.listFiles().foreach(_.delete())
      }
    }
  }
}
