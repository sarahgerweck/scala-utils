package org.gerweck.scala.util.prefs

import java.util.prefs.Preferences

/** A class that represents a preference stored in the Java preferences system.
  *
  * The expectation is that your application will make a global registry of
  * your preferences as `val`s in a central object, and then you'll then
  * import and use those as needed throughout the application.
  */
class Pref[A] protected (path: String)(implicit handler: Pref.AccessHandler[A], pref: Preferences) {
  def apply(): A = handler.read(path)
  def update(a: A): Unit = handler.write(path, a)
  def reset(): Unit = handler.reset(path)
}

object Pref {
  def apply[A](path: String)(implicit handler: PrefHandler[A], pref: Preferences): Pref[Option[A]] = {
    new Pref(path)(new AccessHandler.Optional, pref)
  }
  def apply[A](path: String, default: A)(implicit handler: PrefHandler[A], pref: Preferences): Pref[A] = {
    require(default != null, "Default values of null are not permitted")
    new Pref(path)(new AccessHandler.Defaulted(default), pref)
  }

  sealed trait AccessHandler[A] extends Any {
    protected[this] def handler: PrefHandler[_]
    def read(path: String)(implicit prefs: Preferences): A
    def write(path: String, a: A)(implicit prefs: Preferences): Unit
    def reset(path: String)(implicit prefs: Preferences): Unit = handler.remove(path)
  }
  object AccessHandler {

    final class Optional[A](protected[this] implicit val handler: PrefHandler[A])
      extends AccessHandler[Option[A]] {

      def read(path: String)(implicit prefs: Preferences): Option[A] = {
        handler.read(path)
      }
      def write(path: String, a: Option[A])(implicit prefs: Preferences): Unit = a match {
        case Some(value) => handler.write(path, value)
        case None        => handler.remove(path)
      }
    }

    final class Defaulted[A](default: A)(protected[this] implicit val handler: PrefHandler[A])
      extends AccessHandler[A] {

      def read(path: String)(implicit prefs: Preferences): A = {
        handler.read(path).getOrElse(default)
      }
      def write(path: String, a: A)(implicit prefs: Preferences): Unit = {
        handler.write(path, a)
      }
    }
  }
}
