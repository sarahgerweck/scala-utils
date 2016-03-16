package org.gerweck.scala.util.date

import language.implicitConversions

import scala.concurrent.duration._

/** Implicit wrappers that enrich the Scala [[scala.concurrent.duration.Duration]] object.
  *
  * @author Sarah Gerweck <sarah@atscale.com>
  */
trait ScalaDurationImplicits {
  import ScalaDurationWrappers._
  @inline implicit final def enrichScalaDuration(d: Duration) = new RichScalaDuration(d)
}

object ScalaDurationWrappers {
  final class RichScalaDuration(val inner: Duration) extends AnyVal {
    /* Scala Durations always act on nanoseconds, so we can assume those are the values we have.
     *
     * In the case of infinite durations, the contract of Duration says that `toUnit` will yield
     * `NegativeInfinity`, `NaN` or `PositiveInfinity`, so it actually doesn't matter what unit
     * we give. We give `NANOSECONDS` just to be safest against possible code changes in the
     * future. Doing a `toFloat` on non-finite durations works as expected.
     */

    def toDouble = {
      inner match {
        case _: FiniteDuration    => inner.toNanos * 1e-9d
        case i: Duration.Infinite => i.toUnit(NANOSECONDS)
      }
    }
    def toFloat = this.toDouble.toFloat
    def toHuman: String = formatDuration(toDouble)
  }
}
