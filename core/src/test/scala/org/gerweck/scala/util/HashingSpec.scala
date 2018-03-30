package org.gerweck.scala.util

import java.nio.ByteBuffer

import org.scalacheck.Arbitrary
import org.scalatest._
import org.scalatest.prop.PropertyChecks

import org.gerweck.scala.util.hashing._

/** Test specs for the hashing package
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
class HashingSpec extends FlatSpec with Matchers with PropertyChecks with GivenWhenThen {
  behavior of "MD5 algorithm"
  standardAlgo(_.md5)
  commonTests("JDK" -> JdkHashAlgorithm.md5, "Bouncy" -> BouncyHashAlgorithm.md5)
  fixedValues(BouncyHashAlgorithm.md5)(
    ""      -> "d41d8cd98f00b204e9800998ecf8427e",
    "hello" -> "5d41402abc4b2a76b9719d911017c592",
    "Sarah" -> "28e5481a80aa2bd18c8cf35d0495980a")

  behavior of "SHA-1 algorithm"
  standardAlgo(_.sha1)
  commonTests("JDK" -> JdkHashAlgorithm.sha1, "Bouncy" -> BouncyHashAlgorithm.sha1)
  fixedValues(BouncyHashAlgorithm.sha1)(
    ""      -> "da39a3ee5e6b4b0d3255bfef95601890afd80709",
    "hello" -> "aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d",
    "Sarah" -> "d24e087f504af9366c8376d8a7143821dfd38427")

  behavior of "SHA-256 algorithm"
  standardAlgo(_.sha_256)
  commonTests("JDK" -> JdkHashAlgorithm.sha_256, "Bouncy" -> BouncyHashAlgorithm.sha_256)
  fixedValues(BouncyHashAlgorithm.sha_256)(
    ""      -> "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
    "hello" -> "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
    "Sarah" -> "7e8c729e4e4ecc320cb411c4d1419bf5fbad733212d4e9491b7630aaef0b8b1c")

  behavior of "SHA3-224 algorithm"
  commonTests("Bouncy" -> BouncyHashAlgorithm.sha3_224)
  fixedValues(BouncyHashAlgorithm.sha3_224)(
    ""      -> "6b4e03423667dbb73b6e15454f0eb1abd4597f9a1b078e3f5b5a6bc7",
    "hello" -> "b87f88c72702fff1748e58b87e9141a42c0dbedc29a78cb0d4a5cd81",
    "Sarah" -> "3f17e3f4bb53e8b67e4743d8bb14b4eeb24da928aa3cb0c950f95e8e")

  behavior of "SHA3-256 algorithm"
  commonTests("Bouncy" -> BouncyHashAlgorithm.sha3_256)
  fixedValues(BouncyHashAlgorithm.sha3_256)(
    ""      -> "a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a",
    "hello" -> "3338be694f50c5f338814986cdf0686453a888b84f424d792af4b9202398f392",
    "Sarah" -> "2ddc6bb51b2080fb61b717c52a117ff4bc9339331990271f5de50176ae78ee2a")

  behavior of "SHA3-384 algorithm"
  commonTests("Bouncy" -> BouncyHashAlgorithm.sha3_384)
  fixedValues(BouncyHashAlgorithm.sha3_384)(
    ""      -> "0c63a75b845e4f7d01107d852e4c2485c51a50aaaa94fc61995e71bbee983a2ac3713831264adb47fb6bd1e058d5f004",
    "hello" -> "720aea11019ef06440fbf05d87aa24680a2153df3907b23631e7177ce620fa1330ff07c0fddee54699a4c3ee0ee9d887",
    "Sarah" -> "97164ced8798f770f189f0a1ed9c29b424d774c675be5e23fd1d7d5234781c12ca0a7c2f56cd2117e55e0063aa2297f0")

  behavior of "SHA3-512 algorithm"
  commonTests("Bouncy" -> BouncyHashAlgorithm.sha3_512)
  fixedValues(BouncyHashAlgorithm.sha3_512)(
    ""      -> "a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a615b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26",
    "hello" -> "75d527c368f2efe848ecf6b073a36767800805e9eef2b1857d5f984f036eb6df891d75f72d9b154518c1cd58835286d1da9a38deba3de98b5a53e5ed78a84976",
    "Sarah" -> "a9152df0293158789d28dc3c29341a9ee77e3d2f5c0531149b4d91931244980ded153c1f62bc97fdf367af48d0d765fcd6cf19797091b11844055e521813f8a6")

  behavior of "SHAKE-128 algorithm"
  commonTests("Bouncy" -> BouncyHashAlgorithm.shake_128)
  fixedValues(BouncyHashAlgorithm.shake_128)(
    ""      -> "7f9c2ba4e88f827d616045507605853ed73b8093f6efbc88eb1a6eacfa66ef26",
    "hello" -> "8eb4b6a932f280335ee1a279f8c208a349e7bc65daf831d3021c213825292463",
    "Sarah" -> "ec592cfbe7b5df99c1f34d326549a75d41f920a10f39c2cf026505175d1d0f3a"
  )


  behavior of "SHAKE-256 algorithm"
  commonTests("Bouncy" -> BouncyHashAlgorithm.shake_256)
  fixedValues(BouncyHashAlgorithm.shake_256)(
    ""      -> "46b9dd2b0ba88d13233b3feb743eeb243fcd52ea62b81b82b50c27646ed5762fd75dc4ddd8c0f200cb05019d67b592f6fc821c49479ab48640292eacb3b7c4be",
    "hello" -> "1234075ae4a1e77316cf2d8000974581a343b9ebbca7e3d1db83394c30f221626f594e4f0de63902349a5ea5781213215813919f92a4d86d127466e3d07e8be3",
    "Sarah" -> "dcbef227609044ebae350bcfb946b6901da8365432d9abf9808b4f14a26b4fb3d37c7503162c1d57af4292d40a762c159538a503ac7f63ed271350f55a4c01b6"
  )

  protected[this] def fixedValues(algo: HashAlgorithm)(values: (String, String)*) = {
    it should "produce the expected value" in {
      for ((input, target) <- values) {
        Given(input)
        algo.hash(input).map(b => f"$b%02x").mkString shouldEqual target
      }
    }
  }

  protected[this] def commonTests(algos: (String, HashAlgorithm)*) = {
    def forAlgos(f: HashAlgorithm => Unit): Unit = {
      for ((name, algo) <- algos) {
        When(s"using $name provider")
        f(algo)
      }
    }

    it should "produce the same results for a byte array and a byte sequence" in {
      forAlgos { algo =>
        forAll { ba: Array[Byte] =>
          algo.hash(ba) shouldEqual algo.hash(ba.toVector)
        }
      }
    }

    it should "produce the same results for a byte array and a byte buffer" in {
      forAlgos { algo =>
        forAll { ba: Array[Byte] =>
          algo.hash(ba) shouldEqual algo.hash(ByteBuffer.wrap(ba))
        }
      }
    }

    it should "produce the same results for a byte sequence and a byte buffer" in {
      forAlgos { algo =>
        forAll { bs: Seq[Byte] =>
          val bb = ByteBuffer.allocate(bs.size)
          for (b <- bs) { bb.put(b) }
          bb.flip
          algo.hash(bs) shouldEqual algo.hash(bb)
        }
      }
    }

    it should "correctly handle positions in a byte buffer" in {
      forAlgos { algo =>
        forAll { bs: Seq[Byte] =>
          val allocSize = 2 * bs.size + 2
          val bb = ByteBuffer.allocate(allocSize)
          for (i <- 0 until allocSize) { bb.put(scala.util.Random.nextInt().toByte) }
          bb.position(bs.size / 2 + 1)
          bb.mark()
          for (b <- bs) { bb.put(b) }
          bb.limit(bb.position())
          bb.reset()
          algo.hash(bs) shouldEqual algo.hash(bb)
        }
      }
    }

    for ((name, algo) <- algos) {
      algo match {
        case sized: SizedHashAlgorithm =>
          it should s"produce the correct size output in $name" in {
            forAll { ba: Array[Byte] =>
              algo.hash(ba).size shouldEqual sized.outBytes
            }
          }
        case _ => ()
      }
    }
  }
  protected[this] def standardAlgo(selector: StandardHashAlgorithms => HashAlgorithm) {
    val bouncyAlgorithm = selector(BouncyHashAlgorithm)
    val jdkAlgorithm = selector(JdkHashAlgorithm)

    it should "have the same results for both BouncyCastle and JDK" in {
      Given("strings")
      forAll { s: String =>
        bouncyAlgorithm.hash(s) shouldEqual jdkAlgorithm.hash(s)
      }
      Given("byte arrays")
      forAll { ba: Array[Byte] =>
        bouncyAlgorithm.hash(ba) shouldEqual jdkAlgorithm.hash(ba)
      }
      Given("byte sequences")
      forAll { bs: Seq[Byte] =>
        bouncyAlgorithm.hash(bs) shouldEqual jdkAlgorithm.hash(bs)
      }
      Given("byte buffers")
      forAll { bb: ByteBuffer =>
        bouncyAlgorithm.hash(bb) shouldEqual jdkAlgorithm.hash(bb)
      }
    }
  }

  protected[this] implicit lazy val arbitraryByteBuffer: Arbitrary[ByteBuffer] = Arbitrary {
    Arbitrary.arbitrary[Array[Byte]].map(ByteBuffer.wrap)
  }
}
