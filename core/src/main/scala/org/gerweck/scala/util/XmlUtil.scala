package org.gerweck.scala.util

import scala.xml._

/** Utilities and implicit enhancements for working with XML.
  *
  * In particular, this is designed to enhance Scala's built-in XML handling
  * and make it more convenient and powerful.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
object XmlUtil {
  /** Enhancements to [[scala.xml.Node]] object. */
  final implicit class RichNode(val inner: Node) extends AnyVal {
    /** Query for exactly one child node.
      * This will raise an error unless there is exactly one match.
      */
    def \^ (path: String): Node = {
      val seq = inner \ path
      require(seq.length == 1, s"Attempt to get unique node `$path` returned ${seq.length} results")
      seq.head
    }

    /** Query for the text of exactly one child node.
      * This will raise an error unless there is exactly one match.
      */
    def \! (path: String): String = (inner \^ path).text

    /** Query for one optional child node.
      * This will raise an error if there are multiple matches.
      */
    def \^? (path: String): Option[Node] = {
      val seq = inner \ path
      require(seq.length < 2, s"Got ${seq.length} nodes from search `$path`: expected zero or one")
      seq.headOption
    }

    /** Query for the text of one optional child node.
      * This will raise an error if there are multiple matches.
      */
    def \!? (path: String): Option[String] = (inner \^? path) map {_.text}
  }

  /** Enhancements to [[scala.xml.NodeSeq]] object. */
  final implicit class RichNodeSeq(val inner: NodeSeq) extends AnyVal {
    /** Query for exactly one child node.
      * This will raise an error unless there is exactly one match.
      */
    def \^ (path: String): Node = {
      val seq = inner \ path
      require(seq.length == 1, s"Attempt to get unique node `$path` returned ${seq.length} results")
      seq.head
    }

    /** Query for the text of exactly one child node.
      * This will raise an error unless there is exactly one match.
      */
    def \! (path: String): String = (inner \^ path).text

    /** Query for one optional child node.
      * This will raise an error if there are multiple matches.
      */
    def \^? (path: String): Option[Node] = {
      val seq = inner \ path
      require(seq.length < 2, s"Got ${seq.length} nodes from search `$path`: expected zero or one")
      seq.headOption
    }

    /** Query for the text of one optional child node.
      * This will raise an error if there are multiple matches.
      */
    def \!? (path: String): Option[String] = (inner \^? path) map {_.text}
  }
}
