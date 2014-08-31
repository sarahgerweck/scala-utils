package org.gerweck.scala.util

import org.scalatest._

object TUS {
  sealed trait T1P
  case object T1C1 extends T1P
  case object T1C2 extends T1P
  case object T1C3 extends T1P

  sealed trait T2P
  sealed trait T2C1 extends T2P
  case object T2G1 extends T2C1
  case object T2G2 extends T2C1
  sealed trait T2C2 extends T2P
  case object T2G3 extends T2C2
  case object T2G4 extends T2C2

}

/** Testing specification for [[org.gerweck.scala.util.TypeUtils]].
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
class TypeUtilsSpec extends WordSpec with Matchers {
  import TUS._
  import TypeUtilsSpec._

  "TypeUtils" when {
    "identifying object-contained traits" should {
      "handle immediate children" in {
        // This doesn't seem to work well unless the `t1p0` lives in an `object`.
        // I think it's because this class has some order-of-init issues.
        t1po shouldEqual Set(T1C1, T1C2, T1C3)
      }
      "handle grandchildren" in {
        t2po shouldEqual Set(T2G1, T2G2, T2G3, T2G4)
      }
      "handle children of child traits" in {
        t2c1o shouldEqual Set(T2G1, T2G2)
        t2c2o shouldEqual Set(T2G3, T2G4)
      }
    }
  }
}

object TypeUtilsSpec {
  import TUS._

  val t1po = TypeUtils.getCaseObjects[T1P]

  val t2po = TypeUtils.getCaseObjects[T2P]
  val t2c1o = TypeUtils.getCaseObjects[T2C1]
  val t2c2o = TypeUtils.getCaseObjects[T2C2]
}

