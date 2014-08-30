package org.gerweck.scala.util

import language.experimental.macros

import scala.reflect.macros.Context

import scala.math.Ordering


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
      import Ordering._
      val list = (modules map { m => Ident(m.name) }).toList
      if (sorted)
        list sorted (Ordering[String] on { x:Ident => x.name.encodedName.toString })
      else
        list
    }

    /* This is the AST for Set(a, b, c).  Select the Set.apply function, then apply it to our list of parameters */
    c.Expr[Set[A]](Apply(setBuilder, modList))
  }
}
