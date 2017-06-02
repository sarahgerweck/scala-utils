import BuildSettings._
import Resolvers._
import Dependencies._
import PublishSettings._
import Helpers._

lazy val root: Project = (project in file ("."))
  .aggregate(macros, core, java6, twitter, akka, dbutil)
  .enablePlugins(ModuleSettings)
  .settings (
    name := "Gerweck Utils Root",

    publish := {},
    publishLocal := {},
    publishArtifact := false,

    exportJars := false,

    skip in Compile := true,
    skip in Test := true
  )

lazy val macros = (project in file ("macro"))
  .enablePlugins(CommonSettings)
  .settings(Eclipse.settings: _*)
  .settings(falsePublishSettings: _*)
  .settings (
    name := "Gerweck Utils Macros",
    libraryDependencies += log4s,
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    resolvers += Resolver.sonatypeRepo("releases"),

    addScalacOptions(),
    addJavacOptions(),

    scalaSource in Compile := {
      val mainDir = baseDirectory.value / "src" / "main"
      scalaBinaryVersion.value match {
        case "2.10" => mainDir / "scala-2.10"
        case _      => mainDir / "scala-2.11"
      }
    },

    publish := {},
    publishLocal := {},
    publishArtifact := false,

    exportJars := false,
    releaseProcess := Seq.empty
  )

lazy val core: Project = (project in file ("core"))
  .dependsOn(macros % "optional")
  .enablePlugins(ModuleSettings)
  .settings(
    name := "Gerweck Utils",

    addScalacOptions(),
    addJavacOptions(),

    libraryDependencies ++= utilsDeps,
    libraryDependencies += threeTen % "optional",
    libraryDependencies += scalaTest % "test",
    libraryDependencies += bouncyCastle % "optional",
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,

    libraryDependencies ++= scalaParser(scalaBinaryVersion.value),
    libraryDependencies ++= scalaXml(scalaBinaryVersion.value),

    resolvers += Resolver.sonatypeRepo("releases"),

    unmanagedSourceDirectories in Compile += {
      val srcBase = baseDirectory.value / "src" / "main"
      scalaBinaryVersion.value match {
        case "2.10" => srcBase / "scala-2.10"
        case _      => srcBase / "scala-2.11"
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
  .enablePlugins(ModuleSettings)
  .settings(
    name := "Gerweck Utils (Java 6)",
    normalizedName := "gerweck-utils-java6",

    addScalacOptions(),
    addJavacOptions(),

    libraryDependencies ++= utilsDeps,
    libraryDependencies += threeTen,
    libraryDependencies += scalaTest % "test",
    libraryDependencies += bouncyCastle % "optional",
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,

    libraryDependencies ++= scalaParser(scalaBinaryVersion.value),
    libraryDependencies ++= scalaXml(scalaBinaryVersion.value),

    resolvers += Resolver.sonatypeRepo("releases"),

    unmanagedSourceDirectories in Compile += {
      val mainDir = baseDirectory.value / ".." / "core" / "src" / "main"
      scalaBinaryVersion.value match {
        case "2.10" => mainDir / "scala-2.10"
        case _      => mainDir / "scala-2.11"
      }
    },
    scalaSource in Compile := baseDirectory.value / ".." / "core" / "src" / "main" / "scala",
    scalaSource in Test := baseDirectory.value / ".." / "core" / "src" / "test" / "scala",
    unmanagedSourceDirectories in Compile += baseDirectory.value / ".." / "core" / "src" / "main" / "scala-java6",

    publish := {
      scalaBinaryVersion.value match {
        case "2.12" => ()
        case _      => publish.value
      }
    },
    publishLocal := {
      scalaBinaryVersion.value match {
        case "2.12" => ()
        case _      => publishLocal.value
      }
    },
    publishArtifact := {
      scalaBinaryVersion.value match {
        case "2.12" => false
        case _      => publishArtifact.value
      }
    },
    releaseProcess := {
      scalaBinaryVersion.value match {
        case "2.12" => Seq.empty
        case _      => releaseProcess.value
      }
    },

    // include the macro classes and resources in the main jar
    mappings in (Compile, packageBin) ++= mappings.in(macros, Compile, packageBin).value,

    // include the macro sources in the main source jar
    mappings in (Compile, packageSrc) ++= mappings.in(macros, Compile, packageSrc).value,

    // Do not include macros as a dependency.
    pomPostProcess := excludePomDeps { (group, artifact) => (group == "org.gerweck.scala") && (artifact startsWith "gerweck-utils-macro") }
  )

lazy val twitter = (project in file ("twitter"))
  .enablePlugins(ModuleSettings)
  .settings(
    name := "Gerweck Utils Twitter",
    libraryDependencies ++= basicLogDeps,
    libraryDependencies += twitterUtil % "optional"
  )

lazy val akka: Project = (project in file ("akka"))
  .dependsOn(core)
  .enablePlugins(ModuleSettings)
  .settings(
    name := "Gerweck Utils Akka",

    basicScalacOptions,
    scalacOptions ++= {
      sbt.CrossVersion.partialVersion(scalaBinaryVersion.value) match {
        case Some((2, x)) if x >= 12 =>
          Seq("-opt:l:project")
        case _ =>
          Seq.empty
      }
    },
    addJavacOptions(),

    /* Logging */
    libraryDependencies ++= basicLogDeps,
    /* Akka */
    libraryDependencies ++= Seq (
      akkaActor,
      akkaStream
    ),
    /* Testing */
    libraryDependencies += scalaTest % "test",
    libraryDependencies += scalaCheck % "test",
    /* BouncyCastle is optional for the streaming hash. */
    libraryDependencies += bouncyCastle % "test"
  )

lazy val dbutil: Project = (project in file ("dbutil"))
  .dependsOn(core)
  .enablePlugins(ModuleSettings)
  .settings(
    name := "Gerweck Utils DB",

    addScalacOptions(),
    addJavacOptions(),

    /* Slick has some changes in 3.2, which we only use in Scala 2.12 */
    unmanagedSourceDirectories in Compile += {
      val mainDir = baseDirectory.value / "src" / "main"
      scalaBinaryVersion.value match {
        case "2.11" => mainDir / "scala-2.11"
        case "2.12" => mainDir / "scala-2.12"
      }
    },

    /* Slick dependencies */
    libraryDependencies ++= Seq (
      slick
    ),
    /* Optional mappings */
    libraryDependencies ++= Seq (
      json4sNative,
      akkaHttpCore
    ).map(_ % "optional"),
    /* Optional liquibase support */
    libraryDependencies ++= Seq (
      liquibase,
      liquibaseLogging
    ).map(_ % "optional")
  )

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
