package org.gerweck.scala

/** Miscellaneous utility code for manipulating standard objects.
  *  
  * This package is designed to be maximally convenient if you `import org.gerweck.scala.util._`.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */ 
package object util {
  
  /** Utility functionality for working with characters.   
   * 
   * @author Sarah Gerweck <sarah.a180@gmail.com>
   */
  implicit final class CharUtils(val self: Char) extends AnyVal {
    
    /** Convert a character to a random case.  It will have a 50% probability of
      * being uppercase and a 50% probability of being lowercase.
      */
    def toRandomCase: Char = if (scala.util.Random.nextBoolean) self.toUpper else self.toLower

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
    * @author Sarah Gerweck <sarah.a180@gmail.com>
    */
  implicit final class StringUtils(val self: String) extends AnyVal {
    
    /** Convert a string to random case.  Each character will have a 50%
      * probability of being uppercase and a 50% probability of being lowercase. 
      */
    def toRandomCase: String = self map { _.toRandomCase }
    
    /** Case-insensitive equals.
      *  
      * @note `~` is the command-mode vim keystroke to flip the case of a 
      * character, and the defined operator looks similar to the mathematical 
      * congruence operator (&cong;). These offer useful mnemonics.
      */
    @inline def =~= (that: String): Boolean = 
      if (self eq that) true 
      else self equalsIgnoreCase that
  }
}
