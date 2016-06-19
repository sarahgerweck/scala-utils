package org.gerweck.scala.util.prefs

import scala.concurrent.duration.{ FiniteDuration => SFiniteDuration }

import java.time.{ Duration => JDuration }

import org.gerweck.scala.util.mapping.Homomorphism

trait PlatformPrefHandlers extends CommonPrefHandlers {
  implicit val jDurationHandler: PrefHandler[JDuration] = {
    implicit val morphism = Homomorphism[String, JDuration](JDuration.parse _, _.toString)
    mappedPrefHandler[String, JDuration]
  }

  implicit val sDurationHandler: PrefHandler[SFiniteDuration] = {
    import org.gerweck.scala.util.date._
    implicit val morphism = Homomorphism[JDuration, SFiniteDuration](identity, identity)
    mappedPrefHandler[JDuration, SFiniteDuration]
  }
}
