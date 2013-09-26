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
    * This will actually return ''all'' descendant objects that descend from the sealed class,
    * whether or not they were marked `case object` or not.
    *
    * @note Be careful about calling this on inner classes.  If you have a sealed class hierarchy
    * inside a trait, there will be no known objects if this is used inside the trait itself.
    * It ''will'' work in classes that descend from that trait.
    *
    * @tparam A a sealed class or interface
    * @return all objects that are known to derive from the given type
    */
  def getCaseObjects[A]: Set[A] = macro getCaseObjects_impl[A]

}

/** Module containing macro implementations */
private object TypeUtilMacros {
  /** The module to use for constructing sets */
  private[this] final val setModule = "scala.collection.immutable.Set"

  /** Whether to emit info messages for type macros
    *
    * Info messages are controlled by the `-verbose` flag on the Scala compiler.
    */
  private[this] final val emitInfo = false

  /** Whether to sort sets of objects by name */
  private[this] final val sorted = true

  def getCaseObjects_impl[A: c.WeakTypeTag](c: Context): c.Expr[Set[A]] = {
    import c.universe._

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

    if (emitInfo)
      c.info(c.enclosingPosition, s"Found modules $modules for sealed type $parentType", false)

    /* A function selector that will build a set of its parameters */
    val setBuilder = Select(Ident(rootMirror.staticModule(setModule)), "apply":TermName)

    /* The list of modules we are going to pass into the set builder */
    val modList = {
      import math.Ordering._
      val list = (modules map { m => Ident(m.name) }).toList
      if (sorted)
        list sorted (Ordering[String] on { x:Ident => x.name.encoded })
      else
        list
    }

    /* This is the AST for Set(a, b, c).  Select the Set.apply function, then apply it to our list of parameters */
    c.Expr[Set[A]](Apply(setBuilder, modList))
  }

}
