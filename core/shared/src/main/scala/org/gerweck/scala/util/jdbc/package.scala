package org.gerweck.scala.util

import scala.reflect.ClassTag

import java.sql.Wrapper

/** Utilities related to JDBC processing.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
package object jdbc {
  /** Extension methods for a JDBC `Wrapper` object. */
  implicit final class RichWrapper(val inner: Wrapper) extends AnyVal {
     /** An extension method to make it more Scala-style to use a [[scala.jdbc.Wrapper]].
       *
       * @tparam A the type of the target object to attempt to unwrap
       * @return an [[scala.Option]] that will be defined if the class was a wrapper, and
       * undefined otherwise.
       */
     def unwrapOption[A](implicit ev: ClassTag[A]): Option[A] = {
       if (inner.isWrapperFor(ev.runtimeClass)) {
         ev.unapply(inner.unwrap(ev.runtimeClass))
       } else {
         None
       }
    }
  }
}
