import sbt._

object Dependencies {
  final val slf4jVersion        = "1.7.25"
  final val log4sVersion        = "1.6.1"
  final val logbackVersion      = "1.2.3"
  final val jodaTimeVersion     = "2.10"
  final val jodaConvertVersion  = "1.9.2"
  final val threeTenVersion     = "1.3.6"
  final val commonsVfsVersion   = "2.2"
  final val commonsIoVersion    = "2.6"
  final val spireVersion        = "0.13.0"
  final val groovyVersion       = "2.4.15"
  final val json4sVersion       = "3.5.4"
  final val twitterUtilVersion  = "18.6.0"
  final val scalaParserVersion  = "1.1.0"
  final val scalaXmlVersion     = "1.1.0"
  final val bouncyCastleVersion = "1.59"

  val log4s            = "org.log4s"           %% "log4s"           % log4sVersion
  val slf4j            = "org.slf4j"           %  "slf4j-api"       % slf4jVersion
  val jclBridge        = "org.slf4j"           %  "jcl-over-slf4j"  % slf4jVersion
  val logback          = "ch.qos.logback"      %  "logback-classic" % logbackVersion
  val commonsIo        = "commons-io"          %  "commons-io"      % commonsIoVersion
  val jodaTime         = "joda-time"           %  "joda-time"       % jodaTimeVersion
  val jodaConvert      = "org.joda"            %  "joda-convert"    % jodaConvertVersion
  val threeTen         = "org.threeten"        %  "threetenbp"      % threeTenVersion
  val spire            = "org.spire-math"      %% "spire"           % spireVersion
  val groovy           = "org.codehaus.groovy" %  "groovy-all"      % groovyVersion
  val twitterUtil      = "com.twitter"         %% "util-core"       % twitterUtilVersion
  val json4sNative     = "org.json4s"          %% "json4s-native"   % json4sVersion
  val json4sJackson    = "org.json4s"          %% "json4s-jackson"  % json4sVersion
  val json4sExt        = "org.json4s"          %% "json4s-ext"      % json4sVersion exclude("joda-time", "joda-time") exclude("org.joda", "joda-convert")
  val bouncyCastle     = "org.bouncycastle"    %  "bcprov-jdk15on"  % bouncyCastleVersion
  val bouncyCastlePkix = "org.bouncycastle"    %  "bcpkix-jdk15on"  % bouncyCastleVersion

  val commonsVfs = {
    val base = "org.apache.commons"  %  "commons-vfs2"    % commonsVfsVersion
    base.exclude("commons-logging", "commons-logging")
        .exclude("org.apache.maven.scm", "maven-scm-provider-svnexe")
        .exclude("org.apache.maven.scm", "maven-scm-api")
  }

  /* ********************************************************************** */
  /*                                Database                                */
  /* ********************************************************************** */
  final val liquibaseVersion        = "3.6.2"
  final val liquibaseLoggingVersion = "2.0.0"
  final val slickVersion            = "3.2.3"

  val liquibase        = "org.liquibase"      %  "liquibase-core"  % liquibaseVersion
  val liquibaseLogging = "com.mattbertolini"  %  "liquibase-slf4j" % liquibaseLoggingVersion
  val slick            = "com.typesafe.slick" %% "slick"           % slickVersion

  /* ********************************************************************** */
  /*                          Testing Dependencies                          */
  /* ********************************************************************** */
  final val scalaTestVersion  = "3.0.5"
  final val scalaCheckVersion = "1.14.0"

  val scalaTest  = "org.scalatest"  %% "scalatest"  % scalaTestVersion
  val scalaCheck = "org.scalacheck" %% "scalacheck" % scalaCheckVersion

  /* Use like this: libraryDependencies <++= (scalaBinaryVersion) (scalaParser) */
  def scalaParser(scalaBinaryVersion: String): Seq[ModuleID] = scalaBinaryVersion match {
    case "2.10" => Seq.empty
    case _      => Seq("org.scala-lang.modules" %% "scala-parser-combinators" % scalaParserVersion % "optional")
  }

  def scalaXml(scalaBinaryVersion: String): Seq[ModuleID] = scalaBinaryVersion match {
    case "2.10" => Seq.empty
    case _      => Seq("org.scala-lang.modules" %% "scala-xml" % scalaXmlVersion % "optional")
  }

  /* ********************************************************************** */
  /*                                  Akka                                  */
  /* ********************************************************************** */
  final val akkaVersion        = "2.5.13"
  final val akkaHttpVersion    = "10.1.3"

  val akkaActor    = "com.typesafe.akka" %% "akka-actor"     % akkaVersion
  val akkaAgent    = "com.typesafe.akka" %% "akka-agent"     % akkaVersion
  val akkaRemote   = "com.typesafe.akka" %% "akka-remote"    % akkaVersion
  val akkaSlf4j    = "com.typesafe.akka" %% "akka-slf4j"     % akkaVersion
  val akkaStream   = "com.typesafe.akka" %% "akka-stream"    % akkaVersion
  val akkaHttpCore = "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion
  val akkaHttp     = "com.typesafe.akka" %% "akka-http"      % akkaHttpVersion

  /* ********************************************************************** */
  /*                                Helpers                                 */
  /* ********************************************************************** */
  private def noCL(m: ModuleID) = (
    m exclude("commons-logging", "commons-logging")
      exclude("commons-logging", "commons-logging-api")
  )
}
