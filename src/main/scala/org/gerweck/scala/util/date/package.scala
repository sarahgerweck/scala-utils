package org.gerweck.scala.util

import org.joda.time._

package object date {
  implicit class RichDate(val inner: LocalDateTime) extends AnyVal {
    @inline def quarter = (inner.getMonthOfYear + 2) / 3
    @inline def getQuarter = quarter

    @inline def <  (that: LocalDateTime) = (inner compareTo that) <  0
    @inline def <= (that: LocalDateTime) = (inner compareTo that) <= 0
    @inline def >  (that: LocalDateTime) = (inner compareTo that) >  0
    @inline def >= (that: LocalDateTime) = (inner compareTo that) >= 0

    @inline def + (p: ReadablePeriod) = inner plus p
    @inline def + (d: ReadableDuration) = inner plus d

    @inline def - (p: ReadablePeriod) = inner minus p
    @inline def - (d: ReadableDuration) = inner minus d
  }
}
