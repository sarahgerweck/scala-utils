package org.gerweck.scala.util.stream.impl

import scala.concurrent._
import scala.util._

import java.io.Closeable

import akka.Done

private[stream] class StreamPair[BaseStream <: Closeable, WrappedStream <: Closeable](val bs: BaseStream, val ws: WrappedStream) {
  private[this] final val forceUnderlyingClose = false
  def close(ioDispatcher: ExecutionContext): Future[Done] = {
    Future {
      val zc = Try { ws.close() }
      if (forceUnderlyingClose) {
        val fc = Try { bs.close() }
        zc.flatMap(Function.const(fc)).get
      } else {
        zc.get
      }
      Done
    }(ioDispatcher)
  }
}

private[stream] object StreamPair {
  def apply[BaseStream <: Closeable, WrappedStream <: Closeable]
      (bs: BaseStream)
      (wrapper: BaseStream => WrappedStream)
      (implicit ioDispatcher: ExecutionContext)
      : Future[StreamPair[BaseStream, WrappedStream]] = {
    Future {
      var ws = Option.empty[WrappedStream]
      Try {
        ws = Some(wrapper(bs))
        new StreamPair[BaseStream, WrappedStream](bs, ws.get)
      } .recover { case t =>
        ws.foreach { s => Try(s.close()) }
        Try(bs.close)
        throw t
      } .get
    }(ioDispatcher)
  }
}
