package org.gerweck.scala.util.stream

import language.implicitConversions

import scala.concurrent._

import java.io.File
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
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
    val src = Source.single(content.bs)
    val sink = FileIO.toPath(p.path)
    val rg = src.toMat(sink)(Keep.right)
    rg.run
  }

  def readString(p: PathMagnet, enc: EncodingMagnet = UTF_8)(implicit mat: Materializer, ec: ExecutionContext): Future[String] = {
    val src = FileIO.fromPath(p.path)
    val sink = Sink.fold[ByteString, ByteString](ByteString.empty)(_ ++ _).mapMaterializedValue(_.map(_.utf8String))
    val rg = src.toMat(sink)(Keep.right)
    rg.run
  }

  implicit final class EncodingMagnet(val enc: String) extends AnyVal
  object EncodingMagnet {
    implicit def cs2enc(cs: Charset): EncodingMagnet = new EncodingMagnet(cs.name)
  }

  implicit final class PathMagnet(val path: Path) extends AnyVal
  object PathMagnet {
    implicit def f2pm(f: File): PathMagnet = new PathMagnet(f.toPath)
  }

  implicit final class ByteStreamMagnet(val bs: ByteString) extends AnyVal
  object ByteStreamMagnet {
    implicit def string2bsm(s: String): ByteStreamMagnet = new ByteStreamMagnet(ByteString(s))
    implicit def stringEnc2bsm(s: String, enc: EncodingMagnet): ByteStreamMagnet = new ByteStreamMagnet(ByteString(s, enc.enc))
    implicit def barr2bsm(ba: Array[Byte]): ByteStreamMagnet = new ByteStreamMagnet(ByteString(ba))
    implicit def bb2bsm(bb: ByteBuffer): ByteStreamMagnet = new ByteStreamMagnet(ByteString(bb))
  }
}
