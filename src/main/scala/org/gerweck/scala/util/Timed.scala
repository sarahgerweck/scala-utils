package org.gerweck.scala.util

import java.lang.System.{ nanoTime, currentTimeMillis => millisTime }

import org.log4s._

object Timed {
  private[this] val logger = getLogger
  
  @inline def apply[A](f: => A): A = apply(logger, "Task")(f)
  
  @inline def apply[A](logger: Logger, taskName: String, level: LogLevel = Trace)(f: => A): A = {
    val startTime = nanoTime
    var failed = false
    try {
      f
    } catch { case e: Throwable => 
      failed = true
      throw e
    } finally {
      logger.trace(s"$taskName ${if (failed) "failed" else "completed"} after ${formatSince(startTime)}")
    }
  }
  
  def formatDuration(t: Float): String = {
    require(t >= 0f, "Cannot format a negative duration")
    if      (t == 0)    "0 s"
    else if (t < 1e-6f) "< 1 μs"
    else if (t < 1e-3f) f"${1e6f * t}%.4f μs"
    else if (t < 1f)    f"${1e3f * t}%.4f ms"
    else if (t < 1e3f)  f"$t%.4f s"
    else                f"$t%.4g s"
  }
  
  def formatSince(startTime: Long): String = formatDuration((nanoTime - startTime) * 1e-9f)
}