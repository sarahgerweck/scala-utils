package org.gerweck.scala.util

import org.joda.time._

package object date {
  implicit final class RichDate(val inner: LocalDateTime) extends AnyVal {
    def getQuarter = (inner.getMonthOfYear + 2) / 3

    def <  (that: LocalDateTime) = (inner compareTo that) <  0
    def <= (that: LocalDateTime) = (inner compareTo that) <= 0
    def >  (that: LocalDateTime) = (inner compareTo that) >  0
    def >= (that: LocalDateTime) = (inner compareTo that) >= 0

    def + (p: ReadablePeriod) = inner plus p
    def + (d: ReadableDuration) = inner plus d

    def - (p: ReadablePeriod) = inner minus p
    def - (d: ReadableDuration) = inner minus d
  }
}
