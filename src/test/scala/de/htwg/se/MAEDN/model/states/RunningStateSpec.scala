package de.htwg.se.MAEDN.model.statesImp

import de.htwg.se.MAEDN.model.{IManager, Board, State, IMemento, Player, Figure}
import de.htwg.se.MAEDN.util.{Event, Dice}
import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.module.Injectable
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.{eq => eqTo, any, anyInt, anyString}
import org.scalatestplus.mockito.MockitoSugar
import scala.util.{Try, Success, Failure}
import scala.reflect.ClassTag
import java.lang.reflect.Method
import scala.annotation.tailrec

class RunningStateSpec extends AnyWordSpec with Matchers with MockitoSugar {

  trait TestSetup {
    val mockController: IController = mock[IController]
    val mockBoard: Board = mock[Board]
    val mockPlayer1: Player = mock[Player]
    val mockPlayer2: Player = mock[Player]
    val mockFigure1: Figure = mock[Figure]
    val mockFigure2: Figure = mock[Figure]
    val mockFigure3: Figure = mock[Figure]
    val mockFileIO: de.htwg.se.MAEDN.util.FileIO =
      mock[de.htwg.se.MAEDN.util.FileIO]

    when(mockPlayer1.id).thenReturn(1)
    when(mockPlayer2.id).thenReturn(2)
    when(mockPlayer1.figures).thenReturn(List(mockFigure1, mockFigure2))
    when(mockPlayer2.figures).thenReturn(List(mockFigure3))
    when(mockFigure1.id).thenReturn(1)
    when(mockFigure2.id).thenReturn(2)
    when(mockFigure3.id).thenReturn(3)
    when(mockFigure1.owner).thenReturn(mockPlayer1)
    when(mockFigure2.owner).thenReturn(mockPlayer1)
    when(mockFigure3.owner).thenReturn(mockPlayer2)

    val players = List(mockPlayer1, mockPlayer2)
    when(mockBoard.size).thenReturn(40)

    val testState = new RunningState(
      controller = mockController,
      moves = 0,
      board = mockBoard,
      players = players,
      rolled = 3,
      selectedFigure = 0
    ) with Injectable {
      override protected def inject[T](implicit classTag: ClassTag[T]): T = {
        classTag.runtimeClass match {
          case c if c == classOf[de.htwg.se.MAEDN.util.FileIO] =>
            mockFileIO.asInstanceOf[T]
          case c if c == Dice.getClass => // For Dice object
            Dice.asInstanceOf[T]
          case _ =>
            throw new RuntimeException(s"No mock for ${classTag.runtimeClass}")
        }
      }
    }
  }

  "playNext failure handling" should {
    "return Failure when moveFigure fails but player can still move" in new TestSetup {
      // Mock canFigureMove to return true (player can move) for any figure
      when(mockBoard.canFigureMove(any[Figure], any[List[Figure]](), anyInt()))
        .thenReturn(true)

      // Stub moveFigure to return Failure
      val failingState = new RunningState(
        controller = mockController,
        moves = 0,
        board = mockBoard,
        players = players,
        rolled = 3,
        selectedFigure = 0
      ) with Injectable {
        override protected def inject[T](implicit classTag: ClassTag[T]): T = {
          classTag.runtimeClass match {
            case c if c == de.htwg.se.MAEDN.util.FileIO.getClass =>
              mockFileIO.asInstanceOf[T]
            case c if c == Dice.getClass =>
              Dice.asInstanceOf[T]
            case _ =>
              throw new RuntimeException(
                s"No mock for ${classTag.runtimeClass}"
              )
          }
        }
        override def moveFigure(): Try[IManager] =
          Failure(new RuntimeException("Test exception"))
      }

      val result = failingState.playNext()

      result shouldBe a[Failure[_]]
      result.failed.get shouldBe an[IllegalArgumentException]
      result.failed.get.getMessage should be("Invalid move!")
    }

    "move to next player when moveFigure fails and no figures can move" in new TestSetup {
      // Mock canFigureMove to return false (no figures can move)
      when(mockBoard.canFigureMove(any[Figure], any[List[Figure]](), anyInt()))
        .thenReturn(false)

      // Stub moveFigure to return Failure
      val failingState = new RunningState(
        controller = mockController,
        moves = 0,
        board = mockBoard,
        players = players,
        rolled = 3,
        selectedFigure = 0
      ) with Injectable {
        override protected def inject[T](implicit classTag: ClassTag[T]): T = {
          classTag.runtimeClass match {
            case c if c == de.htwg.se.MAEDN.util.FileIO.getClass =>
              mockFileIO.asInstanceOf[T]
            case c if c == Dice.getClass =>
              Dice.asInstanceOf[T]
            case _ =>
              throw new RuntimeException(
                s"No mock for ${classTag.runtimeClass}"
              )
          }
        }
        override def moveFigure(): Try[IManager] =
          Failure(new RuntimeException("Test exception"))
      }

      val result = failingState.playNext()

      result shouldBe a[Success[_]]
      val newState = result.get
      newState.rolled should be(0)
      newState.moves should be(1)
      newState.selectedFigure should be(0)

      verify(mockController).enqueueEvent(Event.PlayNextEvent(1))
    }
  }

  // ---
  // No longer testing handleInvalidSelection directly, but its effects through moveFigure.
  // The logic is now encapsulated within moveFigure.
  // You would test scenarios where figures cannot move or only one can move,
  // and assert on the selectedFigure or player turn change.
  // ---

  "moveFigure behavior" should {
    "move to next player when no movable figures exist for the current player" in new TestSetup {
      // Setup current player figures such that none can move
      when(mockPlayer1.figures).thenReturn(List(mockFigure1))
      when(mockFigure1.owner).thenReturn(mockPlayer1)
      when(mockFigure1.id).thenReturn(1) // Ensure ID is set for the figure
      when(
        mockBoard.canFigureMove(
          eqTo(mockFigure1),
          any[List[Figure]](),
          anyInt()
        )
      )
        .thenReturn(false)

      val stateWithNoMovableFigures = testState.copy(
        players = List(mockPlayer1, mockPlayer2),
        rolled = 3,
        selectedFigure = 0
      )
      val result = stateWithNoMovableFigures.moveFigure()

      result shouldBe a[Success[_]]
      val newState = result.get
      newState.rolled should be(0)
      newState.moves should be(1) // Should move to next player
      newState.selectedFigure should be(0)
      verify(mockController).enqueueEvent(Event.PlayNextEvent(1))
    }
  }

  "getter methods" should {
    "return correct player count" in new TestSetup {
      testState.getPlayerCount should be(2)
    }

    "return correct figure count from first player" in new TestSetup {
      testState.getFigureCount should be(2)
    }

    "return 0 figure count when no players" in new TestSetup {
      val emptyState = testState.copy(players = List.empty)
      emptyState.getFigureCount should be(0)
    }

    "return correct board size" in new TestSetup {
      testState.getBoardSize should be(40)
    }

    "return correct current player" in new TestSetup {
      testState.getCurrentPlayer should be(0)
      val nextMoveState = testState.copy(moves = 1)
      nextMoveState.getCurrentPlayer should be(1)
    }

    "return players list" in new TestSetup {
      testState.getPlayers should be(players)
    }
  }

  "cleanupSaveFiles behavior" should {
    "handle FileIO injection failure gracefully when triggered by win" in new TestSetup {
      val stateWithFailingInjection = new RunningState(
        controller = mockController,
        moves = 0,
        board = mockBoard,
        players = players,
        rolled = 3,
        selectedFigure = 0
      ) with Injectable {
        override protected def inject[T](implicit classTag: ClassTag[T]): T = {
          classTag.runtimeClass match {
            case c if c == de.htwg.se.MAEDN.util.FileIO.getClass =>
              throw new RuntimeException("Injection failed for FileIO")
            case c if c == Dice.getClass =>
              Dice.asInstanceOf[T]
            case _ =>
              throw new RuntimeException(
                s"No mock for ${classTag.runtimeClass}"
              )
          }
        }
      }

      // Simulate win condition for this state
      when(mockFigure1.index).thenReturn(160)
      when(mockFigure2.index).thenReturn(160)
      val figuresAfterMove = List(
        mockFigure1.copy(index = 160),
        mockFigure2.copy(index = 160),
        mockFigure3
      )
      when(mockBoard.moveFigure(any[Figure], any[List[Figure]](), anyInt()))
        .thenReturn(figuresAfterMove)
      when(mockBoard.canFigureMove(any[Figure], any[List[Figure]](), anyInt()))
        .thenReturn(true)

      // Should not throw exception, but rather recover
      noException should be thrownBy stateWithFailingInjection.moveFigure()

      // Verify that no delete calls were made due to injection failure (or at least no successful ones)
      verify(mockFileIO, never()).deleteSaveFile(anyString())
    }

  }

  // --- Reflection-Helfer für private Methoden ---
  private def invokePrivateMethod[T](
      obj: AnyRef,
      methodName: String,
      args: Any*
  ): T = {
    val method = findMethod(obj.getClass, methodName, args.length)
    method.setAccessible(true)
    method.invoke(obj, args.map(_.asInstanceOf[AnyRef]): _*).asInstanceOf[T]
  }

  @tailrec
  private def findMethod(
      clazz: Class[_],
      methodName: String,
      arity: Int
  ): Method = {
    if (clazz == null) {
      throw new RuntimeException(
        s"Method $methodName not found in class hierarchy"
      )
    }
    clazz.getDeclaredMethods
      .find(m =>
        m.getName == methodName && m.getParameterCount == arity
      ) match {
      case Some(m) => m
      case None    => findMethod(clazz.getSuperclass, methodName, arity)
    }
  }

}
