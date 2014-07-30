import sbt._
import Keys._

import com.typesafe.sbt.SbtSite.site
import sbtrelease.ReleasePlugin._

import scala.util.Properties.envOrNone

object BuildSettings {
  import Helpers._

  final val buildOrganization = "org.gerweck.scala"
  final val buildScalaVersion = "2.10.4"
  final val buildJavaVersion  = "1.7"
  final val optimize          = true

  val buildScalaVersions = Seq("2.10.4")

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

  val buildSettings = Seq (
    organization := buildOrganization,
    licenses     := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    homepage     := Some(url("https://github.com/sarahgerweck/scala-utils")),
    description  := "Miscellaneous utility functionality for Scala.",
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
  def opts(names: String*): Option[String] = names.view.map(getProp _).foldLeft(None: Option[String]) { _ orElse _ }
}

object Resolvers {
  val gerweckSnaps    = "Gerweck Snapshots" at "s3://repo.gerweck.org.s3-us-west-2.amazonaws.com/snapshot/"
  val gerweckReleases = "Gerweck Releases" at "s3://repo.gerweck.org.s3-us-west-2.amazonaws.com/releases/"
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

  val publishSettings = Seq (
    publishMavenStyle    := true,
    pomIncludeRepository := { _ => false },

    publishTo            := {
      if (version.value.trim endsWith "SNAPSHOT")
        Some(gerweckSnaps)
      else
        Some(gerweckReleases)
    },

    pomExtra             := (
      <developers>
        <developer>
          <id>sarah</id>
          <name>Sarah Gerweck</name>
          <email>sarah.a180@gmail.com</email>
          <url>https://github.com/sarahgerweck</url>
          <timezone>America/Los_Angeles</timezone>
        </developer>
      </developers>
    )
  )
}

object Release {
  import sbtrelease._
  import ReleaseStateTransformations._
  import ReleasePlugin._
  import ReleaseKeys._
  import Utilities._
  import com.typesafe.sbt.SbtPgp.PgpKeys._

  val settings = releaseSettings ++ Seq (
    ReleaseKeys.releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishArtifacts.copy(action = publishSignedAction),
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  )

  lazy val publishSignedAction = { st: State =>
    val extracted = st.extract
    val ref = extracted.get(thisProjectRef)
    extracted.runAggregated(publishSigned in Global in ref, st)
  }
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
  final val log4sVersion       = "[1.0.3,)"
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

  lazy val baseSettings = buildSettings ++ Eclipse.settings ++ publishSettings ++ Release.settings

  lazy val allResolvers = Seq ()

  lazy val utilsDeps = Seq (
    slf4j,
    jclBridge % "runtime",
    log4s,
    logback % "test",
    commonsIo,
    jodaTime,
    jodaConvert,
    commonsVfs
  )

  lazy val root = (project in file ("."))
    .settings(baseSettings: _*)
    .settings(
      name := "gerweck-utils",
      libraryDependencies ++= utilsDeps ++ Seq (
        "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided"
      ),
      resolvers ++= allResolvers
    )
}
