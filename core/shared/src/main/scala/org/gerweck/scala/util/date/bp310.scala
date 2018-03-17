package org.gerweck.scala.util.date

import scala.util.Try

import org.threeten.bp.{ Clock, ZoneId, ZoneOffset }

/** An import package that always includes the 310 backport implicits.
  *
  * This should be used if you're using the Java 8 build of the utilities and
  * you're still using the JSR-310 backport libraries.
  */
package object bp310 extends ThreeTenBPImplicits {
  /** Get the default-timezone system clock, falling back to UTC.
    *
    * Unlike Java 8, the JSR-310 backport will throw exceptions in cases where
    * the system timezone is set to something that's not a real timezone, like
    * `US/Pacific-New`. (Unfortuantely a few versions of Ubuntu accidentally
    * shipped with this as their default timezone, even though there has never
    * been such a thing.)
    */
  def safeDefaultClock: Clock = {
    Try(Clock.systemDefaultZone()) getOrElse Clock.systemUTC()
  }
  /** Get the default system timezone, falling back to UTC.
    *
    * Unlike Java 8, the JSR-310 backport will throw exceptions in cases where
    * the system timezone is set to something that's not a real timezone, like
    * `US/Pacific-New`. (Unfortuantely a few versions of Ubuntu accidentally
    * shipped with this as their default timezone, even though there has never
    * been such a thing.)
    */
  def safeDefaultZone: ZoneId = {
    Try(ZoneId.systemDefault) getOrElse ZoneOffset.UTC
  }
}
