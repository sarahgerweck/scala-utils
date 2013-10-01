package org.gerweck.scala.util

import language.reflectiveCalls

object resource {
  // TODO: Make this a macro
  final def using[A <: AutoCloseable,B](ac: A)(body: A => B): B =
    try { body(ac) }
    finally { ac.close() }

  final def usingDynamic[A <: { def close(): Unit },B](rsrc: A)(body: A => B): B =
    try { body(rsrc) }
    finally { rsrc.close() }
}
