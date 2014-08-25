package org.gerweck.scala

import scala.collection.TraversableLike
import scala.reflect.runtime.{universe => ru}
import scala.reflect._

/** Miscellaneous utility code for manipulating standard objects.
  *
  * This package is designed to be maximally convenient if you `import org.gerweck.scala.util._`.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
package object util {

  /** Utility functionality for working with characters.
   *
   * @author Sarah Gerweck <sarah.a180@gmail.com>
   */
  implicit final class CharUtils(val self: Char) extends AnyVal {

    /** Convert a character to a random case.  It will have a 50% probability of
      * being uppercase and a 50% probability of being lowercase.
      */
    def toRandomCase: Char = if (scala.util.Random.nextBoolean) self.toUpper else self.toLower

    /** Case-insensitive equals.
      *
      * @note `~` is the command-mode vim keystroke to flip the case of a
      * character, and the defined operator looks similar to the mathematical
      * congruence operator (&cong;). These offer useful mnemonics.
      */
    def =~= (that: Char): Boolean = {
      /*
       * String's `equalsIgnoreCase` is safer than something like `toLower`. The
       * latter is not always safe when dealing with international characters.
       * (Passing in a specific locale is even better, but String provides good
       * generalized behavior.)
       */
      String.valueOf(self) =~= String.valueOf(that)
    }
  }

  /** Utility functionality for working with strings.
    *
    * @author Sarah Gerweck <sarah.a180@gmail.com>
    */
  implicit final class StringUtils(val self: String) extends AnyVal {

    /** Convert a string to random case.  Each character will have a 50%
      * probability of being uppercase and a 50% probability of being lowercase.
      */
    def toRandomCase: String = self map { _.toRandomCase }

    /** Case-insensitive equals.
      *
      * @note `~` is the command-mode vim keystroke to flip the case of a
      * character, and the defined operator looks similar to the mathematical
      * congruence operator (&cong;). These offer useful mnemonics.
      */
    def =~= (that: String): Boolean =
      if (self eq that) true
      else self equalsIgnoreCase that
  }

  /** Additional methods for `traversable`s.
    *
    * @author Sarah Gerweck <sarah.a180@gmail.com>
    */
  implicit final class TraversableUtil[A](val t: Traversable[A]) extends AnyVal {

    /** A same-type fold for associative operators that requires at least one
      * member present in the collection.
      */
    @inline def fold1(op: (A, A) => A): A = t.tail.fold(t.head)(op)

    /** A same-type non-empty left fold.
      *
      * This requires at least one member present in the collection.
      */
    @inline def foldl1(op: (A, A) => A): A = (t.head /: t.tail)(op)

    /** A same-type non-empty right fold.
      *
      * This requires at least one member present in the collection.
      */
    @inline def foldr1(op: (A, A) => A): A = (t.init :\ t.last)(op)
  }

  /** Enhancements to Java's `MessageFormat` API. */
  implicit final class RichMessageFormat(val mf: java.text.MessageFormat) extends AnyVal {
    final def apply(str: String) = mf.format(Array(str))
    final def apply(str: String, others: String*) = mf.format((str +: others).toArray)
  }

  implicit final class SeqUtil[A](val coll: Seq[A]) {
    import ru._
    final def filterType[B <: A : ClassTag]: Seq[B] = {
      val m = ru.runtimeMirror(getClass.getClassLoader)
      val ct = classTag[B]
      (coll filter { ct.runtimeClass isAssignableFrom _.getClass }).asInstanceOf[Seq[B]]
    }
  }
}
