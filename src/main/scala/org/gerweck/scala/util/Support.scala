package org.gerweck.scala.util

import language.experimental.macros

import scala.reflect.macros.Context

import org.log4s._

object Support {
  import SupportMacros._

  def support = macro support_simple

  def support(logger: Logger, level: LogLevel) = macro support_complex
}

private object SupportMacros {
  def support_simple(c: Context): c.Expr[Unit] = {
    import c.universe._

    val callerName = getCallerName(c)

    val message = s"Method `$callerName` not supported"

    reify { throw new UnsupportedOperationException(c.literal(message).splice) }
  }

  def support_complex(c: Context)(logger: c.Expr[Logger], level: c.Expr[LogLevel]): c.Expr[Unit] = {
    import c.universe._

    val callerName = getCallerName(c)

    val exceptionMessage = s"Method `$callerName` not supported"
    val logMessage = s"Got unsupported call to `$callerName`"

    reify {
      (logger.splice).apply(level.splice).apply(c.literal(logMessage).splice)
      throw new UnsupportedOperationException(c.literal(exceptionMessage).splice)
    }
  }

  @inline private def getCallerName(c: Context): String = {
    val caller = c.enclosingMethod
    caller.symbol.name.decoded
  }
}
