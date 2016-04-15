package org.gerweck.scala.util

import scala.concurrent._

import java.lang.System.{ nanoTime, currentTimeMillis => millisTime }

import org.log4s._

object timed {
  private[this] val logger = getLogger

  def apply[A](f: => A): A = apply()(f)

  def apply[A](logger: Logger = logger, taskName: String = "task", level: LogLevel = Debug)(f: => A): A = timed {
    var failed = false
    val startTime = nanoTime
    try {
      f
    } catch { case e: Throwable =>
      failed = true
      throw e
    } finally {
      val finishTime = nanoTime
      @inline def time = date.formatDuration(1e-9d * (finishTime - startTime))
      @inline def status = if (failed) "failed" else "completed"
      logger(level)(s"${taskName.capitalize} $status after $time")
    }
  }

  @inline def formatNanos(start: Long, finish: Long): String = formatNanos(finish - start)

  @inline def formatNanos(total: Long): String = date.formatDuration(1e-9d * total)

  @inline def formatSince(startTime: Long): String = {
    val finishTime = nanoTime
    date.formatDuration(1e-9d * (finishTime - startTime))
  }
}

object timedDynamic {
  private[this] val logger = getLogger

  def apply[A](logger: Logger = logger, taskName: String, successName: A => String, level: LogLevel = Debug)(f: => A): A = {
    var failed = false
    var resultName: Option[String] = None
    val startTime = nanoTime
    try {
      val a = f
      resultName = scala.util.Try(successName(a)).toOption
      a
    } catch { case e: Throwable =>
      failed = true
      throw e
    } finally {
      val finishTime = nanoTime
      val time = date.formatDuration(1e-9d * (finishTime - startTime))
      val name = resultName.getOrElse(taskName).capitalize
      val status = if (failed) "failed" else "completed"
      logger(level)(s"$name $status after $time")
    }
  }
}

/** Timer that operates on a [[scala.concurrent.Future]].
  *
  * '''Warning''': This is not as accurate as [[org.gerweck.scala.util.timed]], because it relies
  * on scheduling another job to run ''after'' the original future is complete.
  *
  * Whenever possible, use `timed` instead.
  *
  * The `inline` flag tells it to actually give you back a new Future that only completes when the
  * timer logging has been completed. This is a useful way to ensure that your log statements all
  * have the correct sequencing, with a small amount of overhead required to process the callback.
  */
object timedFuture {
  private[this] val logger = getLogger
  import timed._

  def apply[A](logger: Logger = logger, taskName: String = "task", level: LogLevel = Debug, captureMDC: Boolean = true, inline: Boolean = true)(f: => Future[A])(implicit ec: ExecutionContext): Future[A] = {
    val currentMDC = (if (captureMDC) MDC.toMap else Map.empty).toSeq
    val startTime = nanoTime
    val future = f

    @inline def doLog(success: Boolean) = {
      val finishTime = nanoTime
      @inline def time = date.formatDuration(1e-9d * (finishTime - startTime))
      @inline def status = if (success) "completed" else "failed"
      @inline def doLog() = logger(level)(s"${taskName.capitalize} $status after $time")
      if (captureMDC) {
        MDC.withCtx(currentMDC: _*) {
          doLog()
        }
      } else {
        doLog()
      }
    }

    if (inline) {
      future.transform(
        { result => doLog(true); result },
        { error  => doLog(false); error }
      )
    } else {
      future onComplete { result =>
        doLog(result.isSuccess)
      }
      future
    }
  }
}
