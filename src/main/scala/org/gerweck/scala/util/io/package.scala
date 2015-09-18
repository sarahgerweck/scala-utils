package org.gerweck.scala.util

import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors

/**
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
package object io {
  private[this] final val streamThreadPrefix = "scala-utils-iothread-"
  private[this] final val ioThreadPriority = Thread.NORM_PRIORITY - 1
  lazy val ioExecutorContext = {
    val threadFactory = {
      ThreadFactories({streamThreadPrefix + (_: Int)}, daemon = true, priority = ioThreadPriority)
    }
    ExecutionContext.fromExecutor(Executors.newCachedThreadPool(threadFactory))
  }
}
