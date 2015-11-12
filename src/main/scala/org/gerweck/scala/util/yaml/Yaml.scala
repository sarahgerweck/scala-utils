package org.gerweck.scala.util.yaml

import scala.language.implicitConversions

/** A simple DSL for generating YAML documents.
  *
  * The entry point is to call `Yaml.Write`, which takes a map-like
  * syntax. To create nested arrays or maps, use the `Yaml.Map` or `Yaml.Seq`
  * generators.
  *
  * None of the other types in the package should be used directly.
  *
  * Example:
  * {{{
  * Yaml.Write(
  *   "a" -> 1,
  *   "b" -> Yaml.Map(
  *     "b" -> Yaml.Map(
  *       "c" -> Yaml.Seq(1, 2, false)
  *     )
  *   )
  * )
  * }}}
  */
object Yaml {
  import YamlImpl._

  val Write: Writer = Writer
  def Map(bits: YamlRecord*): YamlMapValue = bits
  def Seq(bits: SimpleYamlValue*): YamlSeqValue = bits

  private[this] object Writer extends Writer {
    protected[this] def withOptionalHeader(header: Option[String])(bits: YamlRecord*) = {
      val sb = new StringBuilder
      header foreach { h =>
        sb ++= h
        sb += '\n'
        sb ++= "---\n"
      }
      bits foreach { sb ++= _.output(0) }
      sb.toString
    }
    def withHeader(header: String)(bits: YamlRecord*) = withOptionalHeader(Some(header))(bits: _*)
    def apply(bits: YamlRecord*) = withOptionalHeader(None)(bits: _*)
  }
}

object YamlImpl {
  private[this] final val indentSize = 4

  sealed trait Writer {
    def withHeader(header: String)(bits: YamlRecord*): String
    def apply(bits: YamlRecord*): String
  }

  case class YamlRecord(key: String, value: YamlValue) {
    private[this] def safeKeyChar(c: Char) = {
      c match {
        case '-' | '_'                => true
        case ld if ld.isLetterOrDigit => true
        case _                        => false
      }
    }
    def output(implicit currentIndent: Int): String = {
      val valueOutput = value.output
      val space = if (valueOutput startsWith "\n") "" else " "
      val cr = if (valueOutput startsWith "\n") "" else "\n"
      val keyOutput = if (key forall safeKeyChar) key else doQuoting(key)
      " " * currentIndent + s"$keyOutput:$space$valueOutput$cr"
    }
  }
  object YamlRecord {
    implicit def pairToYamlRecord[A](input: (String, A))(implicit ev1: A => YamlValue) = {
      val (k, v) = input
      YamlRecord(k, ev1(v))
    }
  }

  trait YamlValue {
    def output(implicit currentIndent: Int): String
  }
  sealed trait SimpleYamlValue extends YamlValue
  implicit final class StringYamlValue(val data: String) extends SimpleYamlValue {
    def output(implicit currentIndent: Int) = {
      def unsafeChar(c: Char) = {
        c match {
          case '"' => true
          case '\'' => true
          case '\t' => true
          case '\n' => true
          case '\\' => true
          case ' ' => false
          case a if a.isLetterOrDigit => false
          case a if a.isControl => true
          case a if a.isValidByte => false
          case _ => true
        }
      }
      if (data.size == 0) {
        ""
      } else if (data.head.isSpaceChar || data.last.isSpaceChar || data.exists(unsafeChar)) {
        doQuoting(data)
      } else {
        data
      }
    }
  }
  implicit final class NumberYamlValue[A](val number: A)(implicit ev1: A => spire.math.Number) extends SimpleYamlValue {
    def output(implicit currentIndent: Int) = ev1(number).toString
  }
  implicit final class BooleanYamlValue(val data: Boolean) extends SimpleYamlValue {
    def output(implicit currentIndent: Int) = data.toString
  }
  implicit final class YamlSeqValue(val data: Seq[SimpleYamlValue]) extends YamlValue {
    def output(implicit currentIndent: Int) = data.map(_.output(nextIndent)).mkString("[", ", ", "]")
  }
  implicit final class YamlMapValue(val data: Seq[YamlRecord]) extends YamlValue {
    def output(implicit currentIndent: Int) = data.map(_.output(nextIndent)).mkString("\n", "", "")
  }

  @inline private[this] def nextIndent(implicit currentIndent: Int) = currentIndent + indentSize

  protected def doQuoting(str: String) = {
    val replaced =
      str flatMap { c =>
        c match {
          case '\\'     => "\\\\"
          case '\"'     => "\\\""
          case '\n'     => "\\n"
          case '\t'     => "\\t"
          case '\u0000' => "\\0"
          case '\u0007' => "\\a"
          case '\u0008' => "\\b"
          case '\u000B' => "\\v"
          case '\u000C' => "\\f"
          case '\r'     => "\\r"
          case '\u001B' => "\\e"
          case '\u0085' => "\\N"
          case '\u00A0' => "\\_"
          case '\u2028' => "\\L"
          case '\u2029' => "\\P"
          case lctl if lctl.isControl && lctl <= 128 => f"\\x$lctl%02X"
          case ctl if ctl.isControl || ctl > 128 && !ctl.isLetterOrDigit => f"\\u$ctl%04X"
          case other => other.toString
        }
      }
    "\"" + replaced + "\""
  }
}
