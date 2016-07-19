package org.gerweck.scala.util.stream

import scala.concurrent._
import scala.util.Try
import scala.util.control.NonFatal

import java.io._
import java.util.concurrent.Executors
import java.util.UUID

import org.gerweck.scala.util._
import org.gerweck.scala.util.io._
import org.log4s._

import akka.NotUsed
import akka.stream.FlowShape
import akka.stream.scaladsl._
import akka.util.ByteString

/** A streaming pretty printer for XML.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
object XmlPrettyPrinter {
  private[this] final val defaultBufferSize = 4096
  private[this] val logger = getLogger

  def asFlow(spaces: Int = 2,
             inputBuffer: Int = defaultBufferSize,
             outputBuffer: Int = defaultBufferSize)
          : Flow[ByteString, ByteString, NotUsed] = Flow fromGraph {
    GraphDSL.create() { implicit b =>
      val input  = StreamInverter(inputBuffer)
      val output = StreamInverter(outputBuffer)

      val jobString = {
        val jobId = if (logger.isDebugEnabled) Some(UUID.randomUUID().toString) else None
        (Seq("XML formatting job") ++ jobId).mkString(" ")
      }

      Future {
        try {
          logger.debug(s"Starting $jobString")
          timed(logger, jobString, Debug) {
            prettyPrintXml(input.input, output.output, spaces)
          }
        } catch { case NonFatal(t) =>
          logger.error(t)(s"Error in $jobString")
        } finally {
          try {
            output.output.close()
          } catch { case NonFatal(t) =>
            logger.error(t)(s"Error when closing $jobString")
          }
        }
      } (ioExecutorContext)

      val sink = b.add(StreamConverters.fromOutputStream(() => input.output))
      val src  = b.add(StreamConverters.fromInputStream(()  => output.input))

      FlowShape(sink.in, src.out)
    }
  }

  protected[this] def prettyPrintXml(from: InputStream, to: OutputStream, spaces: Int = 2): Unit = {
    import javax.xml.transform._
    import javax.xml.transform.sax._
    import javax.xml.transform.stream._
    try {
      val transformer = TransformerFactory.newInstance().newTransformer()
      transformer.setOutputProperty(OutputKeys.INDENT, "yes")
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", spaces.toString)
      val result = new StreamResult(to)
      val source = new SAXSource(new org.xml.sax.InputSource(from))
      transformer.transform(source, result)
    } catch { case NonFatal(e) =>
      logger.warn(e)("Error when formatting XML")
    }
  }
}
