package org.gerweck.scala.util

import scala.collection.JavaConverters._

import java.io.File
import java.net.URL
import java.util.jar.JarFile

import org.log4s._

/** A utility for working with resources bundled into your application.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
object Resources {
  private[this] val logger = getLogger

  private def processDirectory(directory: File, pkgname: String): Map[String, URL] = {
    logger.trace("Reading Directory '" + directory + "'")
    ( for {
        fileName <- directory.list
      } yield {
        fileName -> new File(directory, fileName).toURI.toURL
      }
    ).toMap
  }

  private def processJarfile(resource: URL, pkgname: String): Map[String, URL] = {
    val relPath = pkgname.replace('.', '/')
    val resPath = resource.getPath
    val jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "")
    logger.debug("Reading JAR file: '" + jarPath + "'")
    var jarFile: JarFile = null
    jarFile = new JarFile(jarPath)
    ( for {
        entry <- jarFile.entries.asScala
        name = entry.getName
        if (name startsWith relPath) && !(name.drop(relPath.length).dropWhile(_ == '/') contains '/')
      } yield {
        name -> getClass.getResource(s"$relPath/$name")
      }
    ).toMap
  }

  /** Get all the resources within a given package.
    *
    * This is useful if you have a bunch of static resources compiled into your Jar and you need
    * to get a list of them. (The JVM doesn't have this capability built in.)
    */
  def forPackage(pkg: Package): Map[String, URL] = {
    val pkgname = pkg.getName
    val relPath = pkgname.replace('.', '/')

    val resources = getClass.getClassLoader.getResources(relPath).asScala.toVector
    logger.trace(s"Got resources: $resources")

    val maps = {
      for {
        resource <- resources
      } yield {
        logger.trace("Package: '" + pkgname + "' becomes Resource: '" + resource.toString + "'")
        resource.getPath
        if (resource.toString.startsWith("jar:")) {
          processJarfile(resource, pkgname)
        } else {
          processDirectory(new File(resource.getPath), pkgname)
        }
      }
    }

    (Map.empty[String,URL] /: maps) (_ ++ _)
  }

  def forPackage(name: String): Map[String, URL] = forPackage(Package.getPackage(name))
}
