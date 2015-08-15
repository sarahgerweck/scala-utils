package org.gerweck.scala.util

import org.scalatest._
import org.scalatest.prop.PropertyChecks

/** Tests for [[org.gerweck.sala.util.BeanWrapper]].
  *
  * @author Sarah Gerweck <sarah@atscale.com>
  */
class BeanWrapperSpec extends FlatSpec with Matchers with PropertyChecks {
  behavior of "BeanWrapper"

  import BeanWrapperSpec._

  it should "set values correctly" in {
    val b1 = new Bean1
    val w = new BeanWrapper(b1)

    w("x") = 1
    w("x") should equal (1)

    w("x") = 17
    w("x") should equal (17)

    w("y") = "a"
    w("y") should equal ("a")
    w("y") = "whatever"
    w("y") should equal ("whatever")
  }
}

object BeanWrapperSpec {
  class Bean1 {
    private[this] var _x: Int = _
    private[this] var _y: String = _

    def getX() = _x
    def getY() = _y
    def setX(x: Int) = { _x = x }
    def setY(y: String) = { _y = y }
  }
}
