package org.gerweck.scala.util

import org.scalacheck._
import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

/** Tests for the [[VersionNumber]] class.
  *
  * @author Sarah Gerweck <sarah@atscale.com>
  */
class VersionNumberSpec extends FlatSpec with Matchers with ScalaCheckPropertyChecks {
  behavior of "Version Numbers"
  private[this] final val maxVersionLength = 30

  val nonNegInt: Gen[Int] = Gen.choose[Int](0, Int.MaxValue)
  val versionSeqGen: Gen[Seq[Int]] = Gen.nonEmptyListOf(nonNegInt)
  val versionGen: Gen[VersionNumber] = versionSeqGen.map(s => VersionNumber(s.mkString(".")))

  it should "handle equality correctly" in {
    VersionNumber("1") should equal (VersionNumber("1.0"))
    VersionNumber("1") should equal (VersionNumber("1.0.0"))
    VersionNumber("1") should not equal (null)
    forAll(versionGen) { vers =>
      vers should equal (vers)
      vers should not equal null
    }
  }

  it should "handle specific inequalities correctly" in {
    VersionNumber("1") should be > VersionNumber("0.9")
    VersionNumber("1") should be < VersionNumber("1.1")
    VersionNumber("1.1") should be > VersionNumber("1.0.0.0.9999")
  }

  it should "have <= and >= consistent with equals" in {
    forAll(versionGen) { v =>
      v should be <= v
      v should be >= v
    }

    val versionLength = Gen.choose(0, maxVersionLength)
    forAll(versionLength, versionLength, nonNegInt) { (l1, l2, vBase) =>
      def makeExtended(n: Int) = {
        val parts = vBase +: Seq.fill(n)(0)
        VersionNumber(parts.mkString("."))
      }
      val v1 = makeExtended(l1)
      val v2 = makeExtended(l2)
      v1 should equal (v2)
      v1 should be <= v2
      v1 should be >= v2
      v2 should be <= v1
      v2 should be >= v1
      v1 shouldNot be < v2
      v1 shouldNot be > v2
      v2 shouldNot be < v1
      v2 shouldNot be > v1
    }
  }

  it should "have antisymmetric inequalities" in {
    forAll(versionGen, versionGen) { (v1, v2) =>
      whenever (v1 > v2) {
        v2 should be < v1
      }
    }
  }

  it should "have ≠ consistent with compare" in {
    forAll(versionGen, versionGen) { (v1, v2) =>
      whenever (v1 != v2) {
        v1.compare(v2) should not equal 0
        v2.compare(v1) should not equal 0
      }
    }
  }
}
