package org.gerweck.scala.util

import language.reflectiveCalls

import scala.concurrent._

object resource {
  // TODO: Make this a macro
  final def using[A <: AutoCloseable,B](ac: A)(body: A => B): B =
    try { body(ac) }
    finally { ac.close() }

  final def usingDynamic[A <: { def close(): Unit },B](rsrc: A)(body: A => B): B =
    try { body(rsrc) }
    finally { rsrc.close() }

  final def usingAsync[A <: AutoCloseable, B](ac: A)(body: A => Future[B])(implicit ec: ExecutionContext): Future[B] = {
    val resultFuture = body(ac)
    resultFuture map { r => ac.close(); r }
  }

  final def usingAsyncDynamic[A <: { def close(): Unit }, B](rsrc: A)(body: A => Future[B])(implicit ec: ExecutionContext): Future[B] = {
    val resultFuture = body(rsrc)
    resultFuture map { r => rsrc.close(); r }
  }
}
