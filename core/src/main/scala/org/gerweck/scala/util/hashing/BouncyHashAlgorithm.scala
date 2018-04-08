package org.gerweck.scala.util.hashing

import java.nio.ByteBuffer

import org.bouncycastle.crypto._
import org.bouncycastle.crypto.digests._

/** A hash algorithm provided by the [[https://www.bouncycastle.org/ Legion of the Bouncy Castle]].
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
trait BouncyHashAlgorithm extends HashAlgorithm

object BouncyHashAlgorithm extends StandardHashAlgorithms {
  type AlgorithmType = BouncyHashAlgorithm

  sealed abstract class AbstractBouncyHashAlgorithm private[BouncyHashAlgorithm](final val name: String, final val outBytes: Int) extends BouncyHashAlgorithm with SizedHashAlgorithm {
    type D <: Digest
    protected[this] def instantiate(): D

    protected[this] class AlgorithmState extends HashAlgorithm.HashingState {
      protected[this] final val data: D = instantiate()
      override def update(bytes: Array[Byte]) = data.update(bytes, 0, bytes.length)
      override def update(bb: ByteBuffer) = {
        if (bb.hasArray) {
          data.update(bb.array, bb.arrayOffset() + bb.position(), bb.remaining())
        } else {
          super.update(bb)
        }
      }
      override def digest() = {
        val arr = new Array[Byte](outBytes)
        data.doFinal(arr, 0)
        arr
      }
    }

    override def initialize() = new AlgorithmState
  }

  sealed abstract class SimpleBouncyHashAlgorithm(name: String, outBytes: Int) extends AbstractBouncyHashAlgorithm(name, outBytes) {
    type D = Digest
  }

  object md5 extends SimpleBouncyHashAlgorithm("MD5", 16) {
    def instantiate() = new MD5Digest
  }
  object sha1 extends SimpleBouncyHashAlgorithm("SHA-1", 20) {
    def instantiate() = new SHA1Digest
  }
  object sha_256 extends SimpleBouncyHashAlgorithm("SHA-256", 32) {
    def instantiate() = new SHA256Digest
  }

  sealed abstract class Blake2bAlgorithm(protected[this] final val bits: Int)
      extends SimpleBouncyHashAlgorithm("BLAKE2b-" + bits, bits >> 3) {
    def instantiate = new Blake2bDigest(bits)
  }
  object blake2b_160 extends Blake2bAlgorithm(160)
  object blake2b_256 extends Blake2bAlgorithm(256)
  object blake2b_384 extends Blake2bAlgorithm(384)
  object blake2b_512 extends Blake2bAlgorithm(512)

  sealed abstract class ShakeAlgorithm(name: String, protected[this] final val bits: Int)
      extends AbstractBouncyHashAlgorithm(name, bits >> 2) {
    override type D = SHAKEDigest
    def instantiate: SHAKEDigest = new SHAKEDigest(bits)
    protected[this] class ShakeState extends AlgorithmState {
      override def digest() = {
        val arr = new Array[Byte](outBytes)
        data.doOutput(arr, 0, outBytes)
        arr
      }
    }
    override def initialize() = new ShakeState
  }
  object shake_128 extends ShakeAlgorithm("SHAKE-128", bits = 128)
  object shake_256 extends ShakeAlgorithm("SHAKE-256", bits = 256)

  sealed abstract class Sha3Algorithm private[BouncyHashAlgorithm](name: String, protected[this] final val bits: Int)
      extends SimpleBouncyHashAlgorithm(name, bits >> 3) {
    def instantiate = new SHA3Digest(bits)
  }
  object sha3_224 extends Sha3Algorithm("SHA3-224", bits = 224)
  object sha3_256 extends Sha3Algorithm("SHA3-256", bits = 256)
  object sha3_384 extends Sha3Algorithm("SHA3-384", bits = 384)
  object sha3_512 extends Sha3Algorithm("SHA3-512", bits = 512)

}
