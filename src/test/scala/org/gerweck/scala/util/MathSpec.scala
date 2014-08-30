package org.gerweck.scala.util.math

import org.scalatest._

/** Test specs for math utility functions
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
class MathSpec extends FlatSpec with Matchers {
  behavior of "Hexadecimal byte literals"

  it should "generate bytes" in {
    b16"00".isInstanceOf[Byte] should be (true)
    b16"ae".isInstanceOf[Byte] should be (true)
  }

  it should "handle uppercase" in {
    b16"0A" should equal (10.toByte)
  }

  it should "handle lowercase" in {
    b16"0a" should equal (10.toByte)
  }

  it should "be signed" in {
    b16"09".toInt should (be >= 0)
    b16"a0".toInt should (be < 0)
    b16"ff".toInt should (be < 0)
  }
}