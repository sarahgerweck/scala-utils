package org.gerweck.scala.util.hashing

import scala.collection.immutable
import scala.concurrent._

import akka.stream.scaladsl._
import akka.util.ByteString

import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class StreamHashTest extends FlatSpec with Matchers with ScalaCheckPropertyChecks with GivenWhenThen {
  import org.gerweck.scala.util.stream.TestGlobals._

  behavior of "StreamHash"
  it should "produce the same values as a traditional hasher" in {
    val algos = {
      import BouncyHashAlgorithm._
      Vector(md5, sha1, sha_256, sha3_224, sha3_256, sha3_384, sha3_512)
    }
    for (algo <- algos) {
      When(s"using ${algo.name}")
      forAll { sba: immutable.Seq[Array[Byte]] =>
        val bsSource = {
          Source(sba)
            .map(ByteString(_))
        }
        val combinedBytes = sba.flatten
        val bouncyResult = Await.result(StreamHash.hashData(algo, bsSource), timeout)
        algo.hash(combinedBytes) shouldEqual bouncyResult
      }
    }
  }
}
