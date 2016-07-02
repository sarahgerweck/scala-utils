import sbt._
import Keys._

import com.typesafe.sbt.site._
import sbtrelease.ReleasePlugin.autoImport._

import scala.util.Properties.envOrNone
import com.typesafe.sbteclipse.plugin.EclipsePlugin._

import Helpers._

sealed trait Basics {
  final val buildOrganization  = "org.gerweck.scala"
  final val buildOrganizationName = "Sarah Gerweck"
  final val buildOrganizationUrl  = Some("https://github.com/sarahgerweck")

  final val buildScalaVersion  = "2.11.8"
  final val extraScalaVersions = Seq.empty
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

  def scalacOpts(sver: SVer, java8Only: Boolean) = sharedScalacOptions ++ {
    def opt = if (optimize) Seq("-optimize") else Seq.empty
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
  def siteSettings(p: Project) = p.enablePlugins(SiteScaladocPlugin)

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

object Helpers {
  def getProp(name: String): Option[String] = sys.props.get(name) orElse sys.env.get(name)
  def parseBool(str: String): Boolean = Set("yes", "y", "true", "t", "1") contains str.trim.toLowerCase
  def boolFlag(name: String): Option[Boolean] = getProp(name) map { parseBool _ }
  def boolFlag(name: String, default: Boolean): Boolean = boolFlag(name) getOrElse default
  def opts(names: String*): Option[String] = names.view.map(getProp _).foldLeft(None: Option[String]) { _ orElse _ }

  import scala.xml._
  def excludePomDeps(exclude: (String, String) => Boolean): Node => Node = { node: Node =>
    val rewriteRule = new transform.RewriteRule {
      override def transform(n: Node): NodeSeq = {
        if ((n.label == "dependency") && exclude((n \ "groupId").text, (n \ "artifactId").text))
          NodeSeq.Empty
        else
          n
      }
    }
    val transformer = new transform.RuleTransformer(rewriteRule)
    transformer.transform(node)(0)
  }

  sealed trait SVer {
    def requireJava8: Boolean
  }
  object SVer {
    def apply(scalaVersion: String): SVer = {
      scalaVersion match {
        case "2.10"      => SVer2_10
        case "2.11"      => SVer2_11
        case "2.12.0-M1" => SVer2_12M1
        case "2.12.0-M2" => SVer2_12M2
        case "2.12.0-M3" => SVer2_12M3
        case "2.12.0-M4" => SVer2_12M4
        case "2.12"      => SVer2_12
      }
    }
  }
  case object SVer2_10 extends SVer {
    def requireJava8 = false
  }
  case object SVer2_11 extends SVer {
    def requireJava8 = false
  }
  case object SVer2_12M1 extends SVer {
    def requireJava8 = true
  }
  case object SVer2_12M2 extends SVer {
    def requireJava8 = true
  }
  case object SVer2_12M3 extends SVer {
    def requireJava8 = true
  }
  case object SVer2_12M4 extends SVer {
    def requireJava8 = true
  }
  case object SVer2_12 extends SVer {
    def requireJava8 = true
  }
}

object Resolvers {
  val sonaSnaps     = "Sonatype Snaps" at "https://oss.sonatype.org/content/repositories/snapshots"
  val sonaStage     = "Sonatype Staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
}

object PublishSettings {
  import BuildSettings._
  import Resolvers._
  import Helpers._

  val sonaCreds = (
    for {
      user <- getProp("SONATYPE_USER")
      pass <- getProp("SONATYPE_PASS")
    } yield {
      credentials +=
          Credentials("Sonatype Nexus Repository Manager",
                      "oss.sonatype.org",
                      user, pass)
    }
  ).toSeq

  val publishSettings = sonaCreds ++ Seq (
    publishMavenStyle    := true,
    pomIncludeRepository := { _ => false },
    publishArtifact in Test := false,

    publishTo            := {
      if (version.value.trim endsWith "SNAPSHOT")
        Some(sonaSnaps)
      else
        Some(sonaStage)
    },

    pomExtra             := developerInfo
  )

  val falsePublishSettings = publishSettings ++ Seq (
    publishArtifact in Compile := false,
    publishArtifact in Test := false,
    publishTo := Some(Resolver.file("phony-repo", file("target/repo")))
  )

}

object Release {
  import com.typesafe.sbt.SbtPgp.autoImport._

  val settings = Seq (
    releaseCrossBuild             := true,
    releasePublishArtifactsAction := PgpKeys.publishSigned.value
  )
}

object Eclipse {
  import com.typesafe.sbteclipse.plugin.EclipsePlugin._

  val settings = Seq (
    EclipseKeys.createSrc            := EclipseCreateSrc.Default,
    EclipseKeys.projectFlavor        := EclipseProjectFlavor.ScalaIDE,
    EclipseKeys.executionEnvironment := Some(EclipseExecutionEnvironment.JavaSE18),
    EclipseKeys.withSource           := true,
    EclipseKeys.skipParents          := false
  )
}

object Dependencies {
  final val slf4jVersion       = "1.7.21"
  final val log4sVersion       = "1.3.0"
  final val logbackVersion     = "1.1.7"
  final val jodaTimeVersion    = "2.9.4"
  final val jodaConvertVersion = "1.8.1"
  final val threeTenVersion    = "1.3.2"
  final val commonsVfsVersion  = "2.1"
  final val commonsIoVersion   = "2.5"
  final val spireVersion       = "0.11.0"
  final val groovyVersion      = "2.4.7"
  final val twitterUtilVersion = "6.34.0"
  final val scalaCheckVersion  = "1.12.5"
  final val scalaParserVersion = "1.0.4"
  final val scalaXmlVersion    = "1.0.5"

  val log4s       = "org.log4s"           %% "log4s"           % log4sVersion
  val slf4j       = "org.slf4j"           %  "slf4j-api"       % slf4jVersion
  val jclBridge   = "org.slf4j"           %  "jcl-over-slf4j"  % slf4jVersion
  val logback     = "ch.qos.logback"      %  "logback-classic" % logbackVersion
  val commonsIo   = "commons-io"          %  "commons-io"      % commonsIoVersion
  val jodaTime    = "joda-time"           %  "joda-time"       % jodaTimeVersion
  val jodaConvert = "org.joda"            %  "joda-convert"    % jodaConvertVersion
  val threeTen    = "org.threeten"        %  "threetenbp"      % threeTenVersion
  val spire       = "org.spire-math"      %% "spire"           % spireVersion
  val groovy      = "org.codehaus.groovy" %  "groovy-all"      % groovyVersion
  val twitterUtil = "com.twitter"         %% "util-core"       % twitterUtilVersion
  val commonsVfs  = {
    val base      = "org.apache.commons"  %  "commons-vfs2"    % commonsVfsVersion
    base.exclude("commons-logging", "commons-logging")
        .exclude("org.apache.maven.scm", "maven-scm-provider-svnexe")
        .exclude("org.apache.maven.scm", "maven-scm-api")
  }

  val scalaCheck  = "org.scalacheck"      %% "scalacheck"      % scalaCheckVersion

  /* Use like this: libraryDependencies <++= (scalaBinaryVersion) (scalaParser) */
  def scalaParser(scalaBinaryVersion: String): Seq[ModuleID] = scalaBinaryVersion match {
    case "2.10" => Seq.empty
    case _      => Seq("org.scala-lang.modules" %% "scala-parser-combinators" % scalaParserVersion % "optional")
  }

  def scalaXml(scalaBinaryVersion: String): Seq[ModuleID] = scalaBinaryVersion match {
    case "2.10" => Seq.empty
    case _      => Seq("org.scala-lang.modules" %% "scala-xml" % scalaXmlVersion % "optional")
  }

  def scalaTest(scalaBinaryVersion: String): ModuleID = scalaBinaryVersion match {
    case "2.12.0-M1" => "org.scalatest" %% "scalatest" % "2.2.5-M1"
    case "2.12.0-M2" => "org.scalatest" %% "scalatest" % "2.2.5-M2"
    case "2.12.0-M3" => "org.scalatest" %% "scalatest" % "2.2.5-M3"
    case "2.12.0-M4" => "org.scalatest" %% "scalatest" % "2.2.6"
    case _           => "org.scalatest" %% "scalatest" % "2.2.6"
  }

  /* ********************************************************************** */
  /*                                  Akka                                  */
  /* ********************************************************************** */
  final val akkaVersion        = "2.4.7"

  val akkaActor      = "com.typesafe.akka"   %% "akka-actor"             % akkaVersion
  val akkaAgent      = "com.typesafe.akka"   %% "akka-agent"             % akkaVersion
  val akkaRemote     = "com.typesafe.akka"   %% "akka-remote"            % akkaVersion
  val akkaSlf4j      = "com.typesafe.akka"   %% "akka-slf4j"             % akkaVersion
  val akkaStream     = "com.typesafe.akka"   %% "akka-stream"            % akkaVersion
  val akkaHttpCore   = "com.typesafe.akka"   %% "akka-http-core"         % akkaVersion
  val akkaHttp       = "com.typesafe.akka"   %% "akka-http-experimental" % akkaVersion

  /* ********************************************************************** */
  /*                                Helpers                                 */
  /* ********************************************************************** */
  private def noCL(m: ModuleID) = (
    m exclude("commons-logging", "commons-logging")
      exclude("commons-logging", "commons-logging-api")
  )
}

object UtilsBuild extends Build {
  build =>

  import BuildSettings._
  import Resolvers._
  import Dependencies._
  import PublishSettings._
  import Helpers._

  lazy val basicLogDeps = Seq(
    slf4j,
    log4s,
    logback % "test",
    groovy % "test"
  )

  lazy val utilsDeps = basicLogDeps ++ Seq (
    jclBridge,
    scalaCheck % "test",
    commonsIo,
    jodaTime % "optional",
    jodaConvert % "optional",
    /* ThreeTen is optional in some versions and not others, so see below */
    spire % "provided,optional",
    commonsVfs
  )

  lazy val macros = (project in file ("macro"))
    .commonSettings()
    .settings(Eclipse.settings: _*)
    .settings(falsePublishSettings: _*)
    .settings (
      name := "Gerweck Utils Macros",
      libraryDependencies += log4s,
      libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-reflect" % _),
      resolvers += Resolver.sonatypeRepo("releases"),

      scalaSource in Compile := {
        scalaBinaryVersion.value match {
          case "2.10" => baseDirectory.value / "src" / "main" / "scala-2.10"
          case _      => baseDirectory.value / "src" / "main" / "scala-2.11"
        }
      },

      publish := {},
      publishLocal := {},
      publishArtifact := false,

      exportJars := false,
      releaseProcess := Seq.empty
    )

  lazy val root: Project = (project in file ("."))
    .aggregate(macros, core, java6, twitter, akka)
    .commonSettings()
    .settings(Eclipse.settings: _*)
    .settings(EclipseKeys.skipParents in ThisBuild := false)
    .settings(publishSettings: _*)
    .settings(Release.settings: _*)
    .settings (
      name := "Gerweck Utils Root",

      publish := {},
      publishLocal := {},
      publishArtifact := false,

      exportJars := false,

      skip in Compile := true,
      skip in Test := true
    )

  lazy val core: Project = (project in file ("core"))
    .dependsOn(macros % "optional")
    .commonSettings()
    .settings(Eclipse.settings: _*)
    .settings(EclipseKeys.skipParents in ThisBuild := false)
    .settings(publishSettings: _*)
    .settings(Release.settings: _*)
    .settings(
      name := "Gerweck Utils",

      scalacOptions ++= scalacOpts(SVer(scalaBinaryVersion.value), true),
      javacOptions ++= javacOpts(true),

      libraryDependencies ++= utilsDeps,
      libraryDependencies += threeTen % "optional",
      libraryDependencies += scalaTest(scalaBinaryVersion.value) % "test",
      libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-reflect" % _),

      libraryDependencies <++= (scalaBinaryVersion) (scalaParser),
      libraryDependencies <++= (scalaBinaryVersion) (scalaXml),

      resolvers += Resolver.sonatypeRepo("releases"),

      unmanagedSourceDirectories in Compile <+= (scalaBinaryVersion, baseDirectory) { (ver, dir) =>
        ver match {
          case "2.10" => dir / "src" / "main" / "scala-2.10"
          case _      => dir / "src" / "main" / "scala-2.11"
        }
      },

      unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "main" / "scala-java8",

      // include the macro classes and resources in the main jar
      mappings in (Compile, packageBin) ++= mappings.in(macros, Compile, packageBin).value,

      // include the macro sources in the main source jar
      mappings in (Compile, packageSrc) ++= mappings.in(macros, Compile, packageSrc).value,

      // Do not include macros as a dependency.
      pomPostProcess := excludePomDeps { (group, artifact) => (group == "org.gerweck.scala") && (artifact startsWith "gerweck-utils-macro") }
    )

  lazy val java6 = (project in file ("java6"))
    .dependsOn(macros % "optional")
    .commonSettings()
    .settings(Eclipse.settings: _*)
    .settings(EclipseKeys.skipParents in ThisBuild := false)
    .settings(publishSettings: _*)
    .settings(Release.settings: _*)
    .settings(
      name := "Gerweck Utils (Java 6)",
      normalizedName := "gerweck-utils-java6",

      scalacOptions ++= scalacOpts(SVer(scalaBinaryVersion.value), false),
      javacOptions ++= javacOpts(false),

      libraryDependencies ++= utilsDeps,
      libraryDependencies += threeTen,
      libraryDependencies += scalaTest(scalaBinaryVersion.value) % "test",
      libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-reflect" % _),

      libraryDependencies <++= (scalaBinaryVersion) (scalaParser),
      libraryDependencies <++= (scalaBinaryVersion) (scalaXml),

      resolvers += Resolver.sonatypeRepo("releases"),

      unmanagedSourceDirectories in Compile <+= (scalaBinaryVersion, baseDirectory) { (ver, dir) =>
        ver match {
          case "2.10" => dir / ".." / "core" / "src" / "main" / "scala-2.10"
          case _      => dir / ".." / "core" / "src" / "main" / "scala-2.11"
        }
      },
      scalaSource in Compile := baseDirectory.value / ".." / "core" / "src" / "main" / "scala",
      scalaSource in Test := baseDirectory.value / ".." / "core" / "src" / "test" / "scala",
      unmanagedSourceDirectories in Compile += baseDirectory.value / ".." / "core" / "src" / "main" / "scala-java6",

      // include the macro classes and resources in the main jar
      mappings in (Compile, packageBin) ++= mappings.in(macros, Compile, packageBin).value,

      // include the macro sources in the main source jar
      mappings in (Compile, packageSrc) ++= mappings.in(macros, Compile, packageSrc).value,

      // Do not include macros as a dependency.
      pomPostProcess := excludePomDeps { (group, artifact) => (group == "org.gerweck.scala") && (artifact startsWith "gerweck-utils-macro") }
    )

  lazy val twitter = (project in file ("twitter"))
    .commonSettings()
    .settings(Eclipse.settings: _*)
    .settings(EclipseKeys.skipParents in ThisBuild := false)
    .settings(publishSettings: _*)
    .settings(Release.settings: _*)
    .settings(
      name := "Gerweck Utils Twitter",
      libraryDependencies ++= basicLogDeps,
      libraryDependencies += twitterUtil % "optional"
    )

  lazy val akka: Project = (project in file ("akka"))
    .dependsOn(core)
    .commonSettings()
    .settings(Eclipse.settings: _*)
    .settings(EclipseKeys.skipParents in ThisBuild := false)
    .settings(publishSettings: _*)
    .settings(Release.settings: _*)
    .settings(
      name := "Gerweck Utils Akka",

      scalacOptions ++= scalacOpts(SVer(scalaBinaryVersion.value), true),
      javacOptions ++= javacOpts(true),

      /* Logging */
      libraryDependencies ++= basicLogDeps,
      /* Akka */
      libraryDependencies ++= Seq (
        akkaActor,
        akkaStream
      )
    )
}
