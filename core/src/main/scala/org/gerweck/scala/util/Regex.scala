package org.gerweck.scala.util

import scala.util.matching.{Regex => ScalaRegex}

/** Utilities to make it more convenient to work with regular expressions.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
@deprecated("Use Scala's `.r` directly: it is now superior to this", "2.5")
object Regex {
  implicit final class RichRegexString(val inner: String) extends AnyVal {
    def rx = new FirstMatchExtractor(inner.r)
  }

  implicit final class RichRegexRegex(val inner: ScalaRegex) extends AnyVal {
    def rx = new FirstMatchExtractor(inner)
  }

  /** Enhanced regex operations that aren't supported by regular Scala `Regex` objects. */
  final class FirstMatchExtractor(val inner: ScalaRegex) extends AnyVal {
    /** Extract a sequence of values from the first match found.
      *
      * This is useful for allowing you to do a sequence of match & extract operations on a string.
      * For example, see the example below of parsing a date string and pulling out its pieces.
      *
      * {{{
      * "2012-Q1" match {
      *   case "(\\d{4})-(\\d{2})-(\\d{2})".rx(year, month, day) => ...
      *   case "(\\d{4})-Q(\\d)".rx(year, quarter) => ...
      * }
      * }}}
      *
      * There is no convenient way to do this with the libraries that are built into Scala.
      *
      * Note that there is no checking done to ensure that there is only one match.
      */
    def unapplySeq(target: String): Option[Seq[String]] =
      for {
        m <- inner findFirstIn target
        s <- inner unapplySeq m
      } yield s
  }
}
