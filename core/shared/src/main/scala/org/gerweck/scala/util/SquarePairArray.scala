package org.gerweck.scala.util

import scala.reflect.ClassTag

class SquarePairArray[@specialized A] private[util] (val inner: PairArray[A]) {
  lazy val side = math.isqrt(inner.length)
  def first(i: Int, j: Int): A = inner.first(i * side + j)
  def second(i: Int, j: Int): A = inner.second(i * side + j)
  def apply(i: Int, j: Int): (A, A) = inner.apply(i * side + j)
}

object SquarePairArray {
  def tabulate[@specialized A : ClassTag](side: Int)(f: (Int, Int) => (A, A)): SquarePairArray[A] = {
    val arr = new Array[A](side * side * 2)
    var i = 0
    while (i < side) {
      val off = i * side
      var j = 0
      while (j < side) {
        val (fst, snd) = f(i, j)
        val base = 2 * (off + j)
        arr(base) = fst
        arr(base + 1) = snd
        j += 1
      }
      i += 1
    }
    new SquarePairArray(new PairArray(arr))
  }

  def tabulate2[@specialized A : ClassTag](side: Int)(f1: (Int, Int) => A, f2: (Int, Int) => A): SquarePairArray[A] = {
    val arr = new Array[A](side * side * 2)
    var i = 0
    while (i < side) {
      val off = i * side
      var j = 0
      while (j < side) {
        val base = 2 * (off + j)
        arr(base) = f1(i, j)
        arr(base + 1) = f2(i, j)
        j += 1
      }
      i += 1
    }
    new SquarePairArray(new PairArray(arr))
  }
}
