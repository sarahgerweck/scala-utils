package org.gerweck.scala.util.prefs

import java.nio.file.{ Path, Paths }
import java.io.File
import java.net.{ URI, URL }
import java.util.prefs.Preferences

import org.gerweck.scala.util.mapping.Homomorphism

trait CommonPrefHandlers {
  implicit def mappedPrefHandler[A, B](implicit base: PrefHandler[A], morphism: Homomorphism[A, B]): PrefHandler[B] = new PrefHandler[B] {
    def read(path: String)(implicit p: Preferences): Option[B] = base.read(path).map(morphism.apply)
    def write(path: String, b: B)(implicit p: Preferences): Unit = base.write(path, morphism.coapply(b))
  }

  trait NonnullPrefHandler[A] extends PrefHandler[A] {
    def read(path: String)(implicit p: Preferences): Option[A] = {
      Option(p.get(path, None.orNull))
        .map(s => doRead(path, s))
    }
    protected[this] def doRead(path: String, stringValue: String)(implicit p: Preferences): A
  }

  implicit object StringPrefHandler extends PrefHandler[String] {
    override def read(path: String)(implicit p: Preferences): Option[String] = Option(p.get(path, None.orNull))
    override def write(path: String, a: String)(implicit p: Preferences): Unit = p.put(path, a)
    override def readWithDefault(path: String, default: String)(implicit p: Preferences) = p.get(path, default)
  }
  implicit object BooleanPrefHandler extends NonnullPrefHandler[Boolean] {
    override def doRead(path: String, s: String)(implicit p: Preferences): Boolean = s.toBoolean
    override def readWithDefault(path: String, default: Boolean)(implicit p: Preferences): Boolean = p.getBoolean(path, default)
    override def write(path: String, a: Boolean)(implicit p: Preferences): Unit = p.putBoolean(path, a)
  }
  implicit object IntPrefHandler extends NonnullPrefHandler[Int] {
    override def doRead(path: String, s: String)(implicit p: Preferences): Int = s.toInt
    override def readWithDefault(path: String, default: Int)(implicit p: Preferences): Int = p.getInt(path, default)
    override def write(path: String, a: Int)(implicit p: Preferences): Unit = p.putInt(path, a)
  }
  implicit object LongPrefHandler extends NonnullPrefHandler[Long] {
    override def doRead(path: String, s: String)(implicit p: Preferences): Long = s.toLong
    override def readWithDefault(path: String, default: Long)(implicit p: Preferences): Long = p.getLong(path, default)
    override def write(path: String, a: Long)(implicit p: Preferences): Unit = p.putLong(path, a)
  }
  implicit object FloatPrefHandler extends NonnullPrefHandler[Float] {
    override def doRead(path: String, s: String)(implicit p: Preferences): Float = s.toFloat
    override def readWithDefault(path: String, default: Float)(implicit p: Preferences): Float = p.getFloat(path, default)
    override def write(path: String, a: Float)(implicit p: Preferences): Unit = p.putFloat(path, a)
  }
  implicit object DoublePrefHandler extends NonnullPrefHandler[Double] {
    override def doRead(path: String, s: String)(implicit p: Preferences): Double = s.toDouble
    override def readWithDefault(path: String, default: Double)(implicit p: Preferences): Double = p.getDouble(path, default)
    override def write(path: String, a: Double)(implicit p: Preferences): Unit = p.putDouble(path, a)
  }
  implicit object ByteArrayPrefHandler extends PrefHandler[Array[Byte]] {
    override def read(path: String)(implicit p: Preferences): Option[Array[Byte]] = Option(p.getByteArray(path, None.orNull))
    override def write(path: String, a: Array[Byte])(implicit p: Preferences): Unit = p.putByteArray(path, a)
    override def readWithDefault(path: String, default: Array[Byte])(implicit p: Preferences) = p.getByteArray(path, default)
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

  implicit val byteSeqHandler: PrefHandler[Seq[Byte]] = {
    implicit val ba2bsm: Homomorphism[Array[Byte], Seq[Byte]] = Homomorphism(_.toSeq, _.toArray)
    mappedPrefHandler[Array[Byte], Seq[Byte]]
  }
}
