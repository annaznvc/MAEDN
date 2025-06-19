package de.htwg.se.MAEDN.util

import javax.crypto.Cipher
import javax.crypto.spec.{SecretKeySpec, IvParameterSpec}
import javax.crypto.KeyGenerator
import java.security.SecureRandom
import java.util.Base64
import scala.util.{Try, Success, Failure}
import java.nio.charset.StandardCharsets

/** Object providing file encryption and decryption functionality for save
  * files.
  *
  * This object uses AES encryption with CBC mode and PKCS5 padding to secure
  * game save files. The encryption key is derived from system environment
  * variables to ensure security while maintaining portability.
  */
object FileEncryption {

  private val ALGORITHM = "AES"
  private val TRANSFORMATION = "AES/CBC/PKCS5Padding"
  private val KEY_LENGTH = 256
  private val IV_LENGTH = 16

  /** Generates an encryption key from system environment variables.
    *
    * This method combines multiple environment variables to create a more
    * secure key. It uses USER, PATH, and JAVA_HOME as base components, falling
    * back to defaults if they're not available.
    *
    * @return
    *   SecretKeySpec for AES encryption
    */
  private def generateKeyFromEnvironment: SecretKeySpec = {
    val user =
      sys.env.getOrElse("USER", sys.env.getOrElse("USERNAME", "defaultuser"))
    val path = sys.env.getOrElse("PATH", "defaultpath")
    val javaHome = sys.env.getOrElse("JAVA_HOME", "defaultjava")

    // Combine environment variables to create a seed
    val seedString = s"MAEDN_$user$path$javaHome"
    val keyBytes = java.security.MessageDigest
      .getInstance("SHA-256")
      .digest(seedString.getBytes(StandardCharsets.UTF_8))
      .take(32) // Take first 32 bytes for AES-256

    new SecretKeySpec(keyBytes, ALGORITHM)
  }

  /** Encrypts the given plaintext content.
    *
    * @param plaintext
    *   the content to encrypt
    * @return
    *   Try containing base64-encoded encrypted content with IV prepended
    */
  def encrypt(plaintext: String): Try[String] = Try {
    val key = generateKeyFromEnvironment
    val cipher = Cipher.getInstance(TRANSFORMATION)

    // Generate random IV
    val iv = new Array[Byte](IV_LENGTH)
    new SecureRandom().nextBytes(iv)
    val ivSpec = new IvParameterSpec(iv)

    cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
    val encryptedBytes =
      cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8))

    // Prepend IV to encrypted data and encode as Base64
    val combined = iv ++ encryptedBytes
    Base64.getEncoder.encodeToString(combined)
  }

  /** Decrypts the given encrypted content.
    *
    * @param encryptedContent
    *   base64-encoded encrypted content with IV prepended
    * @return
    *   Try containing the decrypted plaintext
    */
  def decrypt(encryptedContent: String): Try[String] = Try {
    val key = generateKeyFromEnvironment
    val cipher = Cipher.getInstance(TRANSFORMATION)

    // Decode from Base64
    val combined = Base64.getDecoder.decode(encryptedContent)

    // Extract IV and encrypted data
    val iv = combined.take(IV_LENGTH)
    val encryptedData = combined.drop(IV_LENGTH)
    val ivSpec = new IvParameterSpec(iv)

    cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
    val decryptedBytes = cipher.doFinal(encryptedData)

    new String(decryptedBytes, StandardCharsets.UTF_8)
  }

  /** Encrypts content and adds a header to identify it as encrypted MAEDN data.
    *
    * @param plaintext
    *   the content to encrypt
    * @return
    *   Try containing encrypted content with MAEDN header
    */
  def encryptWithHeader(plaintext: String): Try[String] = {
    encrypt(plaintext).map(encrypted => s"MAEDN_ENCRYPTED:$encrypted")
  }

  /** Decrypts content, verifying the MAEDN header first.
    *
    * @param content
    *   the encrypted content with header
    * @return
    *   Try containing the decrypted plaintext
    */
  def decryptWithHeader(content: String): Try[String] = {
    if (content.startsWith("MAEDN_ENCRYPTED:")) {
      val encryptedData = content.substring("MAEDN_ENCRYPTED:".length)
      decrypt(encryptedData)
    } else {
      Failure(
        new IllegalArgumentException(
          "Content is not a valid encrypted MAEDN file"
        )
      )
    }
  }

  /** Checks if the given content appears to be encrypted.
    *
    * @param content
    *   the content to check
    * @return
    *   true if the content appears to be encrypted
    */
  def isEncrypted(content: String): Boolean = {
    content.startsWith("MAEDN_ENCRYPTED:")
  }
}
