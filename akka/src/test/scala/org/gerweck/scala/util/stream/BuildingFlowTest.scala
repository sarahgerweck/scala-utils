package org.gerweck.scala.util.stream

import scala.collection.immutable
import scala.concurrent._

import akka.stream.scaladsl._

import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class BuildingFlowTest extends FlatSpec with Matchers with ScalaCheckPropertyChecks with GivenWhenThen {
  import TestGlobals._

  private[this] def runToCollection[A](s: Source[A, _]) = Await.result(s.toMat(Sink.collection)(Keep.right).run(), timeout)

  private[this] implicit class SourceOps[A](s: Source[A, _]) {
    def shouldRunTo(a: immutable.Seq[A]) = runToCollection(s) shouldEqual a
  }

  behavior of "BuildingFlow"
  it should "handle empty streams correctly" in {
    When("using Ints")
    Source.empty[Int].via(IntPassthrough) shouldRunTo Nil
    When("using Strings")
    Source.empty[String].via(StringPassthrough) shouldRunTo Nil
  }
  it should "pass through sequences correctly" in {
    When("using trivial sequence")
    Source(0::Nil).via(IntPassthrough) shouldRunTo 0::Nil
    When("using Ints")
    forAll { sa: immutable.Seq[Int] =>
      Source(sa).via(IntPassthrough) shouldRunTo sa
    }
    When("using Strings")
    forAll { sa: immutable.Seq[String] =>
      Source(sa).via(StringPassthrough) shouldRunTo sa
    }
  }
  it should "handle nontrivial builders correctly" in {
    Source(Nil).via(SummingStreamFlow) shouldRunTo Nil
    Source(("a", 1)::("a", 2)::("b", 3)::("a", 4)::("a", 5)::("a", 6)::("b", 7)::Nil)
      .via(SummingStreamFlow) shouldRunTo ("a", 3)::("b", 3)::("a", 15)::("b", 7)::Nil
  }

  private[this] object IntPassthrough extends PassthroughBuildingFlow[Int]
  private[this] object StringPassthrough extends PassthroughBuildingFlow[String]
  private[this] class PassthroughBuildingFlow[A] extends BuildingFlow[A, A] {
    override def absorb(a: A) = a
    override def combine(state: A, a: A): BuildingFlow.CombineResult[A] = NoCombine
  }
  private[this] object SummingStreamFlow extends BuildingFlow[(String, Int), (String, Int)] {
    override def absorb(p: (String, Int)) = p
    override def combine(state: (String, Int), s: (String, Int)) = {
      val (cs, ci) = state
      val (ss, si) = s
      if (cs == ss) {
        CanCombine((cs, ci + si))
      } else {
        NoCombine
      }
    }
  }
}
object BuildingFlowTest
