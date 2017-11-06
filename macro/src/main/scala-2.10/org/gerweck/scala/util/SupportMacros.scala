package org.gerweck.scala.util

import language.experimental.macros

import scala.reflect.macros.Context

import org.log4s._

private[util] object SupportMacros {
  def support_simple(c: Context): c.Expr[Nothing] = {
    import c.universe._

    val callerName = getCallerName(c)

    val message = s"Method `$callerName` not supported"

    reify {
      throw new java.lang.UnsupportedOperationException(c.literal(message).splice)
    }
  }

  def support_complex(c: Context)(logger: c.Expr[Logger], level: c.Expr[LogLevel]): c.Expr[Nothing] = {
    import c.universe._

    val callerName = getCallerName(c)

    val exceptionMessage = s"Method `$callerName` not supported"
    val logMessage = s"Got unsupported call to `$callerName`"

    reify {
      (logger.splice).apply(level.splice).apply(c.literal(logMessage).splice)
      throw new java.lang.UnsupportedOperationException(c.literal(exceptionMessage).splice)
    }
  }

  // TODO: Reduce code duplication.
  def support_warn(c: Context): c.Expr[Nothing] = {
    import c.universe._

    val logger = c.Expr[Logger](Ident(newTermName("logger")))

    val callerName = getCallerName(c)
    val logMessage = s"Got unsupported call to `$callerName`"
    val exceptionMessage = s"Method `$callerName` is not supported"

    reify {
      (logger.splice).warn(c.literal(logMessage).splice)
      throw new java.lang.UnsupportedOperationException(c.literal(exceptionMessage).splice)
    }
  }

  @inline private def getCallerName(c: Context): String = {
    val caller = c.enclosingMethod
    caller.symbol.name.decodedName.toString
  }
}
