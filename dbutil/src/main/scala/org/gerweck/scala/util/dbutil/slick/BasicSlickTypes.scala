package org.gerweck.scala.util.dbutil.slick

import java.io.File
import java.nio.file.Path
import java.sql.Timestamp
import java.time.Instant

import org.gerweck.scala.util.ProcessStream

/** Basic Slick type mappings.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
trait BasicSlickTypes {
  protected val profile: SProfile
  import profile.api._

  implicit lazy val instantType = MappedColumnType.base[Instant, Timestamp](Timestamp.from, _.toInstant)

  implicit lazy val fileType = MappedColumnType.base[File, String](_.getPath, new File(_))

  implicit lazy val pathType = MappedColumnType.base[Path, File](_.toFile, _.toPath)

  implicit lazy val processStreamType = MappedColumnType.base[ProcessStream, Int](_.number, ProcessStream.fromNumber _)
}
