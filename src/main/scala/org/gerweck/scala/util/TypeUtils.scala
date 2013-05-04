package org.gerweck.scala.util

import language.experimental.macros

import scala.reflect.macros.Context

/** Utilities for working with types
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>  
  */
object TypeUtils {
  import TypeUtilMacros._
  
  /** Get all the known objects that derive from a given sealed class.
    * 
    * @note Be careful about calling this on inner classes.  If you have a sealed class hierarchy 
    * inside a trait, there will be no known objects if this is used inside the trait itself. 
    * It *will* work in classes that descend from that trait.
    * 
    * @tparam A a sealed class or interface
    * @return all objects that are known to derive from the given type
    */
  def getCaseObjects[A]: Set[A] = macro getCaseObjects_impl[A]

}

private object TypeUtilMacros {
  def getCaseObjects_impl[A: c.WeakTypeTag](c: Context): c.Expr[Set[A]] = {
    import c.universe._
    
    @inline def warn(s: Any) = c.warning(c.enclosingPosition, s.toString)
    
    val parentType = weakTypeOf[A]
    val symbol = weakTypeOf[A].typeSymbol.asClass
    if (!symbol.isSealed) 
      c.error(c.enclosingPosition, s"Type $parentType is not a sealed type, cannot get its case objects") 
      
    def descendants(s: ClassSymbol): Set[Symbol] = 
      s.knownDirectSubclasses flatMap { sub =>
        if (sub.isClass) descendants(sub.asClass) + sub 
        else Set(sub)
      }
      
    val modules = 
      for {
        desc <- descendants(symbol)
        if desc.isModuleClass
      } yield desc.asClass.module
    
    // c.warning(c.enclosingPosition, s"Found modules $modules")

    /* This is the AST for Set(a, b, c).  Select the Set.apply function, then apply it to our list of parameters */
    c.Expr[Set[A]](Apply(Select(Ident(rootMirror.staticModule("scala.collection.immutable.Set")), 
                                newTermName("apply")), 
                         (modules map { mod => Ident(newTermName(mod.name.encoded)) }).toList))
  }
  
}