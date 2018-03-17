package org.gerweck.scala.util.hashing

import java.nio.ByteBuffer
import java.nio.charset.{ Charset, StandardCharsets }

/** An algorithm that can provide hash operations.
  *
  * This acts as a factory that you can use to get a new instance of an in-progress hash
  * operation. Hashing uses stateful objects that soak up data progressively until you have fed
  * all of the required data. This allows the hashing of large data structures that may not fit
  * entirely in memory.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
abstract class HashAlgorithm {
  def name: String
  def initialize(): HashAlgorithm.HashingState

  override def toString: String = name

  /** Get the hash of an array of bytes. */
  def hash(bytes: Array[Byte]): Array[Byte] = {
    val a = initialize()
    a.update(bytes)
    a.digest()
  }

  /** Get the hash of a [[java.nio.ByteBuffer]].
    *
    * @note This is, in some situations, slower than finding the hash of an array. If you have
    * both versions of the data available, the byte array should be preferred. However, it is
    * probably not advisable to do your own conversion to a byte array.
    */
  def hash(byteBuffer: ByteBuffer): Array[Byte] = {
    val a = initialize()
    a.update(byteBuffer)
    a.digest()
  }

  /** Get the hash of a sequence of bytes.
    *
    * @note it is '''not''' safe to utilize the hash of a collection without a defined sequence.
    * Hashing something like a set or multiset requires algorithmic choices to produce a
    * determinstic sequence of bytes.
    */
  def hash(bytes: scala.collection.Seq[Byte]): Array[Byte] = {
    hash(bytes.toArray)
  }

  /** Get the hash of a string of text. */
  def hash(string: String, encoding: Charset = StandardCharsets.UTF_8): Array[Byte] = {
    hash(string.getBytes(encoding))
  }
}

object HashAlgorithm {
  /** The state of a hash algorithm in the process of hashing data.
    *
    * Hashing utilizes a state machine that soaks up progressive bytes of data, producing a digest
    * at the end. This state object should be used to produce one hash and then discarded unless a
    * subclass offers a documented mechanism for reuse.
    */
  trait HashingState {
    def update(bytes: Array[Byte]): Unit
    def update(bytes: ByteBuffer): Unit = {
      val ba = new Array[Byte](bytes.remaining)
      bytes.get(ba)
      update(ba)
    }
    def digest(): Array[Byte]
  }
}
