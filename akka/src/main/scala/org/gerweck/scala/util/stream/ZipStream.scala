package org.gerweck.scala.util.stream

import scala.concurrent._
import scala.concurrent.duration._
import scala.util._

import java.io._
import java.nio.file._
import java.util.zip._

import akka.{ Done, NotUsed }
import akka.stream._
import akka.stream.scaladsl._
import akka.stream.stage._
import akka.util.ByteString

import org.log4s._

import org.gerweck.scala.util.io._

/** A stream that takes in zip entries and produces a byte stream as output.
  *
  * @author Sarah Gerweck <sarah@atscale.com>
  */
object ZipStream {
  private[this] val logger = getLogger

  /* These are NOT serializable, as they contain a `Source`. (They must not be case classes.) */
  final class Entry(val location: String, val data: Source[ByteString, _]) {
    private[ZipStream] val toActionSource: Source[ZipAction, NotUsed] = {
      Source.single(ZipAction.NewEntry(location)) ++
      data.map(ZipAction.Data) ++
      Source.single(ZipAction.CloseEntry)
    }
  }

  /** A zip compressor that takes in entries and flows out bytes. */
  def asFlow(outputTimeout: FiniteDuration = 1.days)(implicit ec: ExecutionContext): Flow[Entry, ByteString, Future[IOResult]] = {
    entryToActionFlow
      .viaMat(actionToBytesFlow(outputTimeout))(Keep.right)
  }

  /** A zip compressor that takes in entries and writes them to a file. */
  def toFile(path: Path)(implicit ec: ExecutionContext): Sink[Entry, Future[IOResult]] = {
    val os = { () =>
      Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE)
    }
    val outSink: Sink[ZipAction, Future[IOResult]] = simpleZipOutputSink(os)(ec)

    entryToActionFlow
      .toMat(outSink)(Keep.right)
  }

  /* INTERNALS */

  private[this] val entryToActionFlow = {
    Flow[Entry].flatMapConcat(_.toActionSource)
  }

  private[this] def actionToBytesFlow(outputTimeout: FiniteDuration)(implicit ec: ExecutionContext): Flow[ZipAction, ByteString, Future[IOResult]] = {
    Flow.fromGraph {
      val s = StreamConverters.asOutputStream(outputTimeout)
      val z = new ZipOutputSink(ec)
      GraphDSL.create(s, z)(Keep.both) { implicit b => (oss, zos) =>
        import GraphDSL.Implicits._

        val outputStreamConnector = b.add {
          Sink.foreach[(OutputStream, Promise[OutputStream])] { case (os, pos) =>
            pos.success(os)
          }
        }
        val materializedStreams = b.materializedValue .map { case (os, (pos, _)) => (os, pos) }

        materializedStreams ~> outputStreamConnector

        FlowShape(zos.in, oss.out)
      }
    }.mapMaterializedValue(_._2._2)
  }

  private[this] def simpleZipOutputSink(os: () => OutputStream)(implicit ec: ExecutionContext): Sink[ZipAction, Future[IOResult]] = {
    Sink.fromGraph {
      GraphDSL.create(new ZipOutputSink(ec)) { implicit b => zos =>
        import GraphDSL.Implicits._

        val completer = b.add {
          Sink.foreach[Promise[OutputStream]] { pos =>
            logger.trace(s"Fulfilling OutputStream promise: $pos")
            val osTry = Future { os() }(ioExecutorContext)
            pos.completeWith(osTry)
          }
        }
        val mpd = b.materializedValue.map(_._1) ~> completer
        SinkShape(zos.in)
      }
    }.mapMaterializedValue(_._2)
  }

  private[this] class ZipOutputSink(ec: ExecutionContext) extends GraphStageWithMaterializedValue[SinkShape[ZipAction], (Promise[OutputStream], Future[IOResult])] {
    import ZipOutputSink._

    val in: Inlet[ZipAction] = Inlet("ZipOutputSink.in")
    override val shape: SinkShape[ZipAction] = SinkShape(in)

    def callbackDispatcher = ec
    /* TODO: Make this configurable. */
    def ioDispatcher = ioExecutorContext

    override def createLogicAndMaterializedValue(inheritedAttributes: Attributes): (GraphStageLogic, (Promise[OutputStream], Future[IOResult])) = {
      val osPromise = Promise[OutputStream]
      val ioResults = Promise[IOResult]
      val logic = {
        new GraphStageLogic(shape) {
          private[this] var streams = Option.empty[Streams]
          private[this] var currentEntry = Option.empty[ZipEntry]
          private[this] var entryCount = 0

          private[this] def fos = streams.get.fos
          private[this] def zos = streams.get.zos

          override def preStart(): Unit = {
            val streamsFuture = {
              osPromise.future.flatMap { os =>
                Streams(os)(ioDispatcher)
              }(callbackDispatcher)
            }

            streamsFuture.onComplete {
              getAsyncCallback[Try[Streams]] {
                case Success(st) =>
                  streams = Some(st)
                  pull(in)
                case Failure(t) =>
                  ioResults.success(IOResult(entryCount, Failure(t)))
                  failStage(t)
              }.invoke
            }(callbackDispatcher)
          }

          /* We factor this out so we can register just once, reducing the overhead. */
          private[this] def postWriteAction(writeResult: Try[Done]): Unit = writeResult match {
            case Success(_) =>
              pull(in)
            case Failure(t) =>
              failStage(t)
              ioResults.success(IOResult(entryCount, Failure(t)))
          }

          setHandler(in, new InHandler {
            /* Normally you would create your callback in `preStart`, but this responds to a
             * future that it purely internal, so it cannot be subject to any race conditions.
             */
            private[this] val postWriteCallback = getAsyncCallback[Try[Done]](postWriteAction)

            override def onPush(): Unit = {
              val input = grab(in)
              val writingFuture =
                Future {
                  /* TODO: Ideally we wouldn't modify `currentEntry` or `entryCount` from out of
                   * the main thread. However, I don't believe it can cause any problems here as
                   * we only have a single future at a time and we never do concurrent access. */
                  input match {
                    case ZipAction.NewEntry(loc) =>
                      currentEntry foreach { ce =>
                        logger.warn(s"Zip entry $ce was not explicitly closed (closing automatically).")
                        zos.closeEntry()
                      }
                      val ze = new ZipEntry(loc)
                      zos.putNextEntry(ze)
                      currentEntry = Some(ze)
                      entryCount += 1
                      Done

                    case ZipAction.Data(bs) =>
                      require(currentEntry.isDefined)
                      zos.write(bs.toArray)
                      Done

                    case ZipAction.CloseEntry =>
                      currentEntry = None
                      zos.closeEntry()
                      Done
                  }
                }(ioDispatcher)

              writingFuture.onComplete(postWriteCallback.invoke)(callbackDispatcher)
            }

            override def onUpstreamFinish(): Unit = {
              logger.trace("Got upstream finish")
              /* We're not allowed to use any callbacks here, just schedule our future
               * and use the results to populate the `IOResult`.
               */
              closeStreams().onComplete { tr =>
                logger.trace(s"Got closeStreams completion: $tr")
                ioResults.success(IOResult(entryCount, tr))
              }(callbackDispatcher)
              super.onUpstreamFinish()
            }

            override def onUpstreamFailure(t: Throwable): Unit = {
              closeStreams()
              ioResults.failure(t)
              super.onUpstreamFailure(t)
            }

            private[this] def closeStreams(): Future[Done] = {
              streams match {
                case Some(s) => s.close(ioDispatcher)
                case None    => Future.successful(Done)
              }
            }
          })
        }
      }

      (logic, (osPromise, ioResults.future))
    }
  }

  private[this] object ZipOutputSink {
    private[this] final val forceUnderlyingClose = false
    class Streams(val fos: OutputStream, val zos: ZipOutputStream) {
      def close(ioDispatcher: ExecutionContext): Future[Done] = {
        Future {
          val zc = Try { zos.close() }
          if (forceUnderlyingClose) {
            val fc = Try { fos.close() }
            zc.flatMap(Function.const(fc)).get
          } else {
            zc.get
          }
          Done
        }(ioDispatcher)
      }
    }
    object Streams {
      def apply(fos: OutputStream)(ioDispatcher: ExecutionContext): Future[Streams] = {
        Future {
          var zos = Option.empty[ZipOutputStream]
          Try {
            zos = Some(new ZipOutputStream(fos))
            new Streams(fos, zos.get)
          } .recover { case t =>
            zos.foreach { s => Try(s.close()) }
            Try(fos.close)
            throw t
          } .get
        }(ioDispatcher)
      }
    }
  }

  private[this] sealed trait ZipAction extends Serializable
  private[this] object ZipAction {
    final case class NewEntry(name: String) extends ZipAction
    final case class Data(data: ByteString) extends ZipAction
    final case object CloseEntry extends ZipAction
  }
}
