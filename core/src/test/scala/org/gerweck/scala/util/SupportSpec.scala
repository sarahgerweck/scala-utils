package org.gerweck.scala.util

import org.scalatest._
import org.scalatest.matchers._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

/** Tests for the Support macro utility.
  *
  * @author Sarah Gerweck <sarah@atscale.com>
  */
class SupportSpec extends FlatSpec with Matchers with ScalaCheckPropertyChecks with SupportMethodMatchers {
  behavior of "SupportSpec"

  import SupportSpec._

  private[this] lazy val c1 = new C1

  it should "throw exceptions for class methods" in {
    an [UnsupportedOperationException] should be thrownBy c1.m1
    an [UnsupportedOperationException] should be thrownBy c1.m2("a", 2)
    an [UnsupportedOperationException] should be thrownBy c1.`1/2`()
  }
  it should "throw exceptions for module methods" in {
    an [UnsupportedOperationException] should be thrownBy M1.m1
    an [UnsupportedOperationException] should be thrownBy M1.method2("b", 9)
    an [UnsupportedOperationException] should be thrownBy M1.`a+b`()
  }
  it should "correctly name class methods" in {
    the [UnsupportedOperationException] thrownBy c1.m1         should haveMethodName ("m1")
    the [UnsupportedOperationException] thrownBy c1.m2("a", 2) should haveMethodName ("m2")
    the [UnsupportedOperationException] thrownBy c1.`1/2`()    should haveMethodName ("1/2")
  }
  it should "correctly name module methods" in {
    the [UnsupportedOperationException] thrownBy M1.m1              should haveMethodName ("m1")
    the [UnsupportedOperationException] thrownBy M1.method2("a", 2) should haveMethodName ("method2")
    the [UnsupportedOperationException] thrownBy M1.`a+b`()         should haveMethodName ("a+b")
  }
}

trait SupportMethodMatchers {
  private[this] val rx = "^Method `(.+)` not supported$".r

  final class SupportMethodNameMatcher(expectedName: String) extends Matcher[UnsupportedOperationException] {
    def apply(left: UnsupportedOperationException) = {
      val msg = left.getMessage
      val regexMatch = rx.findFirstMatchIn(msg)

      MatchResult(
        regexMatch.isDefined && regexMatch.get.group(1) == expectedName,
        regexMatch match {
          case Some(m) =>
            val name = m.group(1)
            s"Thrown method name $name does not match expected name $expectedName"
          case None =>
            s"Thrown operation message $msg doesn't have correct pattern"
        },
        s"Thrown method name $expectedName matched the expected name $expectedName"
      )
    }
  }

  def haveMethodName(expectedName: String) = new SupportMethodNameMatcher(expectedName)
}

object SupportSpec {
  import org.gerweck.scala.util.Support.{ support => s}

  class C1 {
    def m1 = s
    def m2(a: String, b: Int) = s
    def `1/2`(): String = s
  }

  object M1 {
    def m1 = s
    def method2(a: String, b: Int) = s
    def `a+b`(): String = s
  }

}
