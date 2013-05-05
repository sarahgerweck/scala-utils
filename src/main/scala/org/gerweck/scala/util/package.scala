package org.gerweck.scala

/** Miscellaneous utility code for manipulating standard objects.
  *  
  * This package is designed to be maximally convenient if you `import org.gerweck.scala.util._`.
  */ 
package object util {
  implicit final class CharUtils(val self: Char) extends AnyVal {

    /** Case-insensitive equals.
      *  
      * @note `~` is the command-mode vim keystroke to flip the case of a 
      * character, and the defined operator looks similar to the mathematical 
      * congruence operator (&cong;). These offer useful mnemonics.
      */
    @inline def =~= (that: Char): Boolean = {
      /* 
       * String's `equalsIgnoreCase` is safer than something like `toLower`. The 
       * latter is not always safe when dealing with international characters. 
       * (Passing in a specific locale is even better, but String provides good 
       * generalized behavior.)
       */
      String.valueOf(self) =~= String.valueOf(that) 
    }
  }

  /** Utility functionality for working with strings.   
    * 
    */
  implicit final class StringUtils(val self: String) extends AnyVal {
    /** Case-insensitive equals.
      *  
      * @note `~` is the command-mode vim keystroke to flip the case of a 
      * character, and the defined operator looks similar to the mathematical 
      * congruence operator (&cong;). These offer useful mnemonics.
      */
    @inline def =~= (that: String): Boolean = self equalsIgnoreCase that
  }
}
