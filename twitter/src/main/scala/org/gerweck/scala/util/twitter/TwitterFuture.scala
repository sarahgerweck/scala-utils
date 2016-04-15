package org.gerweck.scala.util.twitter

import language.implicitConversions

import org.log4s._

import com.twitter.util.{Future, Try, Return, Throw, Duration, Awaitable}

/** Utilities for working with twitter futures.
  *
  * Especially valuable are this class's implicit conversions that can
  * automatically convert from a Twitter Future to a Scala Future and
  * back. This can be very useful in mixed environments.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
object TwitterFuture {
  val logger = getLogger

  implicit def futureToTwitterFuture[A](inner: scala.concurrent.Future[A]): com.twitter.util.Future[A] = {
    new Future[A] {
      import scala.concurrent.ExecutionContext.Implicits.global
      def respond(k: Try[A] => Unit): com.twitter.util.Future[A] = {
        logger.trace("Got call to respond")
        inner.onComplete[Unit](v => k(tryToTwitterTry[A](v)))
        this
      }

      def poll: Option[Try[A]] = {
        logger.trace("Got call to poll")
        inner.value map { x => x }
      }

      def raise(interrupt: Throwable) { logger.debug(s"Got unsupported call to raise($interrupt)") }

      def transform[B](f: com.twitter.util.Try[A] => com.twitter.util.Future[B]): com.twitter.util.Future[B] = {
        logger.trace("Got call to transform")
        val p = scala.concurrent.Promise[B]()
        inner onComplete { x =>
          val future = f(x)
          future respond { t => p.complete(t); () }
        }
        futureToTwitterFuture(p.future)
      }

      def ready(timeout: com.twitter.util.Duration)(implicit permit: com.twitter.util.Awaitable.CanAwait): this.type = {
        logger.trace("Got call to ready")
        scala.concurrent.Await.ready(inner, timeout)
        this
      }

      def result(timeout: com.twitter.util.Duration)(implicit permit: com.twitter.util.Awaitable.CanAwait): A = {
        logger.trace("Got call to result")
        scala.concurrent.Await.result(inner, timeout)
      }

      def isReady(implicit permit: com.twitter.util.Awaitable.CanAwait): Boolean = {
        logger.trace("Got call to isReady")
        inner.isCompleted
      }
    }
  }

  implicit def durationToTwitterDuration(sd: scala.concurrent.duration.Duration): Duration = Duration.fromNanoseconds(sd.toNanos)
  implicit def twitterDurationToDuration(td: Duration): scala.concurrent.duration.Duration = scala.concurrent.duration.Duration.fromNanos(td.inNanoseconds)

  implicit def tryToTwitterTry[A](scale: scala.util.Try[A]): Try[A] = scale match {
    case scala.util.Success(s) => Return(s)
    case scala.util.Failure(e) => Throw(e)
  }

  implicit def twitterTryToTry[A](twit: Try[A]): scala.util.Try[A] = twit match {
    case Return(s) => scala.util.Success(s)
    case Throw(e)  => scala.util.Failure(e)
  }

}
