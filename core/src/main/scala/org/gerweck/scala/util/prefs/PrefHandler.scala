package org.gerweck.scala.util.prefs

import java.util.prefs.Preferences

trait PrefHandler[A] {
  def read(path: String, default: A)(implicit p: Preferences): A
  def write(path: String, a: A)(implicit p: Preferences): Unit
}

/* PlatformPrefHandlers are defined in the java-version-specific folders */
object PrefHandler extends CommonPrefHandlers with PlatformPrefHandlers
