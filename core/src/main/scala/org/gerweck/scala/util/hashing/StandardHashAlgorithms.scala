package org.gerweck.scala.util.hashing

/** Mix-in trait providing algorithms that are guaranteed to be present in all JDK variants.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
trait StandardHashAlgorithms {
  type AlgorithmType <: HashAlgorithm
  /* The JDK guarantees that all three of these will be available. */

  /** The 256-bit SHA-2 hash algorithm. */
  val sha_256: AlgorithmType

  /** The SHA-1 hash algorithm.
    *
    * @note SHA-1 has been broken and should not be used unless it is required for backwards
    * compatibility. Use one of the SHA-2 algorithms, or SHA-3.
    */
  val sha1: AlgorithmType

  /** The MD5 hash algorithm.
    *
    * @note MD5 has been broken and should not be used unless it is required for backwards
    * compatibility. Use one of the SHA-2 algorithms, or SHA-3.
    */
  val md5: AlgorithmType
}
