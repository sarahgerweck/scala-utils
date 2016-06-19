package org.gerweck.scala.util.prefs

import java.io.File
import java.util.prefs.Preferences

import org.gerweck.scala.util.mapping.Homomorphism

trait CommonPrefHandlers {
  implicit def mappedPrefHandler[A, B](implicit base: PrefHandler[A], morphism: Homomorphism[A, B]): PrefHandler[B] = new PrefHandler[B] {
    def read(path: String, default: B)(implicit p: Preferences): B = morphism.apply(base.read(path, morphism.coapply(default)))
    def write(path: String, b: B)(implicit p: Preferences): Unit = base.write(path, morphism.coapply(b))
  }

  implicit object StringPrefHandler extends PrefHandler[String] {
    def read(path: String, default: String)(implicit p: Preferences): String = p.get(path, default)
    def write(path: String, a: String)(implicit p: Preferences): Unit = p.put(path, a)
  }
  implicit object BooleanPrefHandler extends PrefHandler[Boolean] {
    def read(path: String, default: Boolean)(implicit p: Preferences): Boolean = p.getBoolean(path, default)
    def write(path: String, a: Boolean)(implicit p: Preferences): Unit = p.putBoolean(path, a)
  }
  implicit object IntPrefHandler extends PrefHandler[Int] {
    def read(path: String, default: Int)(implicit p: Preferences): Int = p.getInt(path, default)
    def write(path: String, a: Int)(implicit p: Preferences): Unit = p.putInt(path, a)
  }
  implicit object LongPrefHandler extends PrefHandler[Long] {
    def read(path: String, default: Long)(implicit p: Preferences): Long = p.getLong(path, default)
    def write(path: String, a: Long)(implicit p: Preferences): Unit = p.putLong(path, a)
  }
  implicit object FloatPrefHandler extends PrefHandler[Float] {
    def read(path: String, default: Float)(implicit p: Preferences): Float = p.getFloat(path, default)
    def write(path: String, a: Float)(implicit p: Preferences): Unit = p.putFloat(path, a)
  }
  implicit object DoublePrefHandler extends PrefHandler[Double] {
    def read(path: String, default: Double)(implicit p: Preferences): Double = p.getDouble(path, default)
    def write(path: String, a: Double)(implicit p: Preferences): Unit = p.putDouble(path, a)
  }

  implicit val filePrefHandler: PrefHandler[File] = {
    implicit val fileMorphism = Homomorphism[String, File]({ s: String => new File(s) }, { f: File => f.getCanonicalPath })
    mappedPrefHandler[String, File]
  }
}
