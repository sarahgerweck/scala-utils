package org.gerweck.scala.util

import scala.reflect.ClassTag

class SquareArray[@specialized T] private[util] (val inner: Array[T]) {
  lazy val side = math.isqrt(inner.length)
  def apply(i: Int, j: Int) = inner(i * side + j)
}

object SquareArray {
  def tabulate[@specialized A : ClassTag](side: Int)(f: (Int, Int) => A): SquareArray[A] = {
    val arr = new Array[A](side * side)
    var i = 0
    while (i < side) {
      val off = i * side
      var j = 0
      while (j < side) {
        arr(off + j) = f(i, j)
        j += 1
      }
      i += 1
    }
    new SquareArray(arr)
  }
}
