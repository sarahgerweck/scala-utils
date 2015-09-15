package org.gerweck.scala.util.date

/** An import package that always includes the 310 backport implicits.
  *
  * This should be used if you're using the Java 8 build of the utilities and
  * you're still using the JSR-310 backport libraries.
  */
package object bp310 extends ThreeTenBPImplicits
