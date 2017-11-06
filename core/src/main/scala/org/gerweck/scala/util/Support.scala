package org.gerweck.scala.util

import language.experimental.macros

import org.log4s._

object Support {
  import SupportMacros._

  def warnSupport = macro support_warn

  def support: Nothing = macro support_simple

  def support(logger: Logger, level: LogLevel) = macro support_complex
}
