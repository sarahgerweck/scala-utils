package org.gerweck.scala.util

import language.implicitConversions
import language.reflectiveCalls

import scala.annotation.tailrec
import scala.collection.JavaConverters._

import java.util.concurrent.ConcurrentHashMap

// Parsing imports
import scala.util.parsing._
import combinator._
import lexical._

import org.log4s._

object LexerUtil {
  implicit def stringToReader(s: String) = new util.parsing.input.CharSequenceReader(s)
}

trait LexerUtil extends Scanners with ParserUtil {
  protected[this] val logger: Logger = getLogger("org.gerweck.scala.util.LexerUtil")
  def lex(input:String): Iterable[Token] = timed (logger = logger, taskName = "lexing", level = Debug) {
    @tailrec
    def lex(scanner: Scanner, prefix: Vector[Token]): Vector[Token] =
      if (scanner.atEnd)
        prefix
      else
        lex(scanner.rest, prefix :+ scanner.first)

    lex(new Scanner(input), Vector.empty)
  }

  private[this] val charParsers = new ConcurrentHashMap[(String,Boolean),Parser[Vector[Char]]].asScala
  private[this] val strParsers = new ConcurrentHashMap[(String,Boolean),Parser[String]].asScala

  def insensitive(c: Char) = elem(c.toString, { _ =~= c })

  // TBD: This might perform better as a macro
  final def chars(s: String, sensitive: Boolean = false): Parser[Vector[Char]] = charParsers.getOrElseUpdate((s, sensitive), {
    (s :\ success(Vector.empty[Char])) { (head, tailParser) =>
      @inline def headParser =
        if (sensitive) elem(head)
        else           insensitive(head)

      headParser ~ tailParser ^^ { case h ~ t => h +: t }
    }
  })

  // TBD: This might perform better as a macro
  final def str(s: String, sensitive: Boolean = false): Parser[String] = strParsers.getOrElseUpdate((s, sensitive), {
    chars(s, sensitive) ^^ { _.mkString }
  })

  final def longest[A <: { def chars: String }](constants: Iterable[A]): Parser[A] = {
    longest(constants, { a: A => a.chars })
  }

  final def longest[A <: { def chars: String }](constants: Iterable[A], sensitive: Boolean): Parser[A] = {
    longest(constants, { a: A => a.chars }, sensitive)
  }

  final def longest[A](constants: Iterable[A], chars: A => String, sensitive: Boolean = false): Parser[A] = {
    val sorted = constants.toIndexedSeq sortWith { chars(_).length > chars(_).length }
    implicit def toParser(a: A) = str(chars(a), sensitive) ^^^ a
    (toParser(sorted.head) /: sorted.tail) { _ | _ }
  }

  final def longestString[A](constants: Iterable[String], sensitive: Boolean = false): Parser[String] = {
    longest(constants, identity[String], sensitive)
  }
}
