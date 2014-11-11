package org.gerweck.scala.util

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/** Utility mechanisms for constructing thread factories.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
object ThreadFactories {
  type ThreadNamer = Int => String

  /** Construct a new thread factory with given settings.
    *
    * @param namer a function that takes the thread counter (a positive integer) and gives the thread's name.
    * @param daemon whether to construct daemon threads.
    * @param priority the priority of new threads.
    * @param threadGroup the group of new threads.
    */
  def apply(namer: ThreadNamer,
            daemon: Boolean = true,
            priority: Int = Thread.NORM_PRIORITY,
            threadGroup: Option[ThreadGroup] = None): ThreadFactory = {
    new ThreadFactory {
      private[this] val threadCount = new AtomicInteger(0)

      override def newThread(r: Runnable): Thread = {
        val number = threadCount.incrementAndGet()
        val name = namer(number)

        val thread = new Thread(threadGroup.orNull, r, name)

        thread.setDaemon(daemon)
        thread.setPriority(priority)

        thread
      }
    }
  }
}
