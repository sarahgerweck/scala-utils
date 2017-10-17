import sbt._
import Keys._

import scala.util.Properties.envOrNone

import com.typesafe.sbt.site._

import Helpers._

trait BasicSettings {
  val buildOrganization: String
  val buildOrganizationName: String
  val buildOrganizationUrl: Option[java.net.URL] = None
  val projectDescription: String
  val projectStartYear: Int
  val projectHomepage: Option[java.net.URL] = None

  val buildScalaVersion: String
  val extraScalaVersions: Seq[String]
  val minimumJavaVersion: String = "1.8"
  val defaultOptimize: Boolean = true
  val defaultOptimizeGlobal: Boolean = false
  val inlinePatterns: Seq[String]
  val autoAddCompileOptions: Boolean = true

  val parallelBuild: Boolean = true
  val cachedResolution: Boolean = true
  val sonatypeResolver: Boolean = false

  val projectLicenses: Seq[(String, java.net.URL)]

  val defaultNewBackend: Boolean = false

  val developerInfo: scala.xml.Elem

  val buildMetadata: Seq[Setting[_]]
}

trait GithubProject extends BasicSettings {
  val githubOrganization: String
  val githubProject: String

  val githubOrgPageFallback: Boolean = true
  lazy val githubPage = url(s"https://github.com/${githubOrganization}/${githubProject}")

  lazy val buildMetadata = Vector(
    licenses    := projectLicenses,
    homepage    := Some(projectHomepage.getOrElse(githubPage)),
    description := projectDescription,
    startYear   := Some(projectStartYear),
    scmInfo     := Some(ScmInfo(githubPage, s"scm:git:git@github.com:${githubOrganization}/${githubProject}.git"))
  )
}

trait ApacheLicensed extends BasicSettings {
  final val projectLicenses = Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
}

object BasicSettings extends AutoPlugin with ProjectSettings { BasicSettings =>
  override def requires = SiteScaladocPlugin

  private[this] lazy val githubOrgPage = url(s"https://github.com/${githubOrganization}")

  override lazy val projectSettings = (
    buildMetadata ++
    Seq (
      organization         :=  buildOrganization,
      organizationName     :=  buildOrganizationName,
      organizationHomepage :=  buildOrganizationUrl.orElse(if (githubOrgPageFallback) Some(githubOrgPage) else None),

      scalaVersion         :=  buildScalaVersion,
      crossScalaVersions   :=  buildScalaVersions,

      autoAPIMappings      :=  true,

      updateOptions        :=  updateOptions.value.withCachedResolution(cachedResolution),
      parallelExecution    :=  parallelBuild,

      evictionWarningOptions in update :=
        EvictionWarningOptions.default.withWarnTransitiveEvictions(false).withWarnDirectEvictions(false).withWarnScalaVersionEviction(false)
    ) ++ (
      if (autoAddCompileOptions) {
        addScalacOptions() ++ addJavacOptions()
      } else {
        Seq.empty
      }
    ) ++ (
      if (sonatypeResolver) {
        /* Many OSS projects push here and then appear in Maven Central later */
        Seq(resolvers += Resolver.sonatypeRepo("releases"))
      } else {
        Seq.empty
      }
    )
  )

  /* Overridable flags */
  lazy val optimize       = boolFlag("OPTIMIZE") orElse boolFlag("OPTIMISE") getOrElse defaultOptimize
  lazy val optimizeGlobal = boolFlag("OPTIMIZE_GLOBAL") getOrElse defaultOptimizeGlobal
  lazy val optimizeWarn   = boolFlag("OPTIMIZE_WARNINGS") getOrElse false
  lazy val noFatalWarn    = boolFlag("NO_FATAL_WARNINGS") getOrElse false
  lazy val deprecation    = boolFlag("NO_DEPRECATION") map (!_) getOrElse true
  lazy val inlineWarn     = boolFlag("INLINE_WARNINGS") getOrElse false
  lazy val debug          = boolFlag("DEBUGGER") getOrElse false
  lazy val debugPort      = envOrNone("DEBUGGER_PORT") map { _.toInt } getOrElse 5050
  lazy val debugSuspend   = boolFlag("DEBUGGER_SUSPEND") getOrElse true
  lazy val unusedWarn     = boolFlag("UNUSED_WARNINGS") getOrElse false
  lazy val importWarn     = boolFlag("IMPORT_WARNINGS") getOrElse false
  lazy val java8Flag      = boolFlag("BUILD_JAVA_8") getOrElse false
  lazy val newBackend     = boolFlag("NEW_BCODE_BACKEND") getOrElse defaultNewBackend

  lazy val buildScalaVersions = buildScalaVersion +: extraScalaVersions

  def basicScalacOptions = Def.derive {
    scalacOptions ++= {
      var options = Seq.empty[String]
      val sv = sver.value

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
      if (!sv.requireJava8) {
        options :+= "-target:jvm-" + minimumJavaVersion
      }
      if (sv.backend == SupportsNewBackend && newBackend) {
        options :+= "-Ybackend:GenBCode"
      }

      options
    }
  }

  def optimizationScalacOptions(optim: Boolean = optimize) = Def.derive {
    scalacOptions ++= {
      var options = Seq.empty[String]
      val sv = sver.value
      val fos = forceOldInlineSyntax.value

      if (optim) {
        def doNewWarn(): Unit = {
          if (optimizeWarn) {
            options :+= "-opt-warnings:_"
          }
        }

        if (sv.backend == NewBackend && !fos) {
          options :+= "-opt:l:inline"

          val inlineFrom = {
            var patterns = Seq.empty[String]
            if (optimizeGlobal) {
              patterns :+= "**"
            } else {
              patterns :+= "<sources>"
            }
            patterns ++= inlinePatterns
            patterns
          }

          options :+= inlineFrom.mkString("-opt-inline-from:", ":", "")

          doNewWarn()
        } else if (sv.backend == NewBackend && fos || sv.backend == SupportsNewBackend && newBackend) {
          if (optimizeGlobal) {
            options :+= "-opt:l:classpath"
          } else {
            options :+= "-opt:l:project"
          }
          doNewWarn()
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

  def basicSiteSettings = Def.derive {
    scalacOptions in (Compile,doc) ++= Seq(
      "-groups",
      "-implicits",
      "-diagrams",
      "-sourcepath", (baseDirectory in ThisBuild).value.getAbsolutePath,
      "-doc-source-url", s"https://github.com/${githubOrganization}/${githubProject}/blob/master€{FILE_PATH}.scala"
    )
  }
}
