import sbt._
import Keys._

import scala.util.Properties.envOrNone

import com.typesafe.sbt.site._

import Helpers._

sealed trait Basics {
  final val buildOrganization     = "org.gerweck.scala"
  final val buildOrganizationName = "Sarah Gerweck"
  final val buildOrganizationUrl  = Some("https://github.com/sarahgerweck")

  final val buildScalaVersion     = "2.12.2"
  final val extraScalaVersions    = Seq("2.11.11")
  final val minimumJavaVersion    = "1.6"
  lazy  val defaultOptimize       = true
  lazy  val defaultOptimizeGlobal = false

  lazy  val parallelBuild         = false
  lazy  val cachedResolution      = false

  final val defaultNewBackend     = false

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
  lazy val optimize       = boolFlag("OPTIMIZE") orElse boolFlag("OPTIMISE") getOrElse defaultOptimize
  lazy val optimizeGlobal = boolFlag("OPTIMIZE_GLOBAL") getOrElse defaultOptimizeGlobal
  lazy val optimizeWarn   = boolFlag("OPTIMIZE_WARNINGS") getOrElse false
  lazy val deprecation    = boolFlag("NO_DEPRECATION") map (!_) getOrElse true
  lazy val debug          = boolFlag("DEBUGGER") getOrElse false
  lazy val debugPort      = envOrNone("DEBUGGER_PORT") map { _.toInt } getOrElse 5050
  lazy val debugSuspend   = boolFlag("DEBUGGER_SUSPEND") getOrElse true
  lazy val unusedWarn     = boolFlag("UNUSED_WARNINGS") getOrElse false
  lazy val importWarn     = boolFlag("IMPORT_WARNINGS") getOrElse false
  lazy val java8Flag      = boolFlag("BUILD_JAVA_8") getOrElse false
  lazy val newBackend     = boolFlag("NEW_BCODE_BACKEND") getOrElse defaultNewBackend

  val buildScalaVersions  = buildScalaVersion +: extraScalaVersions

  def basicScalacOptions = Def.derive {
    scalacOptions ++= {
      val sv = sver.value
      var options = Seq.empty[String]

      options :+= "-unchecked"
      options :+= "-feature"
      if (deprecation) {
        options :+= "-deprecation"
      }
      if (unusedWarn) {
        options :+= "-Ywarn-unused"
      }
      if (importWarn) {
        options :+= "-Ywarn-unused-import"
      }
      if (!sver.value.requireJava8) {
        options :+= "-target:jvm-" + minimumJavaVersion
      }
      if (sver.value.supportsNewBackend && newBackend && !sver.value.requireJava8) {
        options :+= "-Ybackend:GenBCode"
      }

      options
    }
  }

  def optimizationScalacOptions(optim: Boolean = optimize) = Def.derive {
    scalacOptions ++= {
      var options = Seq.empty[String]

      if (optim) {
        val useNewBackend = sver.value.supportsNewBackend && newBackend
        if (useNewBackend) {
          if (optimizeGlobal) {
            options :+= "-opt:l:classpath"
          } else {
            options :+= "-opt:l:project"
          }
          if (optimizeWarn) {
            options :+= "-opt-warnings:_"
          }
        } else {
          options :+= "-optimize"
          if (optimizeWarn) {
            options :+= "-Yinline-warnings"
          }
        }
      }

      options
    }
  }

  def addScalacOptions(optim: Boolean = optimize) = new Def.SettingList(Seq(
    basicScalacOptions,
    optimizationScalacOptions(optim)
  ))

  def addJavacOptions() = Def.derive {
    javacOptions ++= {
      val sv = SVer(scalaBinaryVersion.value)
      var options = Seq.empty[String]

      if (sv.requireJava8) {
        options ++= Seq[String](
          "-target", "1.8",
          "-source", "1.8"
        )
      } else {
        options ++= Seq[String](
          "-target", minimumJavaVersion,
          "-source", minimumJavaVersion
        )
      }

      options
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

