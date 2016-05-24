package org.gerweck.scala.util.date

import java.time.Duration
import java.time.temporal._

/** A Java 8 [[java.time.temporal.TemporalQuery]] that gives a [[java.time.Duration]].
  *
  * It will look for years, months, days of month, hours, minutes, seconds,
  * millis or nanos. This will '''not''' work with a week-based duration.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
object DurationTemporalQuery extends TemporalQuery[Duration] {
  private[this] val durationFields = {
    import ChronoField._
    Vector(YEAR, MONTH_OF_YEAR, DAY_OF_MONTH, HOUR_OF_DAY, MINUTE_OF_HOUR, SECOND_OF_MINUTE, NANO_OF_SECOND)
  }
  override def queryFrom(ta: TemporalAccessor): Duration = {
    var duration = Duration.ZERO
    for {
      fld <- durationFields
      if ta.isSupported(fld)
    } {
      val amt = ta.getLong(fld)
      duration += (amt, fld.getBaseUnit)
    }
    duration
  }
}
