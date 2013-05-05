package org.gerweck.scala.util

import java.lang.System.{ nanoTime, currentTimeMillis => millisTime }

import org.log4s._

object timed {
  private[this] val logger = getLogger
  
  @inline def apply[A](f: => A): A = apply()(f)
  
  @inline def apply[A](logger: Logger = logger, taskName: String = "task", level: LogLevel = Debug)(f: => A): A = {
    val startTime = nanoTime
    var failed = false
    try {
      f
    } catch { case e: Throwable => 
      failed = true
      throw e
    } finally {
      val finishTime = nanoTime
      logger(level)(s"${taskName.capitalize} ${if (failed) "failed" else "completed"} after ${formatDuration(1e-9f * (finishTime - startTime))}")
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
  
  @inline def formatSince(startTime: Long): String = {
    val finishTime = nanoTime
    formatDuration(1e-9f * (finishTime - startTime))
  }
}