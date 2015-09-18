package org.gerweck.scala.util.io

import java.io._

/** A stream inverter lets you transform an `InputStream` to an `OutputStream`
  * or vice versa.
  *
  * It's often the case that you need to pass a particular kind of stream into
  * an API. In some situations, especially if you're using something like Akka
  * Streams, you may really want to have the opposite of what you need to pass
  * in.
  *
  * E.g., when receive a document upload, a legacy API may ask you to pass
  * in an `OutputStream` to which it will push the bytes. In a streaming
  * context, what you really want is for that API to give you an `InputStream`
  * from which you will pull the bytes (or which you'll use to create a
  * stream `Source`).
  *
  * A `StreamInverter` provides exactly that conversion.
  *
  * '''Warning''': the jobs on each side of the inverter both need to be
  * running on their own thread or you are likely to starve the buffer and
  * lock the application. In the streaming context, this often means invoking
  * the traditional API in a [[scala.concurrent.Future]]. IO jobs typically
  * hold threads for a long time, so you probably want to have a dedicated
  * [[scala.concurrent.ExecutionContext]] for those futures. A good option
  * is [[org.gerweck.scala.util.io.ioExecutionContext]], which is a premade
  * context with good settings for dedicated IO threads.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
class StreamInverter private (val input: InputStream, val output: OutputStream)

object StreamInverter {
  private[this] final val defaultBufferSize = 4096

  def apply(bufferSize: Int = defaultBufferSize): StreamInverter = {
    val is = new PipedInputStream(bufferSize)
    val os = new PipedOutputStream(is)
    new StreamInverter(is, os)
  }
}
