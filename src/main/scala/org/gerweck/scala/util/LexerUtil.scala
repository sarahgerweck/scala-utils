package org.gerweck.scala.util

import scala.annotation.tailrec

// Parsing imports
import scala.util.parsing._
import combinator._
import lexical._
import token._
import input.CharArrayReader.EofCh

object LexerUtil {
  implicit def stringToReader(s: String) = new util.parsing.input.CharSequenceReader(s)
}

trait LexerUtil extends Lexical {
  def lex(input:String): Iterable[Token] = {
    @tailrec 
    def lex(scanner: Scanner, prefix: Vector[Token]): Vector[Token] =
      if (scanner.atEnd) 
        prefix
      else 
        lex(scanner.rest, prefix :+ scanner.first) 
        
    lex(new Scanner(input), Vector.empty)
  }
  
  def str(s: String, sensitive: Boolean = false): Parser[String] = {
    def chars(s: String): Parser[Vector[Char]] = 
      if (s.length == 0) {
        success(Vector.empty)
      } else {
        val head: Char = s.head
        val headParser = 
          if (sensitive) elem(head) 
          else           elem(String.valueOf(head), {_.toLower == head.toLower}) 
        headParser ~ chars(s.tail) ^^ { case h ~ t => h +: t }
      }
    
    chars(s) ^^ { _.mkString }
  }
  
}