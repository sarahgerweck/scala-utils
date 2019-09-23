package org.gerweck.scala.util

import scala.reflect.ClassTag
import scala.util.Try

import java.sql.Wrapper

/** Utilities related to JDBC processing.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
package object jdbc {
  /** Extension methods for a JDBC `Wrapper` object. */
  implicit final class RichWrapper(val inner: Wrapper) extends AnyVal {
     /** An extension method to make it more Scala-style to use a [[java.sql.Wrapper]].
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

    /** A version of [[RichWrapper.unwrapOption]] that is wrapped with a `Try` and
      * falls back to a subclass test if the underlying `Wrapper` doesn't suppoort
      * `isWrapperFor` and `unwrap`.
      */
    def safeUnwrapOption[A](implicit ev: ClassTag[A]): Option[A] = {
      Try(unwrapOption[A])
        .recover { case e: Exception =>
          if (ev.runtimeClass.isInstance(inner)) {
            Some(inner.asInstanceOf[A])
          } else {
            None
          }
        }
        .get
    }
  }
}
