package org.gerweck.scala.util.stream

import scala.concurrent._
import scala.concurrent.duration._

import akka._
import akka.actor._
import akka.stream._
import akka.stream.scaladsl._

import com.typesafe.config._

/** Global objects required for testing hashes.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
object TestGlobals {

  implicit val timeout = 10.minutes
  val actorConfig = ConfigFactory.parseString("akka.daemonic = true")
  implicit val system = ActorSystem("test", config = Some(actorConfig))
  implicit val mat = ActorMaterializer()

}
