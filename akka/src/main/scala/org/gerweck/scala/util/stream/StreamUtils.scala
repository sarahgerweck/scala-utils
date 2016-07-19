package org.gerweck.scala.util.stream

import scala.collection.mutable

import akka.NotUsed
import akka.stream._
import akka.stream.scaladsl._
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.util.ByteString

/** General utilities for building streams
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
object StreamUtils {
  /** A flow that takes [[akka.util.ByteString]], and turns it into UTF-8 strings, split on
    * one of several split characters.
    *
    * The split character is kept at the end of each string.
    */
  def splittingFlowOn(splitChars: Set[Char]): Flow[ByteString, String, NotUsed] = {
    Flow[ByteString]
      .map(Some(_)).concat(Source.single(None)).statefulMapConcat { () =>
        val buffered = new mutable.StringBuilder
        obs: Option[ByteString] => {
          obs match {
            case Some(bs) =>
              var production = Vector.empty[String]
              for (c <- bs.utf8String) {
                buffered += c
                if (splitChars(c)) {
                  production :+= buffered.mkString
                  buffered.clear()
                }
              }
              production
            case None if buffered.nonEmpty =>
              List(buffered.mkString)
            case None =>
              Nil
          }
        }
      }
  }

  /** An optimized version of [[splittingFlowOn]] that splits on `'n'` or `'\r'`. */
  lazy val splittingFlow: Flow[ByteString, String, NotUsed] = {
    Flow[ByteString]
      .map(Some(_)).concat(Source.single(None)).statefulMapConcat { () =>
        val buffered = new mutable.StringBuilder
        obs: Option[ByteString] => {
          obs match {
            case Some(bs) =>
              var production = Vector.empty[String]
              val str = bs.utf8String
              val strSize = str.size
              var i = 0
              while (i < strSize) {
                val c = str(i)
                buffered += c
                if (c == '\n' || c == '\r') {
                  production :+= buffered.mkString
                  buffered.clear()
                }
                i += 1
              }
              production
            case None if buffered.nonEmpty =>
              List(buffered.mkString)
            case None =>
              Nil
          }
        }
      }
  }

  /** A flow that turns a stream of [[akka.util.ByteString]] into a stream of
    * UTF-8 strings, each containing one line.
    */
  @deprecated("Use splittingFlow instead", "2.2.3")
  lazy val linesFlow: Flow[ByteString, String, NotUsed] = {
    Flow[ByteString]
      .via(Framing.delimiter(
        ByteString("\n"),
        maximumFrameLength = 256,
        allowTruncation = true))
      .map(_.utf8String)
  }

  /** Create a [[akka.stream.scaladsl.Flow]] that sends all events to a given
    * [[akka.stream.scaladsl.Sink]], while still allowing them to flow to the
    * output as well.
    */
  def makeTap[A, B](sink: Sink[A, B]): Flow[A, A, B] = Flow fromGraph {
    GraphDSL.create(sink) { implicit b =>
      sink => {
        val broadcast = b.add(Broadcast[A](2))

        /* One output of our broadcast goes to the sink, the other will be our flow output */
        broadcast ~> sink

        FlowShape(broadcast.in, broadcast.out(1))
      }
    }
  }

}
