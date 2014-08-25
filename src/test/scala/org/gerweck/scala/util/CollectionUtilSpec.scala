package org.gerweck.scala.util

import org.scalatest._

/**
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
class CollectionUtilSpec extends FlatSpec with Matchers {
  behavior of "SeqUtil"

  it should "filter subclasses" in {
    val mixedSeq = Seq(1, 2, 3, "a", "b", "c", 4)
    mixedSeq.filterType[String] shouldEqual Seq("a", "b", "c")
    mixedSeq.filterType[Int] shouldEqual Seq(1, 2, 3, 4)
  }

}