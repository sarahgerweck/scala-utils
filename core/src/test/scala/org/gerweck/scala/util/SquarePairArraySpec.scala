package org.gerweck.scala.util

import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class SquarePairArraySpec extends FlatSpec with Matchers with ScalaCheckPropertyChecks {
  behavior of "Square Pair Arrays"

  val expected = Vector(0, 0, 1, 10, 2, 20, 10, 100, 11, 110, 12, 120, 20, 200, 21, 210, 22, 220)

  it should "tabulate singularly correctly" in {
    val spa =
      SquarePairArray.tabulate(3) { (i: Int, j: Int) =>
        (10 * i + j, 100 * i + 10 * j)
      }
    spa.inner.inner.toVector should equal (expected)
  }

  it should "tabulate doubly correctly" in {
    val spa =
      SquarePairArray.tabulate2(3)(
        { (i: Int, j: Int) => 10 * i + j },
        { (i: Int, j: Int) => 100 * i + 10 * j }
      )
    spa.inner.inner.toVector should equal (expected)
  }

  val spa =
    SquarePairArray.tabulate(3) { (i: Int, j: Int) =>
      (10 * i + j, 100 * i + 10 * j)
    }

  it should "read first values correctly" in {
    spa.first(0, 1) should equal (1)
    spa.first(1, 0) should equal (10)
    spa.first(2, 2) should equal (22)
  }

  it should "read second values correctly" in {
    spa.second(0, 1) should equal (10)
    spa.second(1, 0) should equal (100)
    spa.second(2, 2) should equal (220)
  }

  it should "read pairs correctly" in {
    spa(0, 1) should equal ((1, 10))
    spa(1, 0) should equal ((10, 100))
    spa(2, 2) should equal ((22, 220))
  }
}
