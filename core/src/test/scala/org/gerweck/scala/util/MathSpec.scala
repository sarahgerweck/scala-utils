package org.gerweck.scala.util.math

import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

/** Test specs for math utility functions
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
class MathSpec extends FlatSpec with Matchers with ScalaCheckPropertyChecks {
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

  behavior of "Integer square roots"

  it should "handle a few common cases" in {
    @inline def se(n: Long, r: Long) = {
      isqrt(n) should equal (r)
      isqrt(n.toInt) should equal (r)
    }
    se(0, 0)
    se(1, 1)
    se(2, 1)
    se(3, 1)
    se(4, 2)
    se(5, 2)
    se(8, 2)
    se(9, 3)
    se(16, 4)
    se(25, 5)
    se(36, 6)
    se(49, 7)
    se(64, 8)
    se(256, 16)
  }

  it should "reject negative Ints" in {
    forAll { i: Int =>
      whenever (i < 0) {
        an [IllegalArgumentException] should be thrownBy isqrt(i)
      }
    }
  }

  it should "reject negative Longs" in {
    forAll { l: Long =>
      whenever (l < 0) {
        an [IllegalArgumentException] should be thrownBy isqrt(l)
      }
    }
  }

  it should "handle any Int's square root" in {
    forAll { i: Int =>
      whenever (i >= 0) {
        isqrt(i) should equal (scala.math.sqrt(i).toInt)
      }
    }
  }

  it should "handle any Long's square root" in {
    forAll { l: Long =>
      whenever (l >= 0) {
        isqrt(l) should equal (scala.math.sqrt(l).toLong)
      }
    }
  }
}
