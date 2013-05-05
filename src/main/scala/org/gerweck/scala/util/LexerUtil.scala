package org.gerweck.scala.util

import language.implicitConversions

import scala.annotation.tailrec

// Parsing imports
import scala.util.parsing._
import combinator._
import lexical._
import token._
import input.CharArrayReader.EofCh

import org.log4s._

object LexerUtil {
  implicit def stringToReader(s: String) = new util.parsing.input.CharSequenceReader(s)
}

trait LexerUtil extends Lexical {
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
  
  // TBD: This might perform better as a macro 
  @inline final def chars(s: String, sensitive: Boolean = false): Parser[Vector[Char]] = 
    (s :\ success(Vector.empty[Char])) { (head, tailParser) =>
      @inline def headParser = 
        if (sensitive) elem(head)
        else           elem(String.valueOf(head), { _ =~= head })
        
      headParser ~ tailParser ^^ { case h ~ t => h +: t } 
    }
  
  // TBD: This might perform better as a macro 
  final def str(s: String, sensitive: Boolean = false): Parser[String] = chars(s, sensitive) ^^ { _.mkString }
     
}