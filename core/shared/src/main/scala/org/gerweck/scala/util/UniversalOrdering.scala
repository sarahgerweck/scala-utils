package org.gerweck.scala.util

trait UniversalOrdering[A <: UniversalOrdering.SmartComparable[A]] extends Any with Ordered[A] {
  def inner: A
  @inline override def compare(that: A) = inner compareTo that

  @inline def max (that: A): A = if (this < that) that else inner
  @inline def min (that: A): A = if (this < that) inner else that
}

object UniversalOrdering {
  type SmartComparable[A] = Comparable[B] forSome { type B >: A }
}
