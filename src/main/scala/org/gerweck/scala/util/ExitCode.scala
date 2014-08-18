package org.gerweck.scala.util

import spire.syntax.literals._

/** An exit code used for UNIX processes.
  *
  * These are based on the BSD standardized exit codes.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
final class ExitCode private (val code: Byte) extends AnyVal {
  final def exit() {
    scala.sys.exit(code)
  }
}

/** A container of standardized exit codes.
  *
  * These are based on the
  * [[http://www.freebsd.org/cgi/man.cgi?query=sysexits&manpath=FreeBSD+10.0-RELEASE BSD standardized exit codes]].
  * Linux and SysV don't really have a OS-level standard, and many applications use different
  * codes, but these are the most standardized codes that exist.
  */
object ExitCode {
  final val Okay        = new ExitCode(b"0")
  final val Usage       = new ExitCode(b"64")
  final val DataErr     = new ExitCode(b"65")
  final val NoInput     = new ExitCode(b"66")
  final val NoUser      = new ExitCode(b"67")
  final val NoHost      = new ExitCode(b"68")
  final val Unavailable = new ExitCode(b"69")
  final val Software    = new ExitCode(b"70")
  final val OSErr       = new ExitCode(b"71")
  final val OSFile      = new ExitCode(b"72")
  final val CantCreat   = new ExitCode(b"73")
  final val IOErr       = new ExitCode(b"74")
  final val TempFail    = new ExitCode(b"75")
  final val Protocol    = new ExitCode(b"76")
  final val NoPerm      = new ExitCode(b"77")
  final val Config      = new ExitCode(b"78")
}
