import sbt._

import com.typesafe.sbteclipse.plugin.EclipsePlugin._

object ModuleSettings extends AutoPlugin {
  override def requires = CommonSettings

  override lazy val projectSettings = (
    Eclipse.settings ++
    Seq(EclipseKeys.skipParents in ThisBuild := false) ++
    PublishSettings.publishSettings ++
    Release.settings
  )
}
