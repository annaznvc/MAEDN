package de.htwg.se.MAEDN.module

import com.google.inject.AbstractModule
import com.google.inject.Singleton
import com.google.inject.Provides
import org.jline.terminal.Terminal

import de.htwg.se.MAEDN.controller.IController
import de.htwg.se.MAEDN.controller.controllerImp.Controller
import de.htwg.se.MAEDN.model.{IManager, Board, Player}
import de.htwg.se.MAEDN.model.statesImp.MenuState
import de.htwg.se.MAEDN.aview.tui.{TUI, InputManager}
import de.htwg.se.MAEDN.aview.gui.{GUI, ActionManager}
import de.htwg.se.MAEDN.util.{PlayerFactory, Dice, FileIO}
import de.htwg.se.MAEDN.model.strategy.{
  ToBoardStrategy,
  NormalMoveStrategy,
  KickFigureStrategy
}
import de.htwg.se.MAEDN.controller.command._

/** Dependency Injection Module for MAEDN Game
  *
  * This module configures all dependencies using Google Guice to prevent direct
  * object instantiation throughout the project. All object creation should go
  * through this module to maintain proper dependency injection.
  */
class MAEDNModule extends AbstractModule {
  override def configure(): Unit = {
    // Explicitly bind IController to Controller implementation
    bind(classOf[IController]).to(classOf[Controller]).in(classOf[Singleton])
  }

  /** Provides a configured IManager instance through the controller The manager
    * handles game state and logic
    */
  @Provides
  @Singleton
  def provideManager(controller: IController): IManager = {
    // Create a MenuState directly
    new MenuState(
      controller,
      0,
      Board(8),
      PlayerFactory(2, 4),
      0 // Default rolled value
    )
  }

  /** Provides the TUI (Text User Interface) with injected controller and
    * terminal
    */
  @Provides
  def provideTUI(controller: IController, terminal: Terminal): TUI = {
    new TUI(controller, terminal)
  }

  /** Provides the GUI (Graphical User Interface) with injected controller
    */
  @Provides
  def provideGUI(controller: IController): GUI = {
    new GUI(controller)
  }

  /** Provides a default game board configuration
    */
  @Provides
  def provideBoard(
      moveStrategy: NormalMoveStrategy,
      toBoardStrategy: ToBoardStrategy,
      kickFigureStrategy: KickFigureStrategy
  ): Board = {
    new Board(8, moveStrategy, toBoardStrategy, kickFigureStrategy)
  }

  /** Provides access to the Dice object for the game
    */
  @Provides
  @Singleton
  def provideDice(): Dice.type = {
    Dice
  }

  /** Factory method to create players with custom configuration
    */
  @Provides
  def providePlayers(): List[Player] = {
    PlayerFactory(2, 4) // Use default values instead of injected parameters
  }

  /** Provides a shared terminal instance for TUI and InputManager
    */
  @Provides
  @Singleton
  def provideTerminal(): Terminal = {
    import org.jline.terminal.TerminalBuilder
    TerminalBuilder.builder().system(true).build()
  }

  /** Provides the InputManager with injected controller and terminal
    */
  @Provides
  def provideInputManager(
      controller: IController,
      terminal: Terminal
  ): InputManager = {
    new InputManager(controller, terminal)
  }

  /** Provides the ActionManager with injected controller
    */
  @Provides
  def provideActionManager(controller: IController): ActionManager = {
    new ActionManager(controller)
  }

  /** Provides strategy instances for move operations
    */
  @Provides
  def provideToBoardStrategy(): ToBoardStrategy = {
    new ToBoardStrategy()
  }

  @Provides
  def provideNormalMoveStrategy(): NormalMoveStrategy = {
    new NormalMoveStrategy()
  }

  @Provides
  def provideKickFigureStrategy(): KickFigureStrategy = {
    new KickFigureStrategy()
  }

  /** Provider methods for Command objects */
  @Provides
  def providePlayNextCommand(controller: IController): PlayNextCommand = {
    PlayNextCommand(controller)
  }

  @Provides
  def provideMoveUpCommand(controller: IController): MoveUpCommand = {
    MoveUpCommand(controller)
  }

  @Provides
  def provideMoveDownCommand(controller: IController): MoveDownCommand = {
    MoveDownCommand(controller)
  }

  @Provides
  def provideIncreaseFiguresCommand(
      controller: IController
  ): IncreaseFiguresCommand = {
    IncreaseFiguresCommand(controller)
  }

  @Provides
  def provideDecreaseFiguresCommand(
      controller: IController
  ): DecreaseFiguresCommand = {
    DecreaseFiguresCommand(controller)
  }

  @Provides
  def provideIncreaseBoardSizeCommand(
      controller: IController
  ): IncreaseBoardSizeCommand = {
    IncreaseBoardSizeCommand(controller)
  }

  @Provides
  def provideDecreaseBoardSizeCommand(
      controller: IController
  ): DecreaseBoardSizeCommand = {
    DecreaseBoardSizeCommand(controller)
  }
  @Provides
  def provideQuitGameCommand(
      controller: IController,
      fileIO: FileIO
  ): QuitGameCommand = {
    QuitGameCommand(controller, fileIO)
  }

  @Provides
  def provideStartGameCommand(controller: IController): StartGameCommand = {
    StartGameCommand(controller)
  }

  @Provides
  def provideUndoCommand(controller: IController): UndoCommand = {
    new UndoCommand(controller)
  }

  @Provides
  def provideRedoCommand(controller: IController): RedoCommand = {
    RedoCommand(controller)
  }

  /** Provides FileIO instance for save/load operations
    */
  @Provides
  @Singleton
  def provideFileIO(): FileIO = {
    new FileIO()
  }

  /** Provides ContinueGameCommand for continuing a saved game
    */
  @Provides
  def provideContinueGameCommand(
      controller: IController,
      fileIO: FileIO
  ): ContinueGameCommand = {
    ContinueGameCommand(controller, fileIO)
  }
}

/** Companion object providing factory methods for dependency injection
  */
object MAEDNModule {

  /** Creates a new instance of the MAEDN dependency injection module
    */
  def apply(): MAEDNModule = new MAEDNModule()

  /** Creates the module with custom configuration if needed
    */
  def create(): MAEDNModule = new MAEDNModule()
}
