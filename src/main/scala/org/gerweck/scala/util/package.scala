package org.gerweck.scala

package object util {
  implicit final class CharUtils(val c: Char) extends AnyVal {
    /** Case-insensitive equals */
    @inline def =~= (d: Char): Boolean = c.toLower == d.toLower
  }
  
  implicit final class StringUtils(val s: String) extends AnyVal {
    /** Case-insensitive equals */
    @inline def =~= (t: String): Boolean = s equalsIgnoreCase t
  }
}