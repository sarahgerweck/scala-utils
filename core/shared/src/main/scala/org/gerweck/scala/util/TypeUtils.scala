package org.gerweck.scala.util

import language.experimental.macros

/** Utilities for working with types
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
object TypeUtils {
  import TypeUtilMacros._

  /** Get all the known objects that derive from a given sealed class. It '''must''' be called
    * ''after'' the case objects are defined, or else they will not be visible to the macro.
    *
    * This will actually return ''all'' descendant objects that descend from the sealed class,
    * whether or not they were marked `case object` or not.
    *
`   * '''Warning''': This will not always work on inner classes. If you have a sealed class
    * hierarchy inside a trait, there will be no known objects if this is used inside the trait
    * itself. It ''should'' work in classes that descend from that trait. My recommendation is to
    * either stick to top-level classes or verify (manually or by testing) that you get all the
    * objects you expect.
    * (This is due to [[https://issues.scala-lang.org/browse/SI-7046 SI-7406]].)
    *
    * @tparam A a sealed class or interface
    * @return all already-defined objects that derive from the given type
    */
  def getCaseObjects[A]: Set[A] = macro getCaseObjects_impl[A]

}
