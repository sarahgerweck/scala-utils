package org.gerweck.scala.util

import language.experimental.macros

import scala.reflect.macros.whitebox.Context

import org.log4s._

private object SupportMacros {
  def support_simple(c: Context): c.Expr[Nothing] = {
    import c.universe._

    val callerName = getCallerName(c)

    val message = s"Method `$callerName` not supported"

    c.Expr[Nothing](q"throw new java.lang.UnsupportedOperationException($message)")
  }

  def support_complex(c: Context)(logger: c.Expr[Logger], level: c.Expr[LogLevel]): c.Expr[Nothing] = {
    import c.universe._

    val callerName = getCallerName(c)

    val exceptionMessage = s"Method `$callerName` not supported"
    val logMessage = s"Got unsupported call to `$callerName`"

    c.Expr[Nothing](q"""$logger.apply($level).apply($logMessage)
                        throw new java.lang.UnsupportedOperationException($exceptionMessage)""")
  }

  // TODO: Reduce code duplication.
  def support_warn(c: Context): c.Expr[Nothing] = {
    import c.universe._

    val callerName = getCallerName(c)
    val logMessage = s"Got unsupported call to `$callerName`"
    val exceptionMessage = s"Method `$callerName` is not supported"

    c.Expr[Nothing](q"""logger.warn($logMessage)
                        throw new java.lang.UnsupportedOperationException($exceptionMessage)""")
  }

  @inline private def getCallerName(c: Context): String = {
    val caller = c.enclosingMethod
    caller.symbol.name.decodedName.toString
  }
}
