import sbt._
import Keys._

import scala.util.Properties.envOrNone

object BuildSettings {
  final val buildOrganization = "org.gerweck.scala"
  final val baseVersion       = "1.0.0"
  final val buildScalaVersion = "2.10.4"
  final val buildJavaVersion  = "1.7"
  final val optimize          = true

  val buildScalaVersions = Seq("2.10.4")

  val buildNumberOpt = envOrNone("TRAVIS_BUILD_NUMBER") orElse envOrNone("BUILD_NUMBER")
  val isJenkins      = buildNumberOpt.isDefined

  val buildVersion = buildNumberOpt match {
    case Some("") | Some("SNAPSHOT") | None => baseVersion + "-SNAPSHOT"
    case Some(s)                            => baseVersion + "." + s
  }

  lazy val isSnapshot = buildVersion endsWith "-SNAPSHOT"

  val buildScalacOptions = Seq (
    "-deprecation",
    "-unchecked",
    "-feature",
    "-target:jvm-" + buildJavaVersion
  ) ++ (
    if (optimize) Seq("-optimize") else Seq.empty
  )

  val buildJavacOptions = Seq(
    "-target", buildJavaVersion,
    "-source", buildJavaVersion
  )

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    licenses     := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    homepage     := Some(url("https://github.com/sarahgerweck/scala-utils")),
    description  := "Various utility methods with Scala APIs.",
    startYear    := Some(2012),
    scmInfo      := Some(ScmInfo(url("https://github.com/sarahgerweck/scala-utils"), "scm:git:git@github.com:sarahgerweck/scala-utils.git")),

    scalaVersion       := buildScalaVersion,
    crossScalaVersions := buildScalaVersions,
    autoAPIMappings    := true,

    scalacOptions ++= buildScalacOptions,
    javacOptions  ++= buildJavacOptions
  )
}

object Helpers {
  def getProp(name: String): Option[String] = sys.props.get(name) orElse sys.env.get(name)
  def parseBool(str: String): Boolean = Set("yes", "y", "true", "t", "1") contains str.trim.toLowerCase
  def boolFlag(name: String): Option[Boolean] = getProp(name) map { parseBool _ }
  def boolFlag(name: String, default: Boolean): Boolean = boolFlag(name) getOrElse default
}

object Resolvers {
  val sarahSnaps      = "Sarah Snaps" at "https://repository-gerweck.forge.cloudbees.com/snapshot/"
  val log4sReleases   = "Log4s Releases" at "http://repo.log4s.org/releases/"
  val gerweckSnaps    = "Gerweck Snapshots" at "s3://repo.gerweck.org.s3-us-west-2.amazonaws.com/snapshot/"
  val gerweckReleases = "Gerweck Releases" at "s3://repo.gerweck.org.s3-us-west-2.amazonaws.com/releases/"
}

object PublishSettings {
  import BuildSettings._
  import Resolvers._

  val publishRepo =
    if (isSnapshot) Some(gerweckSnaps)
    else            Some(gerweckReleases)

  val publishSettings = Seq (
    publishMavenStyle    := true,
    pomIncludeRepository := { _ => false },

    publishTo            := publishRepo,
    pomExtra             := (
      <developers>
        <developer>
          <id>sarah</id>
          <name>Sarah Gerweck</name>
          <url>http://github.com/sarahgerweck</url>
        </developer>
      </developers>
    )
  )
}

object Eclipse {
  import com.typesafe.sbteclipse.plugin.EclipsePlugin._

  val settings = Seq (
    EclipseKeys.createSrc            := EclipseCreateSrc.Default + EclipseCreateSrc.Resource,
    EclipseKeys.projectFlavor        := EclipseProjectFlavor.Scala,
    EclipseKeys.executionEnvironment := Some(EclipseExecutionEnvironment.JavaSE17),
    EclipseKeys.withSource           := true
  )
}

object Dependencies {
  final val slf4jVersion       = "1.7.7"
  final val log4sVersion       = "[1.0,)"
  final val logbackVersion     = "1.1.2"
  final val jodaTimeVersion    = "2.4"
  final val jodaConvertVersion = "1.6"
  final val commonsVfsVersion  = "2.0"
  final val commonsIoVersion   = "2.4"

  val log4s       = "org.log4s"          %% "log4s"           % log4sVersion
  val slf4j       = "org.slf4j"          %  "slf4j-api"       % slf4jVersion
  val jclBridge   = "org.slf4j"          %  "jcl-over-slf4j"  % slf4jVersion
  val logback     = "ch.qos.logback"     %  "logback-classic" % logbackVersion
  val commonsIo   = "commons-io"         %  "commons-io"      % commonsIoVersion
  val jodaTime    = "joda-time"          %  "joda-time"       % jodaTimeVersion
  val jodaConvert = "org.joda"           %  "joda-convert"    % jodaConvertVersion
  val commonsVfs  = {
    val base      = "org.apache.commons" %  "commons-vfs2"    % commonsVfsVersion
    base.exclude("commons-logging", "commons-logging")
        .exclude("org.apache.maven.scm", "maven-scm-provider-svnexe")
        .exclude("org.apache.maven.scm", "maven-scm-api")
  }

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

  lazy val baseSettings = buildSettings ++ Eclipse.settings ++ publishSettings

  lazy val allResolvers = Seq (
    log4sReleases
  )

  lazy val utilsDeps = Seq (
    slf4j,
    jclBridge,
    log4s,
    logback % "test",
    commonsIo,
    jodaTime,
    jodaConvert,
    commonsVfs
  )

  lazy val root = Project (
    id = "utils",
    base = file("."),
    settings = baseSettings ++ Seq (
      name := "gerweck-utils",
      libraryDependencies ++= utilsDeps ++ Seq (
        "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided"
      ),
      resolvers ++= allResolvers
    )
  )
}
