import sbt._

object CommonSettings extends AutoPlugin {
  override def requires = SiteSettingsPlugin

  override lazy val projectSettings =
    BuildSettings.buildSettings
}
