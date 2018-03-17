package org.gerweck.scala.util.jdbc

import java.sql.Wrapper

import org.gerweck.scala.util.Support.{support => nse}

/** A JDBC
  * `[[http://docs.oracle.com/javase/7/docs/api/java/sql/Wrapper.html java.sql.Wrapper]]`.
  * that throws an exception for all methods.
  *
  * This is useful as a base class for a very limited implementation. You probably don't want to
  * use this for a real driver, because you won't be able to easily tell what you have and haven't
  * implemented.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
trait ThrowingWrapper extends Wrapper {
  override def isWrapperFor(clazz: Class[_]): Boolean = nse
  override def unwrap[T](clazz: Class[T]): T = nse
}
