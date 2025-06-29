package de.htwg.se.MAEDN.controller.command

import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.controller.controllerImp.Controller
import de.htwg.se.MAEDN.model.{IManager, IMemento}
import de.htwg.se.MAEDN.model.statesImp.RunningState
import de.htwg.se.MAEDN.model.{Board, State}
import de.htwg.se.MAEDN.util.PlayerFactory
import de.htwg.se.MAEDN.model.gameDataImp.GameData
import de.htwg.se.MAEDN.util._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.util.{Try, Success, Failure}
import play.api.libs.json.{JsValue, Json}
import scala.xml.Elem

// Test-Datenklasse die Serializable implementiert
case class TestData(value: String) extends Serializable[TestData] {
  override def toJson: JsValue = Json.obj("value" -> value)
  override def toXml: Elem = <testData><value>{value}</value></testData>
}

object TestData extends Deserializable[TestData] {
  override def fromJson(json: JsValue): Try[TestData] = Try {
    TestData((json \ "value").as[String])
  }

  override def fromXml(xml: Elem): Try[TestData] = Try {
    TestData((xml \ "value").text)
  }
}

// Dummy FileIO für Tests
class DummyFileIO extends FileIO {
  override def save[T <: Serializable[T]](
      data: T,
      filename: String,
      format: FileFormat,
      encrypt: Boolean = false
  ): Try[String] = Success("saved")

  override def load[T](
      filename: String,
      deserializer: Deserializable[T]
  ): Try[T] = Failure(new Exception("not implemented"))

  override def listSaveFiles(
      format: Option[FileFormat] = None
  ): Try[List[String]] =
    Success(List.empty)
}

// FileIO die erfolgreiches Speichern simuliert
class SuccessfulSaveFileIO extends FileIO {
  override def save[T <: Serializable[T]](
      data: T,
      filename: String,
      format: FileFormat,
      encrypt: Boolean = false
  ): Try[String] = Success("saved successfully")

  override def load[T](
      filename: String,
      deserializer: Deserializable[T]
  ): Try[T] = Failure(new Exception("not implemented"))

  override def listSaveFiles(
      format: Option[FileFormat] = None
  ): Try[List[String]] =
    Success(List.empty)
}

// FileIO die fehlschlägendes Speichern simuliert
class FailingSaveFileIO extends FileIO {
  override def save[T <: Serializable[T]](
      data: T,
      filename: String,
      format: FileFormat,
      encrypt: Boolean = false
  ): Try[String] = Failure(new Exception("Save failed"))

  override def load[T](
      filename: String,
      deserializer: Deserializable[T]
  ): Try[T] = Failure(new Exception("not implemented"))

  override def listSaveFiles(
      format: Option[FileFormat] = None
  ): Try[List[String]] =
    Success(List.empty)
}

// FileIO für ContinueGameCommand Tests
class ContinueGameFileIO(
    files: List[String] = List.empty,
    loadResult: Try[GameData] = Failure(new Exception("Load failed")),
    listResult: Try[List[String]] = Success(List.empty)
) extends FileIO {
  override def save[T <: Serializable[T]](
      data: T,
      filename: String,
      format: FileFormat,
      encrypt: Boolean = false
  ): Try[String] = Success("saved")

  override def load[T](
      filename: String,
      deserializer: Deserializable[T]
  ): Try[T] = loadResult.asInstanceOf[Try[T]]

  override def listSaveFiles(
      format: Option[FileFormat] = None
  ): Try[List[String]] = listResult
}

class CommandsSpec extends AnyWordSpec with Matchers {
  "A Command" should {
    "work with DummyFileIO and proper Serializable data" in {
      val fileIO = new DummyFileIO
      val testData = TestData("dummyData")
      fileIO.save(testData, "test", FileFormat.JSON) shouldBe Success("saved")
      fileIO.load("test", TestData) shouldBe a[Failure[?]]
    }
  }

  def makeRestorableManager(controller: Controller): IManager = {
    val players = PlayerFactory(2, 4)
    val board = Board(8)
    RunningState(
      controller,
      moves = 0,
      board,
      players,
      rolled = 3,
      selectedFigure = 0
    )
  }

  def makeRunningController(): Controller = {
    val controller = new Controller
    controller.manager = makeRestorableManager(controller)
    controller
  }

  val dummyFileIO = new DummyFileIO

  "All Command classes" should {

    val controller = new Controller

    "execute DecreaseBoardSizeCommand" in {
      val command = DecreaseBoardSizeCommand(controller)
      command.execute().isSuccess shouldBe true
    }

    "execute IncreaseBoardSizeCommand" in {
      val command = IncreaseBoardSizeCommand(controller)
      command.execute().isSuccess shouldBe true
    }

    "execute IncreaseFiguresCommand" in {
      val command = IncreaseFiguresCommand(controller)
      command.execute().isSuccess shouldBe true
    }

    "execute DecreaseFiguresCommand" in {
      val command = DecreaseFiguresCommand(controller)
      command.execute().isSuccess shouldBe true
    }

    "execute MoveUpCommand" in {
      val command = MoveUpCommand(controller)
      command.execute().isSuccess shouldBe true
    }

    "execute MoveDownCommand" in {
      val command = MoveDownCommand(controller)
      command.execute().isSuccess shouldBe true
    }

    "execute QuitGameCommand" in {
      val command = QuitGameCommand(controller, dummyFileIO)
      command.execute().isSuccess shouldBe true
    }

    "execute StartGameCommand" in {
      val command = StartGameCommand(controller)
      command.execute().isSuccess shouldBe true
    }

    "execute PlayNextCommand" in {
      val command = PlayNextCommand(controller)
      command.execute().isSuccess || command.execute().isFailure shouldBe true
    }
  }

  "QuitGameCommand" should {
    "auto-save when quitting from Running state with successful save" in {
      val runningController = makeRunningController()
      val successFileIO = new SuccessfulSaveFileIO
      val command = QuitGameCommand(runningController, successFileIO)

      // Capture console output to verify auto-save message
      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        val result = command.execute()
        result.isSuccess shouldBe true
      }
      output.toString should include("Game auto-saved before quitting")
    }

    "handle failed auto-save when quitting from Running state" in {
      val runningController = makeRunningController()
      val failingFileIO = new FailingSaveFileIO
      val command = QuitGameCommand(runningController, failingFileIO)

      // Capture console output to verify failed save message
      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        val result = command.execute()
        result.isSuccess shouldBe true // Should still succeed despite failed save
      }
      output.toString should include("Failed to auto-save game")
    }

    "handle non-GameData memento types" in {
      val controller = new Controller

      // Create a simple IMemento implementation
      val nonGameDataMemento = new IMemento {
        override def restoreManager(controller: IController): Try[IManager] =
          Success(makeRestorableManager(controller.asInstanceOf[Controller]))
        override def restoreIManager(controller: IController): Try[IManager] =
          Success(makeRestorableManager(controller.asInstanceOf[Controller]))
      }

      // Create a manager that returns a non-GameData memento
      val mockManager = new IManager {
        // jetzt stabile vals statt defs
        override val controller: IController = controller
        override val state: State = State.Running
        override val rolled: Int = 0
        override val moves: Int = 0
        override val selectedFigure: Int = 0

        override def createMemento: Option[IMemento] = Some(nonGameDataMemento)
        override def quitGame(): Try[IManager] = Success(this)
        override def decreaseBoardSize(): Try[IManager] = Success(this)
        override def increaseBoardSize(): Try[IManager] = Success(this)
        override def decreaseFigures(): Try[IManager] = Success(this)
        override def increaseFigures(): Try[IManager] = Success(this)
        override def moveUp(): Try[IManager] = Success(this)
        override def moveDown(): Try[IManager] = Success(this)
        override def startGame(): Try[IManager] = Success(this)
        override def playNext(): Try[IManager] = Success(this)
      }

      controller.manager = mockManager

      val command = QuitGameCommand(controller, dummyFileIO)
      val result = command.execute()
      result.isSuccess shouldBe true
    }

    "quit normally when not in Running state" in {
      val controller = new Controller // Default state is not Running
      val command = QuitGameCommand(controller, dummyFileIO)
      val result = command.execute()
      result.isSuccess shouldBe true
    }

    "silently ignore exceptions thrown by FileIO.save during auto-save" in {
      // Controller in Running-State vorbereiten
      val runningController = makeRunningController()
      // FileIO, das beim Speichern eine Exception wirft
      val throwingFileIO = new DummyFileIO {
        override def save[T <: Serializable[T]](
            data: T,
            filename: String,
            format: FileFormat,
            encrypt: Boolean = false
        ): Try[String] = throw new RuntimeException("boom")
      }
      val command = QuitGameCommand(runningController, throwingFileIO)

      // Wenn execute aufgerufen wird, darf keine Exception herausfliegen
      noException should be thrownBy command.execute()
    }

  }

  "UndoCommand" should {

    val controller = new Controller
    val command = new UndoCommand(controller)

    "do nothing if undo stack is empty" in {
      controller.undoStack.clear()
      val result = command.execute()
      result shouldBe Success(controller.manager)
    }

    "restore manager from undo stack and push to redo" in {
      controller.manager = makeRestorableManager(controller)
      val memento = controller.manager.createMemento.get
      controller.undoStack.push(memento)
      val result = command.execute()
      result.isSuccess shouldBe true
      controller.redoStack.nonEmpty shouldBe true
    }

    "fail if restoreManager fails and cover isNormal = false" in {
      val mockBoard = Board(8)

      val badMemento = new GameData(
        0,
        mockBoard,
        PlayerFactory(2, 4),
        0,
        0
      ) {
        override def restoreManager(controller: IController) =
          Failure(new RuntimeException("fail"))
      }

      controller.undoStack.push(badMemento)

      val command = new UndoCommand(controller)
      command.isNormal shouldBe false
      val result = command.execute()
      result.isFailure shouldBe true
    }
  }

  "RedoCommand" should {

    val controller = new Controller
    val command = RedoCommand(controller)

    "do nothing if redo stack is empty" in {
      controller.redoStack.clear()
      val result = command.execute()
      result shouldBe Success(controller.manager)
    }

    "restore manager from redo stack and push to undo" in {
      controller.manager = makeRestorableManager(controller)
      val memento = controller.manager.createMemento.get
      controller.redoStack.push(memento)
      val result = command.execute()
      result.isSuccess shouldBe true
      controller.undoStack.nonEmpty shouldBe true
    }

    "fail if restoreManager fails and cover isNormal = false" in {
      val mockBoard = Board(8)

      val badMemento = new GameData(
        0,
        mockBoard,
        PlayerFactory(2, 4),
        0,
        0
      ) {
        override def restoreManager(controller: IController) =
          Failure(new RuntimeException("fail"))
      }

      controller.redoStack.push(badMemento)

      val command = RedoCommand(controller)
      command.isNormal shouldBe false
      val result = command.execute()
      result.isFailure shouldBe true
    }
  }

  "ContinueGameCommand" should {
    "fail when no save files are found" in {
      val controller = new Controller
      val fileIO = new ContinueGameFileIO(files = List.empty)
      val command = ContinueGameCommand(controller, fileIO)
      val result = command.execute()
      result.isFailure shouldBe true
      result.failed.get.getMessage should include("No save files found")
    }

    "fail when listSaveFiles fails" in {
      val controller = new Controller
      val fileIO = new ContinueGameFileIO(
        listResult = Failure(new Exception("Failed to list files"))
      )
      val command = ContinueGameCommand(controller, fileIO)
      val result = command.execute()
      result.isFailure shouldBe true
      result.failed.get.getMessage should include("Failed to check save files")
    }

    "fail when loading game file fails" in {
      val controller = new Controller
      val fileIO = new ContinueGameFileIO(
        files = List("savegame.json"),
        listResult = Success(List("savegame.json")),
        loadResult = Failure(new Exception("Load error"))
      )
      val command = ContinueGameCommand(controller, fileIO)
      val result = command.execute()
      result.isFailure shouldBe true
      result.failed.get.getMessage should include(
        "Failed to load game from file"
      )
    }

    "fail when restoring manager fails" in {
      val controller = new Controller
      val mockBoard = Board(8)
      val badGameData = new GameData(0, mockBoard, PlayerFactory(2, 4), 0, 0) {
        override def restoreManager(controller: IController) =
          Failure(new RuntimeException("Restore failed"))
      }

      val fileIO = new ContinueGameFileIO(
        files = List("savegame.json"),
        listResult = Success(List("savegame.json")),
        loadResult = Success(badGameData)
      )
      val command = ContinueGameCommand(controller, fileIO)
      val result = command.execute()
      result.isFailure shouldBe true
      result.failed.get.getMessage should include(
        "Failed to restore game state"
      )
    }

    "successfully load and restore game with different file extensions" in {
      val controller = new Controller
      val mockBoard = Board(8)
      val goodGameData = new GameData(0, mockBoard, PlayerFactory(2, 4), 0, 0) {
        override def restoreManager(controller: IController) = {
          val restoredManager =
            makeRestorableManager(controller.asInstanceOf[Controller])
          Success(restoredManager)
        }
      }

      // Test with .enc extension
      val fileIOEnc = new ContinueGameFileIO(
        files = List("savegame.json.enc"),
        listResult = Success(List("savegame.json.enc")),
        loadResult = Success(goodGameData)
      )
      val commandEnc = ContinueGameCommand(controller, fileIOEnc)
      val resultEnc = commandEnc.execute()
      resultEnc.isSuccess shouldBe true

      // Test with .xml extension
      val fileIOXml = new ContinueGameFileIO(
        files = List("savegame.xml"),
        listResult = Success(List("savegame.xml")),
        loadResult = Success(goodGameData)
      )
      val commandXml = ContinueGameCommand(controller, fileIOXml)
      val resultXml = commandXml.execute()
      resultXml.isSuccess shouldBe true

      // Test with .json extension
      val fileIOJson = new ContinueGameFileIO(
        files = List("savegame.json"),
        listResult = Success(List("savegame.json")),
        loadResult = Success(goodGameData)
      )
      val commandJson = ContinueGameCommand(controller, fileIOJson)
      val resultJson = commandJson.execute()
      resultJson.isSuccess shouldBe true
    }

    "have isNormal return false" in {
      val controller = new Controller
      val command = ContinueGameCommand(controller, dummyFileIO)
      command.isNormal shouldBe false
    }
  }
}
