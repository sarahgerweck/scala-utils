package org.gerweck.scala.util.prefs

import java.util.prefs.Preferences

trait PrefHandler[A] {
  protected[prefs] def read(path: String)(implicit p: Preferences): Option[A]
  protected[prefs] def write(path: String, a: A)(implicit p: Preferences): Unit

  protected[prefs] def remove(path: String)(implicit p: Preferences): Unit = {
    p.remove(path)
  }

  protected[prefs] def readWithDefault(path: String, default: A)(implicit p: Preferences): A = {
    assert(default != null, "Null values should be rejected by other code in this package")
    read(path).getOrElse(default)
  }
}

/* PlatformPrefHandlers are defined in the java-version-specific folders */
object PrefHandler extends CommonPrefHandlers with PlatformPrefHandlers
