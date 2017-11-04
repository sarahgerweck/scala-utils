package org.gerweck.scala.util.prefs

import java.nio.file.{ Path, Paths }
import java.io.File
import java.net.{ URI, URL }
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

  private[this] implicit val string2FileMorphism: Homomorphism[String, File] = Homomorphism[String, File](new File(_), _.getCanonicalPath)
  private[this] implicit val string2UriMorphism: Homomorphism[String, URI] = Homomorphism[String, URI](new URI(_), _.toString)
  private[this] implicit val string2PathMorphism: Homomorphism[String, Path] = Homomorphism[String, Path](Paths.get(_), _.toString)
  private[this] implicit val uri2UrlMorphism: Homomorphism[URI, URL] = Homomorphism[URI, URL](_.toURL, _.toURI)

  implicit val filePrefHandler: PrefHandler[File] = {
    mappedPrefHandler[String, File]
  }

  implicit val pathPrefHandler: PrefHandler[Path] = {
    mappedPrefHandler[String, Path]
  }

  implicit val uriPrefHandler: PrefHandler[URI] = {
    mappedPrefHandler[String, URI]
  }

  implicit val urlPrefHandler: PrefHandler[URL] = {
    implicit val string2UrlMorphism = string2UriMorphism andThen uri2UrlMorphism
    mappedPrefHandler[String, URL]
  }
}
