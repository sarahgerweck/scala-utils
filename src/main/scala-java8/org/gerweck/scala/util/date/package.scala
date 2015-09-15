package org.gerweck.scala.util

import language.implicitConversions

import java.{ time => tt }

import scala.concurrent.duration.FiniteDuration


package object date extends FormatMethods with JavaTimeImplicits
