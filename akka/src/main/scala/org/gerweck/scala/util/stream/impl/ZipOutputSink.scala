package org.gerweck.scala.util.stream
package impl

import scala.concurrent._
import scala.util._

import java.io.OutputStream
import java.nio.file.attribute.FileTime
import java.util.zip.{ ZipOutputStream, ZipEntry }

import akka.Done
import akka.stream._
import akka.stream.stage._
import akka.stream.scaladsl._
import akka.util._

import org.log4s._

import org.gerweck.scala.util.io.ioExecutorContext

import ZipStream._

/** An output sink that writes zip actions into a zip stream.
  *
  * This is part of the internal API and it should never be used directly.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
private[stream] class ZipOutputSink(ec: ExecutionContext) extends ZipStage[SinkShape[ZipAction], OutputStream](ec) {
  import ZipOutputSink._

  val in: Inlet[ZipAction] = Inlet("ZipOutputSink.in")
  override val shape: SinkShape[ZipAction] = SinkShape(in)

  override def createLogicAndMaterializedValue(inheritedAttributes: Attributes): (GraphStageLogic, (Promise[OutputStream], Future[IOResult])) = {
    val osPromise = Promise[OutputStream]
    val ioResults = Promise[IOResult]
    val logic = {
      new GraphStageLogic(shape) {
        private[this] var streams = Option.empty[Streams]
        private[this] var currentEntry = Option.empty[ZipEntry]
        private[this] var entryCount = 0
        private[this] var outstandingFuture = Option.empty[Future[Done]]

        private[this] def fos = streams.get.bs
        private[this] def zos = streams.get.ws

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
        private[this] def postWriteAction(writeResult: Try[Done]): Unit = {
          writeResult match {
            case Success(_) =>
              pull(in)
            case Failure(t) =>
              failStage(t)
              ioResults.success(IOResult(entryCount, Failure(t)))
          }
          outstandingFuture = None
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
                  case ZipAction.NewEntry(metadata) =>
                    currentEntry foreach { ce =>
                      logger.warn(s"Zip entry $ce was not explicitly closed (closing automatically).")
                      zos.closeEntry()
                    }
                    val entry = makeZipEntry(metadata)
                    zos.putNextEntry(entry)
                    currentEntry = Some(entry)
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
            outstandingFuture = Some(writingFuture)

            writingFuture.onComplete(postWriteCallback.invoke)(callbackDispatcher)
          }

          override def onUpstreamFinish(): Unit = {
            logger.trace("Got upstream finish")
            val closingF = outstandingFuture.getOrElse(Future.successful(Done)) .flatMap { _ =>
              logger.trace(s"Trying to close streams")
              closeStreams()
            }(callbackDispatcher)
            closingF.onComplete {
              case s@Success(Done) =>
                ioResults.success(IOResult(entryCount, s))
                logger.trace("Finished processing upstream finish")
                completeStage()
              case Failure(t) =>
                ioResults.failure(t)
                failStage(t)
            }(callbackDispatcher)
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

private[stream] object ZipOutputSink {
  private val logger = getLogger

  private type Streams = StreamPair[OutputStream, ZipOutputStream]
  private object Streams {
    def apply(fos: OutputStream)(ioDispatcher: ExecutionContext): Future[Streams] = {
      StreamPair(fos)(new ZipOutputStream(_))(ioDispatcher)
    }
  }

  private[stream] def simple(os: () => OutputStream)(implicit ec: ExecutionContext): Sink[ZipAction, Future[IOResult]] = {
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

  private def makeZipEntry(metadata: ZipEntryMetadata): ZipEntry = {
    import language.implicitConversions

    @inline implicit def lifter(f: FileTime => ZipEntry): java.time.Instant => ZipEntry =
      f compose FileTime.from

    val ze = new ZipEntry(metadata.name)
    metadata.creation     foreach ze.setCreationTime
    metadata.lastAccess   foreach ze.setLastAccessTime
    metadata.lastModified foreach ze.setLastAccessTime
    metadata.comment      foreach ze.setComment
    metadata.extra        foreach ze.setExtra
    ze
  }
}
