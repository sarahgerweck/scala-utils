import sbt._
import Keys._

import sbtrelease.ReleasePlugin.autoImport._
import com.typesafe.sbteclipse.plugin.EclipsePlugin._

import Helpers._

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
    .aggregate(macros, core, java6, twitter, akka, dbutil)
    .moduleSettings()
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
    .moduleSettings()
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
    .moduleSettings()
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
    .moduleSettings()
    .settings(
      name := "Gerweck Utils Twitter",
      libraryDependencies ++= basicLogDeps,
      libraryDependencies += twitterUtil % "optional"
    )

  lazy val akka: Project = (project in file ("akka"))
    .dependsOn(core)
    .moduleSettings()
    .settings(
      name := "Gerweck Utils Akka",

      scalacOptions ++= scalacOpts(SVer(scalaBinaryVersion.value), true, false),
      javacOptions ++= javacOpts(true),

      /* Logging */
      libraryDependencies ++= basicLogDeps,
      /* Akka */
      libraryDependencies ++= Seq (
        akkaActor,
        akkaStream
      )
    )

  lazy val dbutil: Project = (project in file ("dbutil"))
    .dependsOn(core)
    .moduleSettings()
    .settings(
      name := "Gerweck Utils DB",

      scalacOptions ++= scalacOpts(SVer(scalaBinaryVersion.value), true),
      javacOptions ++= javacOpts(true),

      /* Slick dependencies */
      libraryDependencies ++= Seq (
        slick
      ),
      /* Optional mappings */
      libraryDependencies ++= Seq (
        json4sNative % "optional",
        akkaHttpCore % "optional"
      )
    )

  private implicit class ProjectHelper2(p: Project) {
    def moduleSettings() = {
      p .commonSettings
        .settings(Eclipse.settings: _*)
        .settings(EclipseKeys.skipParents in ThisBuild := false)
        .settings(publishSettings: _*)
        .settings(Release.settings: _*)
    }
  }
}
