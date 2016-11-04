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

    scalacOptions ++= scalacOpts(SVer(scalaBinaryVersion.value), true),
    javacOptions ++= javacOpts(true),

    libraryDependencies ++= utilsDeps,
    libraryDependencies += threeTen % "optional",
    libraryDependencies += scalaTest(scalaBinaryVersion.value) % "test",
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

    scalacOptions ++= scalacOpts(SVer(scalaBinaryVersion.value), false),
    javacOptions ++= javacOpts(false),

    libraryDependencies ++= utilsDeps,
    libraryDependencies += threeTen,
    libraryDependencies += scalaTest(scalaBinaryVersion.value) % "test",
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
  .enablePlugins(ModuleSettings)
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
