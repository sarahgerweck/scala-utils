package org.gerweck.scala.util.stream
package impl

import scala.concurrent._
import scala.util._

import java.io.{ InputStream, IOException }
import java.nio.file.attribute.FileTime
import java.time.Instant
import java.util.zip.{ ZipInputStream, ZipEntry }

import akka.Done
import akka.stream._
import akka.stream.stage._
import akka.stream.scaladsl._
import akka.util._

import org.log4s._

import org.gerweck.scala.util.io.ioExecutorContext

import ZipStream._

/** An input source that produces zip entries from an `InputStream`.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
private[stream] class ZipInputSource(ec: ExecutionContext)(readSize: Int = defaultFlowBuffer.getOrElse(8192)) extends ZipStage[SourceShape[ZipStream.ZipAction], InputStream](ec) {
  import ZipInputSource._

  val out: Outlet[ZipAction] = Outlet("ZipInputSource.out")
  override val shape: SourceShape[ZipAction] = SourceShape(out)

  override def createLogicAndMaterializedValue(inheritedAttributes: Attributes): (GraphStageLogic, (Promise[InputStream], Future[IOResult])) = {
    val isPromise = Promise[InputStream]
    val ioResults = Promise[IOResult]
    val logic = {
      new GraphStageLogic(shape) {
        private[this] val streamsP = Promise[Streams]
        private[this] val streams = streamsP.future
        private[this] var currentEntry = Option.empty[ZipEntry]
        private[this] var entryCount = 0

        private[this] val buffer = new Array[Byte](readSize)

        private[this] var currentlyReading = false

        private[this] def is(implicit str: Streams): InputStream = str.bs
        private[this] def zis(implicit str: Streams): ZipInputStream = str.ws

        override def preStart(): Unit = {
          val streamsFuture = {
            val isFuture = isPromise.future
            isFuture.flatMap { is =>
              Streams(is)(ioDispatcher)
            }(callbackDispatcher)
          }

          streamsFuture.onComplete {
            getAsyncCallback[Try[Streams]] {
              case Success(st) =>
                streamsP.success(st)
              case Failure(t) =>
                ioResults.success(IOResult(entryCount, Failure(t)))
                failStage(t)
            }.invoke
          }(callbackDispatcher)
        }

        private[this] def readData(): Unit = {
          assert(!currentlyReading, "Tried to read when already reading")
          currentlyReading = true
          streams .map { implicit s =>
            if (currentEntry.isEmpty) {
              val nextOption = Option(zis.getNextEntry())
              nextOption match {
                case Some(ne) =>
                  currentEntry = Some(ne)
                  entryCount += 1
                  val metadata = {
                    ZipEntryMetadata(
                      ne.getName,
                      Option(ne.getCreationTime).map(_.toInstant),
                      Option(ne.getLastAccessTime).map(_.toInstant),
                      Option(ne.getLastModifiedTime).map(_.toInstant),
                      Option(ne.getComment),
                      Option(ne.getExtra)
                    )
                  }
                  ZipAction.NewEntry(metadata)
                case None =>
                  InputAction.EndInput
              }
            } else {
              /* We're intentionally reusing this buffer: we make a fresh copy of the bytes needed */
              zis.read(buffer) match {
                case -1 =>
                  currentEntry = None
                  ZipAction.CloseEntry
                case n if n > 0 =>
                  ZipAction.Data(ByteString.fromArray(buffer, 0, n))
                case other =>
                  /* This shouldn't ever be zero or less than -1 */
                  throw new IOException(s"Shouldn't ever read $other bytes from a zip entry")
              }
            }
          }(ioDispatcher) .onComplete {
            getAsyncCallback[Try[InputAction]] { oza =>
              currentlyReading = false
              oza match {
                case Success(za: ZipAction) =>
                  push(out, za)

                case Success(InputAction.EndInput) =>
                  complete(out)

                case Failure(t) =>
                  val newT = {
                    val msg = currentEntry match {
                      case Some(e) => s"Error when reading $e"
                      case None    => "Error when reading zip data"
                    }
                    new IOException(msg, t)
                  }
                  ioResults.success(IOResult(entryCount, Failure(newT)))
                  failStage(t)
              }
            }.invoke
          }(callbackDispatcher)
        }

        setHandler(out, new OutHandler {
          override def onPull(): Unit = {
            readData()
          }
          override def onDownstreamFinish(): Unit = {
            streams.map(_.close(ioDispatcher))(callbackDispatcher)
          }
        })
      }
    }
    (logic, (isPromise, ioResults.future))
  }
}

private[this] object ZipInputSource {
  private val logger = getLogger

  private type Streams = StreamPair[InputStream, ZipInputStream]
  private object Streams {
    def apply(is: InputStream)(ioDispatcher: ExecutionContext): Future[Streams] = {
      StreamPair(is)(new ZipInputStream(_))(ioDispatcher)
    }
  }
}