package org.gerweck.scala.util

import language.implicitConversions

import org.joda.{ time => joda }
import org.threeten.{ bp => tt }

import scala.concurrent.duration.FiniteDuration

package object date {
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
  def formatDuration(seconds: Float): String = {
    require(seconds >= 0f, "Cannot format a negative duration")

    if      (seconds == 0f)         "0 s"
    else if (seconds <  0.9995e-6f) "< 1 μs"
    else if (seconds <  0.9995e-3f) f"${1e+6f * seconds}%.3g μs"
    else if (seconds <  0.9995e+0f) f"${1e+3f * seconds}%.3g ms"
    else if (seconds <  0.9995e+3f) f"${        seconds}%.3g s"
    else if (seconds <  0.9995e+6f) f"${1e-3f * seconds}%.3g ks"
    else                            f"${        seconds}%.4g s"
  }

  @inline def formatDuration(seconds: Double): String = formatDuration(seconds.toFloat)

  /* ====================================================================== */
  /*                       Joda-Time enhanced objects                       */
  /* ====================================================================== */

  implicit final class RichJodaDate(val inner: joda.LocalDateTime) extends AnyVal with UniversalOrdering[joda.LocalDateTime] {
    def getQuarter = (inner.getMonthOfYear + 2) / 3

    def + (p: joda.ReadablePeriod) = inner plus p
    def + (d: joda.ReadableDuration) = inner plus d
    def + (d: FiniteDuration) = inner plus joda.Duration.millis(d.toMillis)

    def - (p: joda.ReadablePeriod) = inner minus p
    def - (d: joda.ReadableDuration) = inner minus d
    def - (d: FiniteDuration) = inner minus joda.Duration.millis(d.toMillis)
  }

  implicit final class RichJodaInstant(val inner: joda.Instant) extends AnyVal with UniversalOrdering[joda.Instant] {
    def - (other: joda.Instant): joda.Duration = new joda.Duration(inner.getMillis - other.getMillis)
    def - (duration: Long): joda.Instant = inner minus duration
    def - (duration: joda.ReadableDuration): joda.Instant = inner minus duration
    def - (d: FiniteDuration) = inner minus joda.Duration.millis(d.toMillis)

    def + (duration: Long): joda.Instant = inner plus duration
    def + (duration: joda.ReadableDuration): joda.Instant = inner plus duration
    def + (d: FiniteDuration) = inner plus joda.Duration.millis(d.toMillis)
  }

  implicit final class RichJodaDuration(val inner: joda.Duration) extends AnyVal with UniversalOrdering[joda.Duration] {
    def - (duration: joda.ReadableDuration): joda.Duration = inner minus duration
    def - (d: FiniteDuration): joda.Duration = inner minus d.toMillis

    def + (duration: joda.ReadableDuration): joda.Duration = inner plus duration
    def + (d: FiniteDuration): joda.Duration = inner plus d.toMillis
    @inline def + (i: joda.Instant): joda.Instant = i + inner
    @inline def + (d: joda.LocalDateTime): joda.LocalDateTime = d + inner

    def toDouble: Double = inner.getMillis * 1e-3
    def toFloat: Float = inner.getMillis * 1e-3f
    def toHuman: String = formatDuration(toFloat)
  }

  /* ====================================================================== */
  /*                   ThreeTen Backport enhanced objects                   */
  /* ====================================================================== */
  implicit def timeUnitAsTTChronoUnit(unit: java.util.concurrent.TimeUnit): tt.temporal.ChronoUnit = {
    import java.util.concurrent.{ TimeUnit => TU }
    import tt.temporal.{ ChronoUnit => CU }
    unit match {
      case TU.DAYS         => CU.DAYS
      case TU.HOURS        => CU.HOURS
      case TU.MICROSECONDS => CU.MICROS
      case TU.MILLISECONDS => CU.MILLIS
      case TU.MINUTES      => CU.MINUTES
      case TU.NANOSECONDS  => CU.NANOS
      case TU.SECONDS      => CU.SECONDS
    }
  }

  implicit def finiteDurationAsTTDuration(dur: FiniteDuration): tt.Duration = {
    tt.Duration.of(dur.length, dur.unit)
  }

  implicit final class RichTTDate(val inner: tt.LocalDate) extends AnyVal with UniversalOrdering[tt.LocalDate] {
    def + (add: tt.Duration) = {
      // I think it's a bug in ThreeTen, but plus with a duration doesn't work for dates as of Dec 2014.
      import scala.collection.JavaConversions._
      import tt.temporal.ChronoUnit

      require(tt.Duration.of(add.toDays(), ChronoUnit.DAYS) == add, "Cannot add a time-based duration to a date")

      inner.plus(add.toDays(), tt.temporal.ChronoUnit.DAYS)
    }

    def - (sub: tt.Duration) = {
      this + sub.negated
    }
    def - (sub: tt.LocalDate): tt.Period = sub until inner
  }

  implicit final class RichTTDateTime(val inner: tt.LocalDateTime) extends AnyVal with UniversalOrdering[tt.LocalDateTime] {
    def getQuarter = inner.get(tt.temporal.IsoFields.QUARTER_OF_YEAR)

    def + (p: tt.temporal.TemporalAmount) = inner plus p
    def + (d: FiniteDuration) = inner plus (d.toMillis, tt.temporal.ChronoUnit.MILLIS)

    def - (p: tt.temporal.TemporalAmount) = inner minus p
    def - (d: FiniteDuration) = inner minus (d.toMillis, tt.temporal.ChronoUnit.MILLIS)

    def toSqlTimestamp = tt.DateTimeUtils.toSqlTimestamp(inner)
  }

  implicit final class RichTTInstant(val inner: tt.Instant) extends AnyVal with UniversalOrdering[tt.Instant] {
    def - (other: tt.Instant): tt.Duration = tt.Duration.between(other, inner)
    def - (duration: tt.temporal.TemporalAmount): tt.Instant = inner minus duration
    def - (d: FiniteDuration) = inner minus (d.toMillis, tt.temporal.ChronoUnit.MILLIS)

    def + (duration: tt.temporal.TemporalAmount): tt.Instant = inner plus duration
    def + (d: FiniteDuration) = inner plus (d.toMillis, tt.temporal.ChronoUnit.MILLIS)

    def toSqlTimestamp = tt.DateTimeUtils.toSqlTimestamp(inner)
  }

  implicit final class RichTTTemporalAmount(val inner: tt.temporal.TemporalAmount) extends AnyVal {
    @inline def + (instant: tt.Instant): tt.Instant = instant + inner
    @inline def + (ldt: tt.LocalDateTime): tt.LocalDateTime = ldt + inner
  }

  implicit final class RichTTDuration(val inner: tt.Duration) extends AnyVal with UniversalOrdering[tt.Duration] {
    def toDouble: Double = inner.getNano * 1e-9 + inner.getSeconds
    def toFloat: Float = inner.getNano * 1e-9f + inner.getSeconds
    def toHuman: String = formatDuration(toFloat)

    def / (div: tt.Duration): Double = toDouble / div.toDouble
  }
}
