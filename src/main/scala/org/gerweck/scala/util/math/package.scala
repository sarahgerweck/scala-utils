package org.gerweck.scala.util

import language.experimental.macros

import java.math.{ BigDecimal, BigInteger }

/** Global imports for math utility.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  * @since 0.9.9
  */
package object math {
  implicit final class HexByte(val sc: StringContext) extends AnyVal {
    def b16(): Byte = macro HexByteMacros.hexByte
  }

  def isqrt(i: Int): Int = {
    if (i < 0) throw new IllegalArgumentException("No square roots for negative numbers")

    var one = 1 << 30
    var op = i
    var res = 0
    while (one > op) {
      one >>>= 2
    }
    while (one != 0) {
      assert (op >= 0)
      /* If res + one is negative, we had a signed overflow and it's actually greater than op.
       * Every operation we do other than the comparison is effectively unsigned.
       */
      if (op >= res + one || res + one < 0) {
        op -= res + one
        res += 2 * one
      }
      res >>>= 1
      one >>>= 2
    }
    res
  }

  def isqrt(l: Long): Long = {
    if (l < 0) throw new IllegalArgumentException("No square roots for negative numbers")

    var one = 1L << 62
    var op = l
    var res = 0L
    while (one > op) {
      one >>>= 2
    }
    while (one != 0) {
      assert (op >= 0)
      /* If res + one is negative, we had a signed overflow and it's actually greater than op.
       * Every operation we do other than the comparison is effectively unsigned.
       */
      if (op >= res + one || res + one < 0) {
        op -= res + one
        res += 2 * one
      }
      res >>>= 1
      one >>>= 2
    }
    res
  }

  implicit final class RichBigDecimal(val inner: BigDecimal) extends AnyVal {
    @inline def eq(that: BigDecimal): Boolean = inner.compareTo(that) == 0
    @inline def ne(that: BigDecimal): Boolean = inner.compareTo(that) != 0
    @inline def eq(that: BigInteger): Boolean = eq(new BigDecimal(that))
    @inline def ne(that: BigInteger): Boolean = ne(new BigDecimal(that))
  }

  implicit class RichBigInteger(val inner: BigInteger) extends AnyVal {
    @inline def eq(that: BigDecimal): Boolean = new BigDecimal(inner).compareTo(that) == 0
    @inline def ne(that: BigDecimal): Boolean = new BigDecimal(inner).compareTo(that) != 0
    @inline def eq(that: BigInteger): Boolean = inner == that
    @inline def ne(that: BigInteger): Boolean = inner != that
  }
}
