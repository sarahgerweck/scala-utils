package org.gerweck.scala.util.stream

import language.implicitConversions

import scala.concurrent._

import java.io.File
import java.nio.ByteBuffer
import java.nio.file.Path

import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString

/** Utilities for working with streams to and from files.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
object FileStreams {
  def writeFile(p: PathMagnet, content: ByteStreamMagnet)(implicit mat: Materializer, ec: ExecutionContext): Future[Unit] = {
    writeFileDetailed(p, content) flatMap { ior =>
      if (ior.wasSuccessful) {
        Future.successful(())
      } else {
        Future.failed(ior.getError)
      }
    }
  }
  def writeFileDetailed(p: PathMagnet, content: ByteStreamMagnet)(implicit mat: Materializer): Future[IOResult] = {
    val bs = content.bs
    val src = Source.single(bs)
    val sink = FileIO.toPath(p.path)
    val rg = src.toMat(sink)(Keep.right)
    rg.run
  }

  def readString(p: PathMagnet)(implicit mat: Materializer, ec: ExecutionContext): Future[String] = {
    val src = FileIO.fromPath(p.path)
    val sink = Sink.fold(ByteString.empty){ (a: ByteString, b: ByteString) => a ++ b }.mapMaterializedValue(_.map(_.utf8String))
    val rg = src.toMat(sink)(Keep.right)
    rg.run
  }

  implicit final class PathMagnet(val path: Path) extends AnyVal
  object PathMagnet {
    implicit def f2pm(f: File) = new PathMagnet(f.toPath)
  }

  implicit final class ByteStreamMagnet(val bs: ByteString) extends AnyVal
  object ByteStreamMagnet {
    implicit def string2bsm(s: String) = new ByteStreamMagnet(ByteString(s))
    implicit def barr2bsm(ba: Array[Byte]) = new ByteStreamMagnet(ByteString(ba))
    implicit def bb2bsm(bb: ByteBuffer) = new ByteStreamMagnet(ByteString(bb))
  }
}
