package org.gerweck.scala.util.date

import language.implicitConversions

import java.{ time => jt }

import scala.concurrent.duration.FiniteDuration

import org.gerweck.scala.util._

/** Implicits for working with Java 8's `java.time` (JSR-310).
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
trait JavaTimeImplicits {
  implicit def timeUnitAsJTChronoUnit(unit: java.util.concurrent.TimeUnit): jt.temporal.ChronoUnit = {
    import java.util.concurrent.{ TimeUnit => TU }
    import jt.temporal.{ ChronoUnit => CU }
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

  implicit def jtDurationAsFiniteDuration(ttDur: jt.Duration): FiniteDuration = {
    // We don't have to worry about durations too big to fit in a long of
    // nanos, because `FiniteDuration` simply can't represent them no matter
    // how you construct it. (It's also roughly 292 years, so not too likely
    // to come up in practice.)
    scala.concurrent.duration.Duration.fromNanos(ttDur.toNanos)
  }

  implicit def finiteDurationAsJTDuration(dur: FiniteDuration): jt.Duration = {
    jt.Duration.of(dur.length, dur.unit)
  }

  import JavaTimeWrappers._
  @inline implicit final def enrichJTDate(i: jt.LocalDate): RichJTDate = new RichJTDate(i)
  @inline implicit final def enrichJTDateTime(i: jt.LocalDateTime): RichJTDateTime = new RichJTDateTime(i)
  @inline implicit final def enrichJTInstant(i: jt.Instant): RichJTInstant = new RichJTInstant(i)
  @inline implicit final def enrichJTTemporalAmount(i: jt.temporal.TemporalAmount): RichJTTemporalAmount = new RichJTTemporalAmount(i)
  @inline implicit final def enrichJTDuration(i: jt.Duration): RichJTDuration = new RichJTDuration(i)
  @inline implicit final def enrichDateTimeFormatter(i: jt.format.DateTimeFormatter): RichDateTimeFormatter = new RichDateTimeFormatter(i)
}

object JavaTimeImplicits extends JavaTimeImplicits

object JavaTimeWrappers {
  final class RichJTDate(val inner: jt.LocalDate) extends AnyVal with UniversalOrdering[jt.LocalDate] {
    def + (add: jt.Duration) = {
      // I think it's a bug in ThreeTen, but plus with a duration doesn't work for dates as of Dec 2014.
      import scala.collection.JavaConversions._
      import jt.temporal.ChronoUnit

      require(jt.Duration.of(add.toDays(), ChronoUnit.DAYS) == add, "Cannot add a time-based duration to a date")

      inner.plus(add.toDays(), jt.temporal.ChronoUnit.DAYS)
    }

    def - (sub: jt.Duration) = {
      this + sub.negated
    }
    def - (sub: jt.LocalDate): jt.Period = sub until inner
  }

  final class RichJTDateTime(val inner: jt.LocalDateTime) extends AnyVal with UniversalOrdering[jt.LocalDateTime] {
    def getQuarter = inner.get(jt.temporal.IsoFields.QUARTER_OF_YEAR)

    def + (p: jt.temporal.TemporalAmount) = inner plus p
    def + (d: FiniteDuration) = inner plus (d.toMillis, jt.temporal.ChronoUnit.MILLIS)

    def - (p: jt.temporal.TemporalAmount) = inner minus p
    def - (d: FiniteDuration) = inner minus (d.toMillis, jt.temporal.ChronoUnit.MILLIS)

    def toSqlTimestamp = java.sql.Timestamp.valueOf(inner)
  }

  final class RichJTInstant(val inner: jt.Instant) extends AnyVal with UniversalOrdering[jt.Instant] {
    def - (other: jt.Instant): jt.Duration = jt.Duration.between(other, inner)
    def - (duration: jt.temporal.TemporalAmount): jt.Instant = inner minus duration
    def - (d: FiniteDuration) = inner minus (d.toMillis, jt.temporal.ChronoUnit.MILLIS)

    def + (duration: jt.temporal.TemporalAmount): jt.Instant = inner plus duration
    def + (d: FiniteDuration) = inner plus (d.toMillis, jt.temporal.ChronoUnit.MILLIS)

    def isOlderThan(d: FiniteDuration): Boolean = this < (jt.Instant.now() - d)
    def isNewerThan(d: FiniteDuration): Boolean = this > (jt.Instant.now() - d)

    def toSqlTimestamp = java.sql.Timestamp.from(inner)
  }

  final class RichJTTemporalAmount(val inner: jt.temporal.TemporalAmount) extends AnyVal {
    @inline def + (instant: jt.Instant): jt.Instant = instant + inner
    @inline def + (ldt: jt.LocalDateTime): jt.LocalDateTime = ldt + inner
  }

  final class RichJTDuration(val inner: jt.Duration) extends AnyVal with UniversalOrdering[jt.Duration] {
    def toDouble: Double = inner.getNano * 1e-9 + inner.getSeconds
    def toFloat: Float = inner.getNano * 1e-9f + inner.getSeconds
    def toHuman: String = formatDuration(toFloat)

    def toScalaDuration: FiniteDuration = FiniteDuration(inner.toNanos, scala.concurrent.duration.NANOSECONDS)

    def / (div: jt.Duration): Double = toDouble / div.toDouble
  }

  final class RichDateTimeFormatter(val inner: jt.format.DateTimeFormatter) extends AnyVal {
    def apply(ta: jt.temporal.TemporalAccessor): String = inner.format(ta)
  }
}
