package org.gerweck.scala.util

/** A class for managing standard version numbers.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
final class VersionNumber(val versionParts: Seq[Int]) extends Ordered[VersionNumber] {
  override def compare(that: VersionNumber) = {
    // TBD: This could be a scalaz lazy foldRight
    // We use zipAll with zero extension because 1.2.0 is the same as 1.2.
    (versionParts.zipAll(that.versionParts, 0, 0) :\ 0) {
      case ((a, b), rest) =>
        val current = a compareTo b
        if (current != 0) current else rest
    }
  }

  override def toString: String = versionParts mkString "."

  override def hashCode: Int = {
    // To make this compatible with equals, we have to drop any trailing zeroes.
    // We do the explicit `toVector` to control for different types of sequences.
    // It doesn't work with an Array, because Arrays don't do a deep `hashCode`.
    versionParts.reverse.dropWhile(_ == 0).toVector.hashCode
  }

  override def equals(that: Any): Boolean = {
    that match {
      case _ if this == that => true
      case null              => false
      case vn: VersionNumber => (this compareTo vn) == 0
      case _                 => false
    }
  }
}

object VersionNumber {
  def apply(s: String): VersionNumber = apply(s split "\\." map { _.toInt })
  def apply(parts: Seq[Int]): VersionNumber = new VersionNumber(parts)
}
