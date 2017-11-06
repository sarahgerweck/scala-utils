/** Basic metadata about the project that gets pulled into the build */
trait ProjectSettings
    extends SettingTemplate
    with SettingTemplate.ApacheLicensed
    with SettingTemplate.GithubProject {
  override final val buildOrganization     = "org.gerweck.scala"
  override final val buildOrganizationName = "Sarah Gerweck"
  override final val projectDescription    = "General utilies for Scala applications"
  override final val projectStartYear      = 2012

  override final val githubOrganization    = "sarahgerweck"
  override final val githubProject         = "scala-utils"

  override final val buildScalaVersion     = "2.12.4"
  override final val extraScalaVersions    = Seq("2.11.11")
  override final val minimumJavaVersion    = "1.6"
  override final val defaultOptimize       = true
  override final val defaultOptimizeGlobal = false
  override final val inlinePatterns        = Seq("!akka.**", "!slick.**")
  override final val defaultWarnUnused     = false
  override final val defaultWarnImport     = true
  override final val defaultWarnInline     = true
  override final val autoAddCompileOptions = false

  override final val parallelBuild         = true
  override final val cachedResolution      = true
  override final val sonatypeResolver      = true

  override final val defaultNewBackend     = false

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
