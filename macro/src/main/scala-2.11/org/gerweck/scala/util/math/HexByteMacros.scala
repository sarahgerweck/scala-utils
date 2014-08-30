package org.gerweck.scala.util.math

import scala.reflect.macros.blackbox.Context

/** Macros for working with hexadecimal byte literals.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
private object HexByteMacros {
  /** Macro for hexadecimal byte literals. */
  def hexByte(c: Context)(): c.Expr[Byte] = {
    import c.universe._

    val Apply(_, List(Apply(_, List(Literal(Constant(s: String)))))) = c.prefix.tree
    val byte = Integer.parseInt(s, 16)
    val byteExpr: c.Expr[Int] = c.Expr[Int](Literal(Constant(byte)))
    reify { byteExpr.splice.toByte }
  }
}
