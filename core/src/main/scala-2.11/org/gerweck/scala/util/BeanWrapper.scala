package org.gerweck.scala.util

import scala.collection.mutable
import scala.reflect.runtime.{universe => ru}

import org.log4s._

/** A wrapper around JavaBeans that allows one to get and set properties.
  *
  * This uses reflection, so it will not perform as quickly as setting those
  * properties statically.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
class BeanWrapper(val inner: Any) {
  import BeanWrapper._

  private[this] lazy val mirror = ru.runtimeMirror(inner.getClass.getClassLoader)
  private[this] lazy val tpe = mirror.classSymbol(inner.getClass).toType
  private[this] lazy val methods: mutable.Map[String, ru.MethodMirror] = mutable.WeakHashMap.empty

  def update(property: String, value: Any) = {
    val name = "set" + property.capitalize
    logger.trace(s"Setting bean property $name")

    getMethod(name)(value)
  }

  def apply(property: String): Any = {
    val name = "get" + property.capitalize
    logger.trace(s"Getting bean property $name")

    getMethod(name)()
  }

  private[this] def getMethod(name: String) = methods.getOrElseUpdate(name, constructMirror(name))

  private[this] def constructMirror(name: String) = {
    val termName = ru.TermName(name)
    val decl =
      try {
        tpe.member(termName).asMethod
      } catch {
        case e: Exception => throw new RuntimeException(s"No method on ${inner.getClass} named $name", e)
      }
    val im = mirror.reflect(inner)

    val meth = im.reflectMethod(decl)
    logger.trace(s"Reflected method: $meth")

    meth
  }
}

object BeanWrapper {
  private val logger = getLogger
}
