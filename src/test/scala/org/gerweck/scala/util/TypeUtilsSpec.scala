package org.gerweck.scala.util

import org.scalatest._


/* Tests doesn't seem to work well unless the `t1p0` lives in an `object`.
 *
 * I think it's because this class has some order-of-init issues. (This is probably why the
 * maintainers of Scala reflection have marked the methods I'm using as "officially broken," but
 * it's extremely useful.)
 *
 * This also needs to be above the point where we call `getCaseObjects`. */
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

  sealed trait T3P

  sealed trait T3T1 extends T3P

  sealed trait T3T2 extends T3P
  case object T3T2O1 extends T3T2

  sealed trait T3T3 extends T3T2
  case object T3T3O1 extends T3T3
  case object T3T3O2 extends T3T3

  sealed trait T3T4 extends T3T3
  case object T3T4O1 extends T3T4

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
        t1po shouldEqual Set(T1C1, T1C2, T1C3)
      }
      "handle grandchildren" in {
        t2po shouldEqual Set(T2G1, T2G2, T2G3, T2G4)
      }
      "handle children of child traits" in {
        t2c1o shouldEqual Set(T2G1, T2G2)
        t2c2o shouldEqual Set(T2G3, T2G4)
      }
      "handle empty traits" in {
        t3t1o shouldBe empty
      }
      "handle complex ancestries" in {
        t3po  shouldEqual Set(T3T2O1, T3T3O1, T3T3O2, T3T4O1)
        t3t2o shouldEqual Set(T3T2O1, T3T3O1, T3T3O2, T3T4O1)
        t3t3o shouldEqual Set(T3T3O1, T3T3O2, T3T4O1)
        t3t4o shouldEqual Set(T3T4O1)
      }
    }
  }
}

object TypeUtilsSpec {
  import TUS._
  import TypeUtils.{ getCaseObjects => gco }

  val t1po = gco[T1P]

  val t2po = gco[T2P]
  val t2c1o = gco[T2C1]
  val t2c2o = gco[T2C2]

  val t3po  = gco[T3P]
  val t3t1o = gco[T3T1]
  val t3t2o = gco[T3T2]
  val t3t3o = gco[T3T3]
  val t3t4o = gco[T3T4]
}
