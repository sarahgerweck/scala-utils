package org.gerweck.scala.util.io

import java.nio.file.StandardOpenOption

/** Describes what to do if a target path already exists. */
sealed trait ExistingFile {
  val openOpts: Seq[StandardOpenOption]
}

/** Contains options for handling existing files. */
object ExistingFile {

  /** If the file exists, empty and overwrite it. */
  case object Overwrite extends ExistingFile {
    override final val openOpts =
      Seq(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  }

  /** If the file exists, fail the operation. */
  case object Fail extends ExistingFile {
    override final val openOpts =
      Seq(StandardOpenOption.CREATE_NEW)
  }
}
