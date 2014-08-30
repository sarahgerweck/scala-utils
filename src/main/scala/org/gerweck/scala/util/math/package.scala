package org.gerweck.scala.util

import language.experimental.macros

/** Global imports for math utility.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  * @since 0.9.9
  */
package object math {
  implicit final class HexByte(val sc: StringContext) extends AnyVal {
    def b16(): Byte = macro HexByteMacros.hexByte
  }
}