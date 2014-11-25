package org.gerweck.scala.util

import org.joda.{ time => joda }
import org.threeten.{ bp => tt }

import scala.concurrent.duration.FiniteDuration

package object date {
  implicit final class RichDate(val inner: joda.LocalDateTime) extends AnyVal {
    def getQuarter = (inner.getMonthOfYear + 2) / 3

    def <  (that: joda.LocalDateTime) = (inner compareTo that) <  0
    def <= (that: joda.LocalDateTime) = (inner compareTo that) <= 0
    def >  (that: joda.LocalDateTime) = (inner compareTo that) >  0
    def >= (that: joda.LocalDateTime) = (inner compareTo that) >= 0

    def + (p: joda.ReadablePeriod) = inner plus p
    def + (d: joda.ReadableDuration) = inner plus d
    def + (d: FiniteDuration) = inner plus joda.Duration.millis(d.toMillis)

    def - (p: joda.ReadablePeriod) = inner minus p
    def - (d: joda.ReadableDuration) = inner minus d
    def - (d: FiniteDuration) = inner minus joda.Duration.millis(d.toMillis)
  }

  implicit final class RichInstant(val inner: joda.Instant) extends AnyVal {
    def - (other: joda.Instant): joda.Duration = new joda.Duration(inner.getMillis - other.getMillis)
    def - (duration: Long): joda.Instant = inner minus duration
    def - (duration: joda.ReadableDuration): joda.Instant = inner minus duration
    def - (d: FiniteDuration) = inner minus joda.Duration.millis(d.toMillis)

    def + (duration: Long): joda.Instant = inner plus duration
    def + (duration: joda.ReadableDuration): joda.Instant = inner plus duration
    def + (d: FiniteDuration) = inner plus joda.Duration.millis(d.toMillis)

    def <  (that: joda.Instant) = (inner compareTo that) <  0
    def <= (that: joda.Instant) = (inner compareTo that) <= 0
    def >  (that: joda.Instant) = (inner compareTo that) >  0
    def >= (that: joda.Instant) = (inner compareTo that) >= 0
  }

  implicit final class RichTTDate(val inner: tt.LocalDateTime) extends AnyVal {
    def getQuarter = inner.get(tt.temporal.IsoFields.QUARTER_OF_YEAR)

    def <  (that: tt.LocalDateTime) = (inner compareTo that) <  0
    def <= (that: tt.LocalDateTime) = (inner compareTo that) <= 0
    def >  (that: tt.LocalDateTime) = (inner compareTo that) >  0
    def >= (that: tt.LocalDateTime) = (inner compareTo that) >= 0

    def + (p: tt.temporal.TemporalAmount) = inner plus p
    def + (d: FiniteDuration) = inner plus (d.toMillis, tt.temporal.ChronoUnit.MILLIS)

    def - (p: tt.temporal.TemporalAmount) = inner minus p
    def - (d: FiniteDuration) = inner minus (d.toMillis, tt.temporal.ChronoUnit.MILLIS)
  }

  implicit final class RichTTInstant(val inner: tt.Instant) extends AnyVal {
    def - (other: tt.Instant): tt.Duration = tt.Duration.between(inner, other)
    def - (duration: tt.temporal.TemporalAmount): tt.Instant = inner minus duration
    def - (d: FiniteDuration) = inner minus (d.toMillis, tt.temporal.ChronoUnit.MILLIS)

    def + (duration: tt.temporal.TemporalAmount): tt.Instant = inner plus duration
    def + (d: FiniteDuration) = inner plus (d.toMillis, tt.temporal.ChronoUnit.MILLIS)

    def <  (that: tt.Instant) = (inner compareTo that) <  0
    def <= (that: tt.Instant) = (inner compareTo that) <= 0
    def >  (that: tt.Instant) = (inner compareTo that) >  0
    def >= (that: tt.Instant) = (inner compareTo that) >= 0
  }
}
