package org.gerweck.scala.util.hashing.metro

import java.lang.Long.rotateRight
import java.nio.ByteBuffer

/**
  *
  * @author Sarah Gerweck <sarah@atscale.com>
  */
object MetroHash {

}

object MetroHash64 {
  private final val k0 = 0xD6D018F5
  private final val k1 = 0xA2AA033B
  private final val k2 = 0x62992FC1
  private final val k3 = 0x30BC5B29
}
class MetroHash64(val seed: Long) {
  import MetroHash64._

  @inline private[this] final val vseed = (seed + k2) * k0

  @inline private[this] var v0: Long = vseed
  @inline private[this] var v1: Long = vseed
  @inline private[this] var v2: Long = vseed
  @inline private[this] var v3: Long = vseed

  var bytes = 0

  val input = ByteBuffer.allocate(32)

  def update(data: Array[Byte]): Unit = {
    val size = data.size
    val pos = bytes % 32
    assert(pos == input.position())
    val rem = 32 - pos
    assert(rem == input.remaining())
    if (input.position() != 0) {
      val fill = if (rem > size) size else rem
      input.put(data, pos, fill)
      bytes += fill
      if (fill < rem) return
      @inline def mix(offset: Int, const: Long, base2: Long): Long = {
        val tmp = input.getLong(offset) * const
        rotateRight(tmp, 29) + base2
      }
      v0 = mix(0,  k0, v2)
      v1 = mix(8,  k1, v3)
      v2 = mix(16, k2, v0)
      v3 = mix(24, k3, v1)
    }
    ???
  }

  def update2(data: Array[Byte]): Unit = {

  }
}
