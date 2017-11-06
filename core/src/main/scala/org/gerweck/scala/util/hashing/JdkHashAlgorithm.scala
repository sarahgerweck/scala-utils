package org.gerweck.scala.util.hashing

import java.security.MessageDigest
import java.nio.ByteBuffer

/** A hash algorithm provided by the JDK's [[java.security.MessageDigest]] mechanism.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
abstract class JdkHashAlgorithm extends HashAlgorithm {
  protected[this] def instantiate(): MessageDigest
  protected[this] val forceReset = false

  override def initialize() = {
    val md = instantiate()
    if (forceReset) {
      md.reset()
    }
    new JdkHashAlgorithm.HashingState(md)
  }
}
object JdkHashAlgorithm extends StandardHashAlgorithms {
  def forName(name: String) = new NamedJdkAlgorithm(name)

  class HashingState private[JdkHashAlgorithm] (val data: MessageDigest) extends HashAlgorithm.HashingState {
    override def update(bytes: ByteBuffer) = data.update(bytes)
    override def update(bytes: Array[Byte]) = data.update(bytes)
    override def digest() = data.digest()
  }

  type AlgorithmType = JdkHashAlgorithm

  class NamedJdkAlgorithm(final val name: String) extends JdkHashAlgorithm {
    override def instantiate() = MessageDigest.getInstance(name)
  }

  object md5 extends NamedJdkAlgorithm("MD5") with SizedHashAlgorithm {
    override final val outBytes = 16
  }
  object sha1 extends NamedJdkAlgorithm("SHA-1") with SizedHashAlgorithm {
    override final val outBytes = 20
  }
  object sha_256 extends NamedJdkAlgorithm("SHA-256") with SizedHashAlgorithm {
    override final val outBytes = 32
  }
}
