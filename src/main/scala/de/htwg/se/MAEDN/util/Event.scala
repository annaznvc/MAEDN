package de.htwg.se.MAEDN.util

enum Event {
  // Standard events
  case StartGameEvent
  case QuitGameEvent
  case BackToMenuEvent

  // Config events
  case ConfigEvent

  // Game events
  case ChangeSelectedFigureEvent(figureId: Int)
  case RollDiceEvent(rolled: Int)
  case MoveFigureEvent(figureId: Int)
  case KickFigureEvent
  case InvalidMoveEvent
}
