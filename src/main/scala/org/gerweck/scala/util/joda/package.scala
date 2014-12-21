package org.gerweck.scala.util

import scala.concurrent.duration._

import org.joda.{ time => jt }

/** Implicit utilities for working with Joda Time.
  *
  * These have moved out of the `date` package as I now recommend that you
  * use the new JSR 310 libraries, either from Java 8 directly or through the
  * ThreeTenBP backport project.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
package object joda {

  /* ====================================================================== */
  /*                       Joda-Time enhanced objects                       */
  /* ====================================================================== */

  implicit final class RichJodaDateTime(val inner: jt.LocalDateTime) extends AnyVal with UniversalOrdering[jt.LocalDateTime] {
    def getQuarter = (inner.getMonthOfYear + 2) / 3

    def + (p: jt.ReadablePeriod) = inner plus p
    def + (d: jt.ReadableDuration) = inner plus d
    def + (d: FiniteDuration) = inner plus jt.Duration.millis(d.toMillis)

    def - (p: jt.ReadablePeriod) = inner minus p
    def - (d: jt.ReadableDuration) = inner minus d
    def - (d: FiniteDuration) = inner minus jt.Duration.millis(d.toMillis)
  }

  implicit final class RichJodaInstant(val inner: jt.Instant) extends AnyVal with UniversalOrdering[jt.Instant] {
    def - (other: jt.Instant): jt.Duration = new jt.Duration(inner.getMillis - other.getMillis)
    def - (duration: Long): jt.Instant = inner minus duration
    def - (duration: jt.ReadableDuration): jt.Instant = inner minus duration
    def - (d: FiniteDuration) = inner minus jt.Duration.millis(d.toMillis)

    def + (duration: Long): jt.Instant = inner plus duration
    def + (duration: jt.ReadableDuration): jt.Instant = inner plus duration
    def + (d: FiniteDuration) = inner plus jt.Duration.millis(d.toMillis)
  }

  implicit final class RichJodaDuration(val inner: jt.Duration) extends AnyVal with UniversalOrdering[jt.Duration] {
    def - (duration: jt.ReadableDuration): jt.Duration = inner minus duration
    def - (d: FiniteDuration): jt.Duration = inner minus d.toMillis

    def + (duration: jt.ReadableDuration): jt.Duration = inner plus duration
    def + (d: FiniteDuration): jt.Duration = inner plus d.toMillis
    @inline def + (i: jt.Instant): jt.Instant = i + inner
    @inline def + (d: jt.LocalDateTime): jt.LocalDateTime = d + inner

    def toDouble: Double = inner.getMillis * 1e-3
    def toFloat: Float = inner.getMillis * 1e-3f
    def toHuman: String = org.gerweck.scala.util.date.formatDuration(toFloat)
  }
}
