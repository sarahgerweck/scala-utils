package org.gerweck.scala.util.hashing

import scala.concurrent._
import scala.util._

import akka.stream._
import akka.stream.scaladsl._
import akka.stream.stage._
import akka.util.ByteString

/** Utilities providing streaming hash functions.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
object StreamHash {
  /** Hash a stream of bytes and return a `Future` of the hash data.
    *
    * The overhead of materializing a stream graph makes this '''much''' more expensive than just
    * calling the `HashAlgorithm` directly if the data involved is small. This should be used only
    * for relatively large, infrequent activities. (The primary use case for a `StreamHash` is to
    * use [[StreamHash.hashingSink]] when you're already running a graph.)
    */
  def hashData(hashAlgorithm: HashAlgorithm, source: Source[ByteString, _])(implicit mat: Materializer): Future[Array[Byte]] = {
    source.toMat(hashingSink(hashAlgorithm))(Keep.right).run
  }

  /** A graph [[akka.stream.scaladsl.Sink]] that takes in bytes and materializes a hash. */
  def hashingSink(hashAlgorithm: HashAlgorithm): Sink[ByteString, Future[Array[Byte]]] = {
    Sink.fromGraph(new HashingSink(hashAlgorithm))
  }

  private class HashingSink(hashAlgorithm: HashAlgorithm) extends GraphStageWithMaterializedValue[SinkShape[ByteString], Future[Array[Byte]]] {
    val in: Inlet[ByteString] = Inlet("HashData.in")
    override val shape: SinkShape[ByteString] = SinkShape(in)

    override def createLogicAndMaterializedValue(inheritedAttributes: Attributes): (GraphStageLogic, Future[Array[Byte]]) = {
      val results = Promise[Array[Byte]]
      val logic =
        new GraphStageLogic(shape) {
          val digest = hashAlgorithm.initialize()

          override def preStart(): Unit = pull(in)

          setHandler(in, new InHandler {
            override def onPush(): Unit = {
              /* Grab the incoming ByteString */
              val bs = grab(in)

              Try {
                /* Add the ByteString's data into the digest */
                bs.asByteBuffers.foreach(bb => digest.update(bb))
              } match {
                case Success(_) =>
                  /* Request more data */
                  pull(in)
                case Failure(t) =>
                  /* Time to die */
                  failStage(t)
                  results.failure(t)
              }
            }

            override def onUpstreamFinish(): Unit = {
              results.success(digest.digest())
              super.onUpstreamFinish()
            }
            override def onUpstreamFailure(t: Throwable): Unit = {
              results.failure(t)
              super.onUpstreamFailure(t)
            }
          })
        }

      (logic, results.future)
    }
  }
}
