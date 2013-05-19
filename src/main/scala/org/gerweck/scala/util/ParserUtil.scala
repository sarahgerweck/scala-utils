package org.gerweck.scala.util

import scala.util.parsing.combinator.Parsers

trait ParserUtil extends Parsers {

  def cntsep[T](p: Int => Parser[T], q: => Parser[Any]): Parser[Seq[T]] = {
    cnt1sep(p, q) | success(Seq.empty[T])
  }
  
  def cnt1sep[T](p: Int => Parser[T], q: => Parser[Any]): Parser[Seq[T]] = {
    lazy val q0 = q
    def tail(count: Int): Parser[Seq[T]] =
      ( q0 ~> p(count) ~ tail(count + 1) ^^ { case a ~ b => a +: b }
      | success(List.empty[T])
      )
    
    p(0) ~ tail(1) ^^ { case a ~ b => a +: b }
  }
}