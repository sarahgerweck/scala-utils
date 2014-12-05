package org.gerweck.scala.util

import scala.concurrent._

import java.lang.System.{ nanoTime, currentTimeMillis => millisTime }

import org.log4s._

object timed {
  private[this] val logger = getLogger

  def apply[A](f: => A): A = apply()(f)

  def apply[A](logger: Logger = logger, taskName: String = "task", level: LogLevel = Debug)(f: => A): A = {
    var failed = false
    val startTime = nanoTime
    try {
      f
    } catch { case e: Throwable =>
      failed = true
      throw e
    } finally {
      val finishTime = nanoTime
      @inline def time = date.formatDuration(1e-9f * (finishTime - startTime))
      @inline def status = if (failed) "failed" else "completed"
      logger(level)(s"${taskName.capitalize} $status after $time")
    }
  }

  @inline def formatNanos(start: Long, finish: Long): String = formatNanos(finish - start)

  @inline def formatNanos(total: Long): String = date.formatDuration(1e-9f * total)

  @inline def formatSince(startTime: Long): String = {
    val finishTime = nanoTime
    date.formatDuration(1e-9f * (finishTime - startTime))
  }
}

/** Timer that operates on a [[scala.concurrent.Future]].
  *
  * '''Warning''': This is not as accurate as [[org.gerweck.scala.util.timed]], because it relies
  * on scheduling another job to run ''after'' the original future is complete.
  *
  * Whenever possible, use `timed` instead.
  */
object timedFuture {
  private[this] val logger = getLogger
  import timed._

  def apply[A](logger: Logger = logger, taskName: String = "task", level: LogLevel = Debug)(f: => Future[A])(implicit ec: ExecutionContext): Future[A] = {
    val startTime = nanoTime
    val future = f
    f onComplete { result =>
      val finishTime = nanoTime
      @inline def time = date.formatDuration(1e-9f * (finishTime - startTime))
      @inline def status = if (result.isFailure) "failed" else "completed"
      logger(level)(s"${taskName.capitalize} $status after $time")
    }
    f
  }
}
