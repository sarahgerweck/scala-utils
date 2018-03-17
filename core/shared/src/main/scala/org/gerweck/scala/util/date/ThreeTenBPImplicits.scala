package org.gerweck.scala.util.date

import language.implicitConversions

import org.threeten.{ bp => tt }

import scala.concurrent.duration.FiniteDuration

import org.gerweck.scala.util._

/** Implicit conversions for JSR-310 Backport objects.
  *
  * @author Sarah Gerweck <sarah@atscale.com>
  */
trait ThreeTenBPImplicits {
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

  implicit def ttDurationAsFiniteDuration(ttDur: tt.Duration): FiniteDuration = {
    // We don't have to worry about durations too big to fit in a long of
    // nanos, because `FiniteDuration` simply can't represent them no matter
    // how you construct it. (It's also roughly 292 years, so not too likely
    // to come up in practice.)
    scala.concurrent.duration.Duration.fromNanos(ttDur.toNanos)
  }

  implicit def finiteDurationAsTTDuration(dur: FiniteDuration): tt.Duration = {
    tt.Duration.of(dur.length, dur.unit)
  }

  import ThreeTenBPWrappers._
  @inline implicit final def enrichTTDate(i: tt.LocalDate): RichTTDate = new RichTTDate(i)
  @inline implicit final def enrichTTDateTime(i: tt.LocalDateTime): RichTTDateTime = new RichTTDateTime(i)
  @inline implicit final def enrichTTInstant(i: tt.Instant): RichTTInstant = new RichTTInstant(i)
  @inline implicit final def enrichTTTemporalAmount(i: tt.temporal.TemporalAmount): RichTTTemporalAmount = new RichTTTemporalAmount(i)
  @inline implicit final def enrichTTDuration(i: tt.Duration): RichTTDuration = new RichTTDuration(i)
  @inline implicit final def enrichDateTimeFormatter(i: tt.format.DateTimeFormatter): RichDateTimeFormatter = new RichDateTimeFormatter(i)
}

object ThreeTenBPWrappers extends ThreeTenBPImplicits {
  /* ====================================================================== */
  /*                   ThreeTen Backport enhanced objects                   */
  /* ====================================================================== */

  final class RichTTDate(val inner: tt.LocalDate) extends AnyVal with UniversalOrdering[tt.LocalDate] {
    def + (add: tt.Duration) = {
      // I think it's a bug in ThreeTen, but plus with a duration doesn't work for dates as of Dec 2014.
      import tt.temporal.ChronoUnit

      require(tt.Duration.of(add.toDays(), ChronoUnit.DAYS) == add, "Cannot add a time-based duration to a date")

      inner.plus(add.toDays(), tt.temporal.ChronoUnit.DAYS)
    }

    def - (sub: tt.Duration) = {
      this + sub.negated
    }
    def - (sub: tt.LocalDate): tt.Period = sub until inner
  }

  final class RichTTDateTime(val inner: tt.LocalDateTime) extends AnyVal with UniversalOrdering[tt.LocalDateTime] {
    def getQuarter = inner.get(tt.temporal.IsoFields.QUARTER_OF_YEAR)

    def + (p: tt.temporal.TemporalAmount) = inner plus p
    def + (d: FiniteDuration) = inner plus (d.toMillis, tt.temporal.ChronoUnit.MILLIS)

    def - (other: tt.LocalDateTime): tt.Duration = tt.Duration.between(other, inner)
    def - (p: tt.temporal.TemporalAmount): tt.LocalDateTime = inner minus p
    def - (d: FiniteDuration): tt.LocalDateTime = inner minus (d.toMillis, tt.temporal.ChronoUnit.MILLIS)

    def toSqlTimestamp = tt.DateTimeUtils.toSqlTimestamp(inner)
  }

  final class RichTTInstant(val inner: tt.Instant) extends AnyVal with UniversalOrdering[tt.Instant] {
    def - (other: tt.Instant): tt.Duration = tt.Duration.between(other, inner)
    def - (duration: tt.temporal.TemporalAmount): tt.Instant = inner minus duration
    def - (d: FiniteDuration) = inner minus (d.toMillis, tt.temporal.ChronoUnit.MILLIS)

    def + (duration: tt.temporal.TemporalAmount): tt.Instant = inner plus duration
    def + (d: FiniteDuration) = inner plus (d.toMillis, tt.temporal.ChronoUnit.MILLIS)

    def isOlderThan(d: FiniteDuration): Boolean = this < (tt.Instant.now() - d)
    def isNewerThan(d: FiniteDuration): Boolean = this > (tt.Instant.now() - d)

    def toSqlTimestamp = tt.DateTimeUtils.toSqlTimestamp(inner)
  }

  final class RichTTTemporalAmount(val inner: tt.temporal.TemporalAmount) extends AnyVal {
    @inline def + (instant: tt.Instant): tt.Instant = instant + inner
    @inline def + (ldt: tt.LocalDateTime): tt.LocalDateTime = ldt + inner
  }

  final class RichTTDuration(val inner: tt.Duration) extends AnyVal with UniversalOrdering[tt.Duration] {
    def toDouble: Double = inner.getNano * 1e-9 + inner.getSeconds
    def toFloat: Float = inner.getNano * 1e-9f + inner.getSeconds
    def toHuman: String = formatDuration(toFloat)

    def toScalaDuration: FiniteDuration = FiniteDuration(inner.toNanos, scala.concurrent.duration.NANOSECONDS)

    def / (div: tt.Duration): Double = toDouble / div.toDouble
  }

  final class RichDateTimeFormatter(val inner: tt.format.DateTimeFormatter) extends AnyVal {
    def apply(ta: tt.temporal.TemporalAccessor): String = inner.format(ta)
  }
}
