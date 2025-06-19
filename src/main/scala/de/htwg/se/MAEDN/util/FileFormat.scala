package de.htwg.se.MAEDN.util

/** Enumeration for supported file formats in the MAEDN game save system.
  *
  * This enum defines the available file formats for saving and loading game
  * data. Each format has its own advantages:
  *   - JSON: Human-readable, widely supported, good for debugging
  *   - XML: Structured, supports validation, good for complex data hierarchies
  */
enum FileFormat:
  case JSON extends FileFormat
  case XML extends FileFormat

  /** Returns the file extension for this format */
  def extension: String = this match
    case JSON => "json"
    case XML  => "xml"

  /** Returns the MIME type for this format */
  def mimeType: String = this match
    case JSON => "application/json"
    case XML  => "application/xml"
