package org.gerweck.scala.util

import org.scalatest._
import org.scalatest.prop.PropertyChecks


/** Test specs for the gerweckHash function.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
class GerweckHashSpec extends FlatSpec with Matchers with PropertyChecks {
  behavior of "1-argument RotateHash"

  it should "have a stable value" in {
    forAll { s: String =>
      rotateHash(s) shouldEqual rotateHash(s)
    }
  }

  behavior of "2-argument RotateHash"

  it should "have a stable value" in {
    forAll { (s1: String, s2: String) =>
      rotateHash(s1, s2) shouldEqual rotateHash(s1, s2)
    }
  }

  it should "be antisymmetrical" in {
    forAll { (s1: String, s2: String) =>
      whenever (s1.hashCode != s2.hashCode) {
        rotateHash(s1, s2) should not equal rotateHash(s2, s1)
      }
    }
  }

  it should "be consistent with the vararg version" in {
    forAll { (s1: String, s2: String) =>
      rotateHash(s1, s2) shouldEqual rotateHash(s1, s2, Seq.empty: _*)
    }
  }

  behavior of "n-argument RotateHash"

  it should "have a stable value" in {
    forAll { (s1: String, s2: String, ss: Seq[String]) =>
      rotateHash(s1, s2, ss: _*) shouldEqual rotateHash(s1, s2, ss: _*)
    }
  }
}
