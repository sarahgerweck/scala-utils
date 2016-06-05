package org.gerweck.scala.util

import scala.reflect.ClassTag

/** An array of pairs. */
class PairArray[@specialized A: ClassTag] private[util] (val inner: Array[A]) {
  /* Safe to do an assert because constructors aren't public */
  assert(inner.length % 2 == 0)

  lazy val length = inner.size / 2

  @inline def first(i: Int): A = inner(i * 2)
  @inline def second(i: Int): A = inner(i * 2 + 1)
  def apply(i: Int): (A, A) = (first(i), second(i))

  def map[@specialized B: ClassTag](f: (A, A) => B): Array[B] = {
    Array.tabulate (length) { n: Int => f(first(n), second(n)) }
  }

  def mapFirst(f: A => A): PairArray[A] = {
    val arr = new Array[A](inner.length)
    for (i <- 0 until length) {
      arr(2 * i) = f(inner(2 * i))
      arr(2 * i + 1) = inner(2 * i + 1)
    }
    new PairArray[A](arr)
  }

  def mapSecond(f: A => A): PairArray[A] = {
    val arr = new Array[A](inner.length)
    for (i <- 0 until length) {
      arr(2 * i) = inner(2 * i)
      arr(2 * i + 1) = f(inner(2 * i + 1))
    }
    new PairArray[A](arr)
  }

  def mapAll(f: A => A): PairArray[A] = {
    val arr = new Array[A](inner.length)
    for (i <- 0 until length) {
      arr(2 * i) = f(inner(2 * i))
      arr(2 * i + 1) = f(inner(2 * i + 1))
    }
    new PairArray[A](arr)
  }

  def toPairs: Array[(A, A)] = {
    val arr = new Array[(A, A)](length)
    for (i <- 0 until length) {
      arr(i) = (inner(2 * i), inner(2 * i + 1))
    }
    arr
  }
}

object PairArray {
  def tabulate[@specialized A: ClassTag](n: Int)(f: Int => (A, A)): PairArray[A] = {
    val arr = new Array[A](n * 2)
    for (i <- 0 until n) {
      val (fst, snd) = f(i)
      arr(i * 2) = fst
      arr(i * 2 + 1) = snd
    }
    new PairArray[A](arr)
  }
}
