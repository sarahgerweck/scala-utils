package org.gerweck.scala

/** Miscellaneous utility code for manipulating standard objects.
  *
  * This package is designed to be maximally convenient if you `import org.gerweck.scala.util._`.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
package object util {
  private[this] final val HashBase = 0x53474073
  private[this] final val HashOffsetStep = 7

  @inline final def rotateHash(v1: Any): Int = {
    HashBase ^ Integer.rotateRight(v1.hashCode, HashOffsetStep)
  }

  @inline final def rotateHash(v1: Any, v2: Any): Int = {
    HashBase ^ Integer.rotateRight(v1.hashCode, HashOffsetStep) ^ Integer.rotateRight(v2.hashCode, 2 * HashOffsetStep)
  }

  /** Compute a very fast hash functional of all the inputs' hashes.
    *
    * There is no guarantee that this value will be stable between releases.
    * It's suitable for an ephemeral hashCode if all the versions of the code
    * are on the same versions of GerweckUtil.
    */
  def rotateHash(v1: Any, v2: Any, inputs: Any*): Int = {
    // This uses a bit of group theory. As long as HashOffset is coprime with 32, we won't
    // repeat our offset until we've added in 32 values, which will take a while.
    @scala.annotation.tailrec @inline def helper(inputs: Seq[Any], offset: Int, current: Int): Int = {
      if (inputs.isEmpty) {
        current
      } else {
        val soak = Integer.rotateRight(inputs.head.hashCode, offset)
        helper(inputs.tail, offset + HashOffsetStep, current ^ soak)
      }
    }
    helper(inputs, HashOffsetStep * 3, rotateHash(v1, v2))
  }

  @inline final def hashProduct(p: Product) = {
    scala.util.hashing.MurmurHash3.seqHash(p.productPrefix +: p.productIterator.toSeq)
  }

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
    @deprecated("Use the built-in `reduce` method", "2.5")
    @inline def fold1(op: (A, A) => A): A = t.tail.fold(t.head)(op)

    /** A same-type non-empty left fold.
      *
      * This requires at least one member present in the collection.
      */
    @deprecated("Use the built-in `reduceLeft` method", "2.5")
    @inline def foldl1(op: (A, A) => A): A = (t.head /: t.tail)(op)

    /** A same-type non-empty right fold.
      *
      * This requires at least one member present in the collection.
      */
    @deprecated("Use the built-in `reduceRight` method", "2.5")
    @inline def foldr1(op: (A, A) => A): A = (t.init :\ t.last)(op)
  }

  /** Enhancements to Java's `MessageFormat` API. */
  implicit final class RichMessageFormat(val mf: java.text.MessageFormat) extends AnyVal {
    final def apply(str: String) = mf.format(Array(str))
    final def apply(str: String, others: String*) = mf.format((str +: others).toArray)
  }

  /** Enhancements to Scala's `Regex` class. */
  implicit final class RichRegex(val inner: scala.util.matching.Regex) extends AnyVal {
    /** Check whether the given regex matches a provided string.
      *
      * Whether the entire string must match is governed by whether this is an anchored regex.
      *
      * @param s the string to test
     */
    def matches(s: String): Boolean = inner.unapplySeq(s).isDefined
  }
}
