package de.htwg.se.MAEDN.util

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
  case InvalidMoveEvent

  // Command events
  case UndoEvent
  case RedoEvent
}
