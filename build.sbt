import BasicSettings._
import Resolvers._
import Dependencies._
import PublishSettings._
import Helpers._

lazy val root: Project = (project in file ("."))
  .aggregate(macros, core, akka, dbutil)
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
  .enablePlugins(BasicSettings)
  .settings(Eclipse.settings: _*)
  .settings(falsePublishSettings: _*)
  .settings (
    name := "Gerweck Utils Macros",
    libraryDependencies += log4s,
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    resolvers += Resolver.sonatypeRepo("releases"),

    addCompilerPlugin(MetalsPlugin.semanticdbModule),
    addScalacOptions(),
    addJavacOptions(),

    publish := {},
    publishLocal := {},
    publishArtifact := false,

    exportJars := false,
    releaseProcess := Seq.empty
  )

lazy val core: Project = (project in file ("core"))
  .dependsOn(macros % "optional")
  .enablePlugins(ModuleSettings, SiteSettingsPlugin)
  .settings(
    name := "Gerweck Utils",

    addCompilerPlugin(MetalsPlugin.semanticdbModule),
    addScalacOptions(),
    addJavacOptions(),

    libraryDependencies ++= basicLogDeps,
    libraryDependencies ++= Seq(
      jclBridge % "optional",
      scalaCheck % "test",
      commonsIo % "optional",
      jodaTime % "optional",
      jodaConvert % "optional",
      spire.value % "provided,optional",
      commonsVfs % "optional"
    ),
    libraryDependencies += threeTen % "optional",
    libraryDependencies += scalaTest % "test",
    libraryDependencies += bouncyCastle % "optional",
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,

    libraryDependencies ++= scalaParser(scalaBinaryVersion.value),
    libraryDependencies ++= scalaXml(scalaBinaryVersion.value),

    resolvers += Resolver.sonatypeRepo("releases"),

    // include the macro classes and resources in the main jar
    mappings in (Compile, packageBin) ++= mappings.in(macros, Compile, packageBin).value,

    // include the macro sources in the main source jar
    mappings in (Compile, packageSrc) ++= mappings.in(macros, Compile, packageSrc).value,

    // Do not include macros as a dependency.
    pomPostProcess := excludePomDeps { (group, artifact) => (group == "org.gerweck.scala") && (artifact startsWith "gerweck-utils-macro") }
  )

lazy val akka: Project = (project in file ("akka"))
  .dependsOn(core)
  .enablePlugins(ModuleSettings, SiteSettingsPlugin)
  .settings(
    name := "Gerweck Utils Akka",

    addCompilerPlugin(MetalsPlugin.semanticdbModule),
    addScalacOptions(),
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
  .enablePlugins(ModuleSettings, SiteSettingsPlugin)
  .settings(
    name := "Gerweck Utils DB",

    addCompilerPlugin(MetalsPlugin.semanticdbModule),
    addScalacOptions(),
    addJavacOptions(),

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
