package org.gerweck.scala.util

import spire.syntax.literals._

/** An exit code used for UNIX processes.
  *
  * These are based on the BSD standardized exit codes.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
final class ExitCode private (val code: Byte) extends AnyVal {
  final def exit(): Nothing = {
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

  /** The command succeeded. */
  final val Okay        = new ExitCode(b"0")

  /** The command was used incorrectly.
    *
    * E.g., it was used with the wrong number of
    * arguments, a bad flag, a bad syntax in a parameter, or whatever.
    */
  final val Usage       = new ExitCode(b"64")

  /** The input data was incorrect in some way.
    *
    * This should only be used for user's data and not system files.
    */
  final val DataErr     = new ExitCode(b"65")

  /** An input file (not a system file) did not exist or was not readable.
    *
    * This could also include errors like **No message** to a mailer (if it
    * cared to catch it).
    */
  final val NoInput     = new ExitCode(b"66")

  /** The user specified did not exist.
    *
    * This might be used for mail addresses or remote logins.
    */
  final val NoUser      = new ExitCode(b"67")

  /** The host specified did not exist.
    *
    * This is used in mail addresses or network requests.
    */
  final val NoHost      = new ExitCode(b"68")

  /** A service is unavailable.
    *
    * This can occur if a support program or file does not exist. This can
    * also be used as a catchall message when something you wanted to do does
    * not work, but you do not know why.
    */
  final val Unavailable = new ExitCode(b"69")

  /** An internal software error has been detected.
    *
    * This should be limited to non-operating system related errors as
    * possible.
    */
  final val Software    = new ExitCode(b"70")

  /** An operating system error has been detected.
    *
    * This is intended to be used for such things as **cannot fork**, **cannot
    * create pipe**, or the like. It includes things like getuid returning a
    * user that does not exist in the passwd file.
    */
  final val OSErr       = new ExitCode(b"71")

  /** Some system file does not exist, cannot be opened, or has some sort of
    * error.
    *
    * E.g., there is a syntax error in `/etc/passwd` or `/var/run/utx.active`.
    */
  final val OSFile      = new ExitCode(b"72")

  /** A (user specified) output file cannot be created. */
  final val CantCreat   = new ExitCode(b"73")

  /** An error occurred while doing I/O on some file. */
  final val IOErr       = new ExitCode(b"74")

  /** Temporary failure, indicating something that is not really an error.
    *
    * In sendmail, this means that a mailer (e.g.) could not create a
    * connection, and the request should be reattempted later.
    */
  final val TempFail    = new ExitCode(b"75")

  /** The remote system returned something that was **not possible** during a
    * protocol exchange.
    */
  final val Protocol    = new ExitCode(b"76")

  /** You did not have sufficient permission to perform the operation.
    *
    * This is not intended for file system problems, which should use
    * [[NoInput]] or [[CantCreat]], but rather for higher level permissions.
    */
  final val NoPerm      = new ExitCode(b"77")

  /** Something was found in an unconfigured or misconfigured state.  */
  final val Config      = new ExitCode(b"78")
}
