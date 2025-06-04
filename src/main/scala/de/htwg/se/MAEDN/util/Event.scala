package de.htwg.se.MAEDN.util

import de.htwg.se.MAEDN.model.State

enum Event {
  // Standard events
  case StartGameEvent
  case QuitGameEvent
  case BackToMenuEvent

  // Config events
  case ConfigEvent

  // Game events
  case PlayNextEvent(playerId: Int)
  case ChangeSelectedFigureEvent(figureId: Int)
  case PlayDiceEvent(rolled: Int)
  case MoveFigureEvent(figureId: Int)
  case KickFigureEvent
  case WinEvent(playerId: Int)

  // Command events
  case UndoEvent
  case RedoEvent

  // Error events
  case ErrorEvent(message: String)
  // Default priority (lower value = higher priority)
  def priority: Int = this match {
    case StartGameEvent | QuitGameEvent | BackToMenuEvent | ConfigEvent => 0
    case WinEvent(_)                                                    => 1
    case PlayNextEvent(_)                                               => 2
    case PlayDiceEvent(_)                                               => 3
    case MoveFigureEvent(_)                                             => 4
    case ChangeSelectedFigureEvent(_)                                   => 5
    case KickFigureEvent                                                => 6
    case UndoEvent | RedoEvent                                          => 7
    case ErrorEvent(_)                                                  => 8
  }
}
