package org.gerweck.scala.util

import java.nio.charset.Charset

import org.apache.commons
import org.apache.commons.vfs2.VFS
import org.log4s._

/** Simple utility methods for dealing with files.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
object FileUtil {
  private val logger = getLogger
  private val manager = VFS.getManager

  /** The default character set for most methods */
  val utf8: Charset = Charset.forName("UTF-8")

  /** Read a file from an arbitrary URI. */
  def read(uri: String, charset: Charset = utf8): String = {
    val fo = manager.resolveFile(uri)
    try {
      val data = commons.io.IOUtils.toString(fo.getContent.getInputStream, charset)
      logger.debug(s"Read ${data.length} bytes from $fo")
      data
    } finally {
      fo.close
    }
  }

  /** Write a file to an arbitrary URI. */
  def write(uri: String, charset: Charset = utf8)(content: String): Unit = {
    val fo = manager.resolveFile(uri)
    try {
      logger.debug(s"Writing ${content.length} bytes to $fo using $charset")
      commons.io.IOUtils.write(content, fo.getContent.getOutputStream, charset)
      logger.trace("Finished writing successfully")
    } finally {
      fo.close
    }
  }
}
