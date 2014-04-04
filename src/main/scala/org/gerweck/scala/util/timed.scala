package org.gerweck.scala.util

import scala.concurrent._

import java.lang.System.{ nanoTime, currentTimeMillis => millisTime }

import org.log4s._

object timed {
  private[this] val logger = getLogger

  def apply[A](f: => A): A = apply()(f)

  def future[A](logger: Logger = logger, taskName: String = "task", level: LogLevel = Debug)(f: => Future[A])(implicit ec: ExecutionContext): Future[A] = {
    val startTime = nanoTime
    val future = f
    f onComplete { result =>
      val finishTime = nanoTime
      @inline def time = formatDuration(1e-9f * (finishTime - startTime))
      @inline def status = if (result.isFailure) "failed" else "completed"
      logger(level)(s"${taskName.capitalize} $status after $time")
    }
    f
  }

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
      @inline def time = formatDuration(1e-9f * (finishTime - startTime))
      @inline def status = if (failed) "failed" else "completed"
      logger(level)(s"${taskName.capitalize} $status after $time")
    }
  }

  def formatDuration(t: Float): String = {
    require(t >= 0f, "Cannot format a negative duration")
    if      (t == 0f)    "0 s"
    else if (t < 1e-6f) "< 1 μs"
    else if (t < 1e-3f) f"${1e6f * t}%.3g μs"
    else if (t < 1f)    f"${1e3f * t}%.3g ms"
    else if (t < 1e3f)  f"$t%.3g s"
    else if (t < 1e6f)  f"${1e-3f * t}%.3g ks"
    else                f"$t%.4g s"
  }

  @inline def formatNanos(start: Long, finish: Long): String = formatNanos(finish - start)

  @inline def formatNanos(total: Long): String = formatDuration(1e-9f * total)

  @inline def formatSince(startTime: Long): String = {
    val finishTime = nanoTime
    formatDuration(1e-9f * (finishTime - startTime))
  }
}
