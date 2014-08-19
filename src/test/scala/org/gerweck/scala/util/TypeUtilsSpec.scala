package org.gerweck.scala.util

import org.scalatest._

object TUS {
  sealed trait T1P
  case object T1C1 extends T1P
  case object T1C2 extends T1P
  case object T1C3 extends T1P
}

/** Testing specification for [[org.gerweck.scala.util.TypeUtils]].
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
class TypeUtilsSpec extends FlatSpec with Matchers {
  import TUS._
  import TypeUtilsSpec._

  behavior of "TypeUtils"

  it should "identify immediate children of object-contained traits" in {
    // This doesn't seem to work well unless the `t1p0` lives in an `object`.
    // I think it's because this class has some order-of-init issues.
    t1po shouldEqual Set(T1C1, T1C2, T1C3)
  }
}

object TypeUtilsSpec {
  import TUS._

  val t1po = TypeUtils.getCaseObjects[T1P]
}

