package org.gerweck.scala.util

import scala.reflect.runtime.universe._

import org.log4s._

/** A pretty-printer that renders an object tree.
  *
  * This is meant for debugging purposes. Inspired by code from the
  * [[https://github.com/nikita-volkov/sext sext project]].
  *
  * '''This is experimental and the output format may change at any time.'''
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
object ObjectTree {
  private[this] final val logger = getLogger

  private[this] final val nestingLimitString = "⟨∅⟩"
  private[this] final val defaultDepthLimit = 64
  private[this] final val defaultSingleLineLimit = 100

  private[this] lazy val currentMirror = runtimeMirror(this.getClass.getClassLoader)

  def apply(any: Any, indent: Int = 2, skipNones: Boolean = false, maxDepth: Int = defaultDepthLimit, singleLineSizeLimit: Int = defaultSingleLineLimit) = {
    var hitDepthLimit: Boolean = false
    def ind(s: String) = s.linesWithSeparators.map(" " * indent + _).mkString
    def singleLine(s: String) = {
      s.size < singleLineSizeLimit && s.linesIterator.drop(1).isEmpty
    }
    def smartShow(any: Any, currentDepth: Int): String = {
      if (currentDepth > maxDepth) {
        if (!hitDepthLimit) {
          logger.warn(s"Object tree exceeded depth limit of $maxDepth, showing further nested values as $nestingLimitString")
          hitDepthLimit = true
        }
        return nestingLimitString
      }
      def depth = currentDepth + 1
      def showKey(k: Any) = smartShow(k, depth)
      def showKV(shownKey: String, shownValue: String, indentValue: Boolean) = {
        def doIndent(v: String) = if (indentValue) ind(v) else v
        val sep = if (singleLine(shownValue)) ":" else ":\n"
        if (singleLine(shownValue)) {
          s"$shownKey: ${shownValue.dropWhile(_.isSpaceChar)}"
        } else {
          s"$shownKey:\n${doIndent(shownValue)}"
        }
      }
      any match {
        case (k, v) =>
          val shownKey = showKey(k)
          val shownValue = smartShow(v, depth)
          ind(showKV(showKey(k), smartShow(v, depth), true))
        case a: TraversableOnce[_] =>
          a.toIterator
            .map(smartShow(_, depth))
            .mkString("\n")
        case p: Product if p.productArity == 1 && singleLine(smartShow(p.productElement(0), depth)) =>
          p.toString
        case a: Product if a.productArity > 0 =>
          val aMirror = currentMirror.reflect(a)
          val fieldStrings =
            aMirror.symbol.typeSignature.members.toStream
              .collect { case a: TermSymbol => a }
              .filter(a => a.isParamAccessor)
              .filterNot(a => a.isMethod || a.isModule)
              .map(aMirror.reflectField)
              .filterNot(skipNones && _.get == None)
              .map(f => f.symbol.name.toString.trim -> f.get)
              .reverse
          val shownFields = smartShow(Seq(fieldStrings: _*), depth)
          showKV(a.productPrefix + ':', shownFields, false)
        case null =>
          "null"
        case other =>
          other.toString
      }
    }
    smartShow(any, 0)
  }
}
