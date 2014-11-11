package org.gerweck.scala.util

import org.joda.time._

import scala.concurrent.duration.FiniteDuration

package object date {
  implicit final class RichDate(val inner: LocalDateTime) extends AnyVal {
    def getQuarter = (inner.getMonthOfYear + 2) / 3

    def <  (that: LocalDateTime) = (inner compareTo that) <  0
    def <= (that: LocalDateTime) = (inner compareTo that) <= 0
    def >  (that: LocalDateTime) = (inner compareTo that) >  0
    def >= (that: LocalDateTime) = (inner compareTo that) >= 0

    def + (p: ReadablePeriod) = inner plus p
    def + (d: ReadableDuration) = inner plus d
    def + (d: FiniteDuration) = inner plus Duration.millis(d.toMillis)

    def - (p: ReadablePeriod) = inner minus p
    def - (d: ReadableDuration) = inner minus d
    def - (d: FiniteDuration) = inner minus Duration.millis(d.toMillis)
  }

  implicit final class RichInstant(val inner: Instant) extends AnyVal {
    def - (other: Instant): Duration = new Duration(inner.getMillis - other.getMillis)
    def - (duration: Long): Instant = inner minus duration
    def - (duration: ReadableDuration): Instant = inner minus duration
    def - (d: FiniteDuration) = inner minus Duration.millis(d.toMillis)

    def + (duration: Long): Instant = inner plus duration
    def + (duration: ReadableDuration): Instant = inner plus duration
    def + (d: FiniteDuration) = inner plus Duration.millis(d.toMillis)

    def <  (that: Instant) = (inner compareTo that) <  0
    def <= (that: Instant) = (inner compareTo that) <= 0
    def >  (that: Instant) = (inner compareTo that) >  0
    def >= (that: Instant) = (inner compareTo that) >= 0
  }
}
