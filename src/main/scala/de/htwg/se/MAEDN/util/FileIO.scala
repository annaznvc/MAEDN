package de.htwg.se.MAEDN.util

import java.io.{File, FileWriter, FileReader, PrintWriter}
import java.nio.file.{Files, Paths, StandardOpenOption}
import java.nio.charset.StandardCharsets
import scala.util.{Try, Success, Failure, Using}
import scala.io.Source

/** Main FileIO class that provides save and load functionality for game data.
  *
  * This class handles reading and writing game data to files in different
  * formats (JSON/XML) with optional encryption. It serves as the main interface
  * for persistence operations in the MAEDN game.
  */
class FileIO {

  /** Default save directory for game files */
  private val saveDirectory = "saves"

  /** Ensures the save directory exists */
  private def ensureSaveDirectory(): Try[Unit] = Try {
    val dir = new File(saveDirectory)
    if (!dir.exists()) {
      dir.mkdirs()
    }
  }

  /** Generates a filename based on the object type and format
    *
    * @param prefix
    *   the prefix for the filename (e.g., "gamedata", "settings")
    * @param format
    *   the file format
    * @param encrypted
    *   whether the file will be encrypted
    * @return
    *   the complete filename
    */
  private def generateFilename(
      prefix: String,
      format: FileFormat,
      encrypted: Boolean = false
  ): String = {
    if (encrypted) {
      s"$prefix.enc" // Encrypted files get .enc extension
    } else {
      s"$prefix.${format.extension}" // Normal files get format extension
    }
  }

  /** Saves serializable data to a file
    *
    * @param data
    *   the serializable data to save
    * @param filename
    *   the target filename without extension
    * @param format
    *   the file format to use for serialization
    * @param encrypt
    *   whether to encrypt the file (if true, saves as .enc file)
    * @return
    *   Try indicating success or failure
    */
  def save[T <: Serializable[T]](
      data: T,
      filename: String,
      format: FileFormat,
      encrypt: Boolean = false
  ): Try[String] = {
    for {
      _ <- ensureSaveDirectory()
      _ <- cleanupExistingSaveFiles(
        filename
      ) // Clean up existing files with same base name
      content = data.toString(format)
      finalContent <-
        if (encrypt) FileEncryption.encryptWithHeader(content)
        else Success(content)
      actualFilename = generateFilename(filename, format, encrypt)
      filePath = Paths.get(saveDirectory, actualFilename)
      _ <- writeToFile(filePath.toString, finalContent)
    } yield filePath.toString
  }

  /** Loads and deserializes data from a file with automatic format detection
    *
    * @param filename
    *   the filename to load from (without extension)
    * @param deserializer
    *   the deserializer to use for converting content back to objects
    * @return
    *   Try containing the deserialized object
    */
  def load[T](
      filename: String,
      deserializer: Deserializable[T]
  ): Try[T] = {
    // Try to find the file with different extensions
    val possibleFiles = List(
      s"$filename.enc", // Encrypted file
      s"$filename.json", // JSON file
      s"$filename.xml" // XML file
    )

    val saveDir = new File(saveDirectory)

    // Find all existing files for this base name and get the most recent one
    val existingFiles = possibleFiles.map { fname =>
      val file = new File(saveDir, fname)
      if (file.exists()) Some((fname, file.lastModified())) else None
    }.flatten

    if (existingFiles.isEmpty) {
      return Failure(new RuntimeException(s"No save file found for: $filename"))
    }

    // Get the most recently modified file
    val (foundFile, _) = existingFiles.maxBy(_._2)
    val filePath = Paths.get(saveDirectory, foundFile).toString

    for {
      content <- readFromFile(filePath)
      (decryptedContent, detectedFormat) <-
        if (FileEncryption.isEncrypted(content)) {
          // Encrypted file - decrypt and try to detect format from content
          FileEncryption.decryptWithHeader(content).map { decrypted =>
            val format =
              if (
                decrypted.trim.startsWith("{") || decrypted.trim
                  .startsWith("[")
              ) {
                FileFormat.JSON
              } else {
                FileFormat.XML
              }
            (decrypted, format)
          }
        } else {
          // Unencrypted file - detect format from extension
          val format =
            if (foundFile.endsWith(".json")) FileFormat.JSON
            else FileFormat.XML
          Success((content, format))
        }
      obj <- deserializer.fromString(decryptedContent, detectedFormat)
    } yield obj
  }

  /** Legacy load method with explicit format (for backward compatibility)
    *
    * @param filename
    *   the filename to load from
    * @param format
    *   the file format to expect
    * @param deserializer
    *   the deserializer to use for converting content back to objects
    * @return
    *   Try containing the deserialized object
    */
  def loadWithFormat[T](
      filename: String,
      format: FileFormat,
      deserializer: Deserializable[T]
  ): Try[T] = {
    val filePath = if (filename.contains("/") || filename.contains("\\")) {
      filename // Absolute or relative path provided
    } else {
      Paths.get(saveDirectory, filename).toString // Just filename provided
    }

    for {
      content <- readFromFile(filePath)
      decryptedContent <-
        if (FileEncryption.isEncrypted(content)) {
          FileEncryption.decryptWithHeader(content)
        } else {
          Success(content)
        }
      obj <- deserializer.fromString(decryptedContent, format)
    } yield obj
  }

  /** Lists all save files in the save directory
    *
    * @param format
    *   optional format filter (when specified, includes both regular files and
    *   encrypted files)
    * @return
    *   Try containing list of filenames
    */
  def listSaveFiles(format: Option[FileFormat] = None): Try[List[String]] =
    Try {
      ensureSaveDirectory()
      val dir = new File(saveDirectory)
      if (dir.exists() && dir.isDirectory) {
        val files = dir.listFiles().filter(_.isFile).map(_.getName).toList
        format match {
          case Some(fmt) =>
            // Include both regular format files and encrypted files
            files.filter(fname =>
              fname.endsWith(s".${fmt.extension}") || fname.endsWith(".enc")
            )
          case None => files
        }
      } else {
        List.empty
      }
    }

  /** Deletes a save file
    *
    * @param filename
    *   the filename to delete
    * @return
    *   Try indicating success or failure
    */
  def deleteSaveFile(filename: String): Try[Unit] = Try {
    val filePath = Paths.get(saveDirectory, filename)
    Files.deleteIfExists(filePath)
  }

  /** Checks if a save file exists
    *
    * @param filename
    *   the filename to check
    * @return
    *   true if the file exists
    */
  def saveFileExists(filename: String): Boolean = {
    val filePath = Paths.get(saveDirectory, filename)
    Files.exists(filePath)
  }

  /** Low-level file writing utility
    *
    * @param filePath
    *   the complete file path
    * @param content
    *   the content to write
    * @return
    *   Try indicating success or failure
    */
  private def writeToFile(filePath: String, content: String): Try[Unit] = {
    Using(new PrintWriter(new FileWriter(filePath, StandardCharsets.UTF_8))) {
      writer =>
        writer.write(content)
    }
  }

  /** Low-level file reading utility
    *
    * @param filePath
    *   the complete file path
    * @return
    *   Try containing the file content
    */
  private def readFromFile(filePath: String): Try[String] = {
    Using(Source.fromFile(filePath, StandardCharsets.UTF_8.name())) { source =>
      source.mkString
    }
  }

  /** Creates a backup of an existing save file
    *
    * @param filename
    *   the filename to backup
    * @return
    *   Try containing the backup filename
    */
  def createBackup(filename: String): Try[String] = {
    val timestamp = java.time.LocalDateTime.now().toString.replace(":", "-")
    val backupFilename = s"backup_${timestamp}_$filename"
    val sourcePath = Paths.get(saveDirectory, filename)
    val backupPath = Paths.get(saveDirectory, backupFilename)

    Try {
      Files.copy(sourcePath, backupPath)
      backupFilename
    }
  }

  /** Cleans up existing save files with the same base name but different
    * extensions
    *
    * @param filename
    *   the base filename (without extension)
    * @return
    *   Try indicating success or failure
    */
  private def cleanupExistingSaveFiles(filename: String): Try[Unit] = Try {
    val saveDir = new File(saveDirectory)
    if (saveDir.exists() && saveDir.isDirectory) {
      // List of possible extensions for the same base filename
      val possibleExtensions = List(".enc", ".json", ".xml")

      possibleExtensions.foreach { ext =>
        val fileToDelete = new File(saveDir, s"$filename$ext")
        if (fileToDelete.exists()) {
          fileToDelete.delete()
        }
      }
    }
  }
}

/** Companion object providing factory methods and utilities */
object FileIO {

  /** Creates a new FileIO instance */
  def apply(): FileIO = new FileIO()

  /** Quick save method for convenience
    *
    * @param data
    *   the data to save
    * @param filename
    *   filename without extension
    * @param format
    *   file format (defaults to JSON)
    * @param encrypt
    *   whether to encrypt (defaults to false)
    * @return
    *   Try containing the saved file path
    */
  def quickSave[T <: Serializable[T]](
      data: T,
      filename: String,
      format: FileFormat = FileFormat.JSON,
      encrypt: Boolean = false
  ): Try[String] = {
    FileIO().save(data, filename, format, encrypt)
  }

  /** Quick load method for convenience
    *
    * @param filename
    *   the filename to load (without extension)
    * @param deserializer
    *   the deserializer to use
    * @return
    *   Try containing the loaded object
    */
  def quickLoad[T](
      filename: String,
      deserializer: Deserializable[T]
  ): Try[T] = {
    FileIO().load(filename, deserializer)
  }
}
