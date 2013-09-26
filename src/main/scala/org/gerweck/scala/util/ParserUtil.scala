package org.gerweck.scala.util

import scala.util.parsing.combinator.Parsers

trait ParserUtil extends Parsers {

  def cntsep[T](p: Int => Parser[T], q: => Parser[Any]): Parser[Seq[T]] =
    smartsep( { case n => (false, p(n)) }, q)

  def cnt1sep[T](p: Int => Parser[T], q: => Parser[Any]): Parser[Seq[T]] =
    smartsep( { case 0 => (true, p(0))
                case n => (false, p(n))
              }
            , q)

  def smartsep[T](p: PartialFunction[Int,(Boolean,Parser[T])], q: => Parser[Any]): Parser[Seq[T]] = {
    lazy val q0 = q
    def tail(count: Int): Parser[Seq[T]] = {
      if (p isDefinedAt count) {
        val (required, parser) = p(count)
        ( (  (if (count > 0) q0 else success(()))
          ~> parser ~ tail(count + 1)
          ^^ { case a ~ b => a +: b }
          )
        | success(List.empty[T])
        )
      } else success(List.empty[T])
    }

    tail(0)
  }
}
