import sbt._
import Keys._

import scala.util.Properties.envOrNone

import com.typesafe.sbt.site._

import Helpers._

sealed trait Basics {
  final val buildOrganization  = "org.gerweck.scala"
  final val buildOrganizationName = "Sarah Gerweck"
  final val buildOrganizationUrl  = Some("https://github.com/sarahgerweck")

  final val buildScalaVersion  = "2.11.8"
  final val extraScalaVersions = Seq("2.12.1")
  final val minimumJavaVersion = "1.6"
  lazy  val defaultOptimize    = true

  lazy  val parallelBuild      = false
  lazy  val cachedResolution   = false

  /* Metadata definitions */
  lazy val buildMetadata = Vector(
    licenses    := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    homepage    := Some(url("https://github.com/sarahgerweck/scala-utils")),
    description := "Miscellaneous utility functionality for Scala",
    startYear   := Some(2012),
    scmInfo     := Some(ScmInfo(url("https://github.com/sarahgerweck/scala-utils"), "scm:git:git@github.com:sarahgerweck/scala-utils.git"))
  )

  lazy val developerInfo = {
    <developers>
      <developer>
        <id>sarah</id>
        <name>Sarah Gerweck</name>
        <email>sarah.a180@gmail.com</email>
        <url>https://github.com/sarahgerweck</url>
        <timezone>America/Los_Angeles</timezone>
      </developer>
    </developers>
  }
}

object BuildSettings extends Basics {
  /* Overridable flags */
  lazy val optimize     = boolFlag("OPTIMIZE") orElse boolFlag("OPTIMISE") getOrElse defaultOptimize
  lazy val deprecation  = boolFlag("NO_DEPRECATION") map (!_) getOrElse true
  lazy val inlineWarn   = boolFlag("INLINE_WARNINGS") getOrElse false
  lazy val debug        = boolFlag("DEBUGGER") getOrElse false
  lazy val debugPort    = envOrNone("DEBUGGER_PORT") map { _.toInt } getOrElse 5050
  lazy val debugSuspend = boolFlag("DEBUGGER_SUSPEND") getOrElse true
  lazy val unusedWarn   = boolFlag("UNUSED_WARNINGS") getOrElse false
  lazy val importWarn   = boolFlag("IMPORT_WARNINGS") getOrElse false
  lazy val java8Flag    = boolFlag("BUILD_JAVA_8") getOrElse false

  val buildScalaVersions = buildScalaVersion +: extraScalaVersions

  private[this] val sharedScalacOptions = Seq (
    "-unchecked",
    "-feature"
  ) ++ (
    if (deprecation) Seq("-deprecation") else Seq.empty
  ) ++ (
    if (inlineWarn) Seq("-Yinline-warnings") else Seq.empty
  ) ++ (
    if (unusedWarn) Seq("-Ywarn-unused") else Seq.empty
  ) ++ (
    if (importWarn) Seq("-Ywarn-unused-import") else Seq.empty
  )

  def scalacOpts(sver: SVer, java8Only: Boolean, optim: Boolean = optimize) = sharedScalacOptions ++ {
    def opt = if (optim) Seq("-optimize") else Seq.empty
    sver match {
      case j8 if j8.requireJava8 => Seq.empty
      case SVer2_10              => Seq("-target:jvm-1.6") ++ opt
      case _                     =>
        opt ++ Seq("-target:jvm-" + (if (java8Only) "1.8" else minimumJavaVersion))
    }
  }

  private[this] val sharedJavacOptions = Seq.empty
  def javacOpts(java8Only: Boolean) = sharedJavacOptions ++ {
    if (java8Only) {
      Seq (
        "-target", "1.8",
        "-source", "1.8"
      )
    } else {
      Seq (
        "-target", minimumJavaVersion,
        "-source", minimumJavaVersion
      )
    }
  }

  implicit class ProjectHelper(p: Project) {
    def commonSettings() = siteSettings(p).settings(buildSettings: _*)
  }

  /* Site setup */
  def siteSettings(p: Project) = {
    p.enablePlugins(SiteScaladocPlugin)
     .settings(
       scalacOptions in (Compile,doc) ++= Seq(
         "-groups",
         "-implicits",
         "-diagrams",
         "-sourcepath", (baseDirectory in ThisBuild).value.getAbsolutePath,
         "-doc-source-url", "https://github.com/sarahgerweck/scala-utils/blob/master€{FILE_PATH}.scala"
       )
     )
  }

  lazy val buildSettings = buildMetadata ++ Seq (
    organization         := buildOrganization,
    organizationName     := buildOrganizationName,
    organizationHomepage := buildOrganizationUrl map { url _ },

    scalaVersion         := buildScalaVersion,
    crossScalaVersions   := buildScalaVersions,

    autoAPIMappings      := true,

    updateOptions        := updateOptions.value.withCachedResolution(cachedResolution),
    parallelExecution    := parallelBuild,

    evictionWarningOptions in update :=
      EvictionWarningOptions.default.withWarnTransitiveEvictions(false).withWarnDirectEvictions(false).withWarnScalaVersionEviction(false)
  )
}

