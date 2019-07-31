package org.gerweck.scala.util

import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class PairArraySpec extends FlatSpec with Matchers with ScalaCheckPropertyChecks {
  behavior of "Pair Arrays"

  it should "tabulate correctly" in {
    val tar = PairArray.tabulate(5)({ i: Int => ( i * 2, i * 3) })
    tar.inner should have length (10)
    tar.length should equal (5)
    tar.inner.toVector should equal (Vector(0, 0, 2, 3, 4, 6, 6, 9, 8, 12))
  }

  val tar = PairArray.tabulate(5)({ i: Int => ( i * 2, i * 3) })

  it should "map firsts" in {
    (tar mapFirst { _ + 1 }).inner.toVector should equal (Vector(1, 0, 3, 3, 5, 6, 7, 9, 9, 12))
  }

  it should "map seconds" in {
    (tar mapSecond { _ + 1 }).inner.toVector should equal (Vector(0, 1, 2, 4, 4, 7, 6, 10, 8, 13))
  }

  it should "map all" in {
    (tar mapAll { _ + 1 }).inner.toVector should equal (Vector(1, 1, 3, 4, 5, 7, 7, 10, 9, 13))
  }

  it should "convert to pairs" in {
    tar.toPairs.toVector should equal (Vector((0, 0), (2, 3), (4, 6), (6, 9), (8, 12)))
  }
}
