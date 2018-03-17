package org.gerweck.scala.util.hashing

/** A hash algorithm where the output size is known.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
trait SizedHashAlgorithm extends HashAlgorithm {
  val outBytes: Int
}
