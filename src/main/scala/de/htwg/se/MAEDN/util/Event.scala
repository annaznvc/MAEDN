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

  // Command events
  case UndoEvent
  case RedoEvent

  // Error events
  case ErrorEvent(message: String)

  // Default priority (lower value = higher priority)
  def priority: Int = this match {
    case StartGameEvent | QuitGameEvent | BackToMenuEvent | ConfigEvent => 0
    case PlayNextEvent(_)                                               => 1
    case PlayDiceEvent(_)                                               => 2
    case MoveFigureEvent(_)                                             => 3
    case ChangeSelectedFigureEvent(_)                                   => 4
    case KickFigureEvent                                                => 5
    case UndoEvent | RedoEvent                                          => 6
    case ErrorEvent(_)                                                  => 7
  }
}
