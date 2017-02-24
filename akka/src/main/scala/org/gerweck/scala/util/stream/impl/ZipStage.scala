package org.gerweck.scala.util.stream
package impl

import scala.concurrent._

import akka.stream._
import akka.stream.stage._
import akka.stream.IOResult

import org.gerweck.scala.util.io.ioExecutorContext

/** An internal graph stage implementation that interacts with zip data.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
private[stream] abstract class ZipStage[Shape1 <: Shape, Stream1](ec: ExecutionContext) extends GraphStageWithMaterializedValue[Shape1, (Promise[Stream1], Future[IOResult])] {
  protected[this] def callbackDispatcher = ec
  /* TODO: Make this configurable. */
  protected[this] def ioDispatcher = ioExecutorContext
}
