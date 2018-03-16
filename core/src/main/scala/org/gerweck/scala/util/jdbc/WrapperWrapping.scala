package org.gerweck.scala.util.jdbc

import java.sql.Wrapper

/** A parent trait that should be used for all the JDBC wrappers.
  *
  * JDBC is designed to accommodate wrappers, and has a few special methods
  * that can be used to penetrate wrappers to expose hidden interfaces. This
  * trait provides standard implementations of those, plus implementations of
  * `hashCode` and `equals`.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
trait WrapperWrapping[+A <: Wrapper] extends Wrapper {
  val inner: A

  override def isWrapperFor(clazz: Class[_]): Boolean = {
    (clazz isInstance inner) || (inner isWrapperFor clazz)
  }

  override def unwrap[T](clazz: Class[T]): T = {
    if (clazz isInstance inner) {
      inner.asInstanceOf[T]
    } else {
      inner.unwrap(clazz)
    }
  }

  override def hashCode: Int = {
    /* Randomly flip some of the bits to avoid collisions. */
    inner.hashCode ^ 0xb92442c2
  }

  override def equals(that: Any): Boolean = that match {
    case wc: WrapperWrapping[_] => inner equals wc.inner
    case _                      => inner equals that
  }
}
