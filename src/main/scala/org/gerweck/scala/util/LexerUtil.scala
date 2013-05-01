package org.gerweck.scala.util

import scala.annotation.tailrec

// Parsing imports
import scala.util.parsing._
import combinator._
import lexical._
import token._
import input.CharArrayReader.EofCh


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

  def string(s: String, sensitive: Boolean = false): Parser[List[Char]] = {
    if (s.length == 0) {
      success(List.empty)
    } else {
      val head: Char = s.head
      val headParser = 
        if (sensitive) elem(head) 
        else           elem(head.toString, {_.toLower == head.toLower}) 
      headParser ~ string(s.tail, sensitive) ^^ { case h ~ t => h :: t}
    }
  } 
}