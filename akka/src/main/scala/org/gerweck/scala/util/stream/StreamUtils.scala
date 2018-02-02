package org.gerweck.scala.util.stream

import scala.collection.mutable

import akka.NotUsed
import akka.stream.scaladsl._
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

  /** An optimized version of [[splittingFlowOn]] that splits on `'\n'` or `'\r'`. */
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
}
