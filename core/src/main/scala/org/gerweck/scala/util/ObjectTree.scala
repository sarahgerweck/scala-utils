package org.gerweck.scala.util

import scala.reflect.runtime.universe._

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
  private[this] lazy val currentMirror = runtimeMirror(this.getClass.getClassLoader)
  def apply(any: Any, indent: Int = 2, skipNones: Boolean = false) = {
    def ind(s: String) = s.linesWithSeparators.map(" " * indent + _).mkString
    def singleLine(s: String) = s.lines.drop(1).isEmpty
    def smartShow(any: Any): String = {
      def showKey(k: Any) = smartShow(k)
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
          val shownValue = smartShow(v)
          ind(showKV(showKey(k), smartShow(v), true))
        case a: TraversableOnce[_] =>
          a.toIterator
            .map(smartShow(_))
            .mkString("\n")
        case p: Product if p.productArity == 1 && singleLine(smartShow(p.productElement(0))) =>
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
          val shownFields = smartShow(Seq(fieldStrings: _*))
          showKV(a.productPrefix + ':', shownFields, false)
        case null =>
          "null"
        case other =>
          other.toString
      }
    }
    smartShow(any)
  }
}
