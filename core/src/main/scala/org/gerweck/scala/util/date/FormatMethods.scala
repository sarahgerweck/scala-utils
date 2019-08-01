package org.gerweck.scala.util.date

import scala.concurrent.duration.FiniteDuration

/** Methods for formatting time durations.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
trait FormatMethods {
  /** Take an amount of time and format it as a user-friendly string.
    *
    * The output string is more human readable than machine friendly: it uses
    * SI prefixes and always has roughly three digits of precision. This makes
    * it easier to see at a glance whether you're talking about milliseconds,
    * seconds, nanoseconds, etc.
    *
    * @param seconds The amount of elapsed time, in seconds. This may not be
    * negative.
    */
  final def formatDuration(seconds: Double): String = {
    require(seconds >= 0d, "Cannot format a negative duration")

    @inline def isMilliPrecision(): Boolean = {
      // This will give a false positive 0.00001% of the time, which is good
      // enough for our purposes. These values are meant to be human readable.
      // If they're getting used systematically, they should go in a database
      // or some kind of structured export.
      (1e+10d * seconds).round % 10000000 == 0
    }
    @inline def formatMillis(): String = {
      // Sometimes we'll have nanosecond precision, and other time millisecond
      // precision. We don't want to say things like 4.00 ms when we don't
      // know to that level of detail. (It's misleading & looks weird.)
      if (isMilliPrecision && seconds < 1f) {
        s"${(1e+3d * seconds).round} ms"
      } else {
        f"${1e+3d * seconds}%.3g ms"
      }
    }

    if      (seconds == 0d)         "0 s"
    else if (seconds <  0.9995e-6d) "< 1 μs"
    else if (seconds <  0.9995e-3d) f"${1e+6d * seconds}%.3g μs"
    else if (seconds <  0.9995e+0d) formatMillis()
    else if (seconds <  0.9995e+3d) f"${        seconds}%.3g s"
    else if (seconds <  0.9995e+6d) f"${1e-3d * seconds}%.3g ks"
    else                            f"${        seconds}%.4g s"
  }

  @inline final def formatDuration(seconds: Float): String = formatDuration(seconds.toDouble)

  @inline final def formatDuration(duration: FiniteDuration): String = formatDuration(duration.toNanos * 1e-9d)
}
