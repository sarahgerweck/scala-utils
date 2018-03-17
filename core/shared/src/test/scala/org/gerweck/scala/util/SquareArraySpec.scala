package org.gerweck.scala.util

import org.scalatest._
import org.scalatest.prop.PropertyChecks

class SquareArraySpec extends FlatSpec with Matchers with PropertyChecks {
  behavior of "Square Arrays"

  it should "tabulate correctly" in {
    val sa = SquareArray.tabulate(3){ (i: Int, j: Int) => 10 * i + j }
    sa.side should equal (3)
    sa.inner.toVector should equal (Vector(0, 1, 2, 10, 11, 12, 20, 21, 22))
  }

  it should "read values correctly" in {
    val sa = SquareArray.tabulate(3){ (i: Int, j: Int) => 10 * i + j }
    sa(0, 0) should equal (0)
    sa(0, 1) should equal (1)
    sa(1, 0) should equal (10)
    sa(2, 2) should equal (22)
  }
}
