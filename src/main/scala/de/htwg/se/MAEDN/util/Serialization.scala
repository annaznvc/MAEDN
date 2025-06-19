package de.htwg.se.MAEDN.util

import play.api.libs.json.{JsValue, Json, Writes, Reads}
import scala.xml.Elem
import scala.util.Try

/** Generic trait for objects that can be serialized to different formats.
  *
  * This trait provides a common interface for serializing objects to JSON and
  * XML. Classes that mix in this trait must implement the conversion methods
  * for their specific data structure.
  */
trait Serializable[T] {

  /** Converts the object to JSON format
    * @return
    *   JsValue representation of the object
    */
  def toJson: JsValue

  /** Converts the object to XML format
    * @return
    *   Elem representation of the object
    */
  def toXml: Elem

  /** Converts the object to a string representation based on the file format
    * @param format
    *   the target file format
    * @return
    *   String representation of the object
    */
  def toString(format: FileFormat): String = format match {
    case FileFormat.JSON => Json.prettyPrint(toJson)
    case FileFormat.XML  => toXml.toString()
  }
}

/** Generic trait for objects that can be deserialized from different formats.
  *
  * This trait provides a common interface for deserializing objects from JSON
  * and XML. Companion objects of classes that need deserialization should
  * extend this trait.
  */
trait Deserializable[T] {

  /** Creates an object from JSON format
    * @param json
    *   the JSON representation
    * @return
    *   Try containing the deserialized object or failure
    */
  def fromJson(json: JsValue): Try[T]

  /** Creates an object from XML format
    * @param xml
    *   the XML representation
    * @return
    *   Try containing the deserialized object or failure
    */
  def fromXml(xml: Elem): Try[T]

  /** Creates an object from string representation based on the file format
    * @param content
    *   the string content
    * @param format
    *   the source file format
    * @return
    *   Try containing the deserialized object or failure
    */
  def fromString(content: String, format: FileFormat): Try[T] = format match {
    case FileFormat.JSON =>
      Try(Json.parse(content)).flatMap(fromJson)
    case FileFormat.XML =>
      Try(scala.xml.XML.loadString(content)).flatMap(fromXml)
  }
}
