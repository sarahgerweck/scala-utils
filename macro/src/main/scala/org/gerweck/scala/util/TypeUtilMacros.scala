package org.gerweck.scala.util

import scala.reflect.macros.blackbox.Context

/** Module containing macro implementations */
private[util] object TypeUtilMacros {
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
    if (!symbol.isSealed) {
      c.error(c.enclosingPosition, s"Type $parentType is not a sealed type, cannot get its case objects")
    }

    def descendants(s: ClassSymbol): Set[Symbol] =
      s.knownDirectSubclasses flatMap { sub =>
        if (sub.isClass) {
          descendants(sub.asClass) + sub
        } else {
          Set(sub)
        }
      }

    val modules =
      for {
        desc <- descendants(symbol)
        if desc.isModuleClass
      } yield desc.asClass.module

    if (emitInfo) {
      c.info(c.enclosingPosition, s"Found modules $modules for sealed type $parentType", false)
    }

    /* The list of modules we are going to pass into the set builder */
    val modList: List[Ident] = {
      val list = modules.map(m => Ident(m.name)).toList
      if (sorted) {
        list.sortBy(_.name.encodedName.toString)
      } else {
        list
      }
    }

    c.Expr[Set[A]](q"_root_.scala.Predef.Set[..${List(parentType)}](..$modList)")
  }
}
