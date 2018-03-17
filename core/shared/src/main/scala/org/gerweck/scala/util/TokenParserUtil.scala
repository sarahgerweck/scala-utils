package org.gerweck.scala.util

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters._
import scala.reflect.{ ClassTag, classTag }
import scala.util.parsing.combinator.syntactical.TokenParsers

/** Mix-in utility code to assist with Parser development.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
trait TokenParserUtil extends TokenParsers with ParserUtil {

  private[this] val typeParsers = new ConcurrentHashMap[ClassTag[_ <: Elem], Parser[Elem]].asScala

  /** A parser that matches elements of a given type.  This is useful if you have
    * a hierarchy of traits that extend from `Elem` and want to easily be able to
    * match certain classes of tokens.
    */
  final protected[this] def tpe[A <: Elem : ClassTag]: Parser[A] = {
    val ct = classTag[A]
    val p = typeParsers getOrElseUpdate (ct, {
      val clzz = ct.runtimeClass
      elem(clzz.getSimpleName, { clzz isInstance _ })
    })
    p.asInstanceOf[Parser[A]]
  }
}
