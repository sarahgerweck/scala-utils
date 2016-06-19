package org.gerweck.scala.util.prefs

import scala.concurrent.duration.Duration

import java.io.File
import java.util.prefs.Preferences

import org.gerweck.scala.util.mapping.Homomorphism

/** A class that represents a preference stored in the Java preferences system.
  *
  * The expectation is that your application will make a global registry of
  * your preferences as `val`s in a central object, and then you'll then
  * import and use those as needed throughout the application.
  */
case class Pref[A](path: String, default: A)(implicit handler: PrefHandler[A], p: Preferences)  {
  def apply(): A = handler.read(path, default)
  def update(a: A): Unit = handler.write(path, a)
}
