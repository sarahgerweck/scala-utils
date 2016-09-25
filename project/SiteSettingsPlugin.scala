import sbt._
import Keys._

import com.typesafe.sbt.site._

object SiteSettingsPlugin extends AutoPlugin {
  override def requires = SiteScaladocPlugin

  override lazy val projectSettings = Seq(
     scalacOptions in (Compile,doc) ++= Seq(
       "-groups",
       "-implicits",
       "-diagrams",
       "-sourcepath", (baseDirectory in ThisBuild).value.getAbsolutePath,
       "-doc-source-url", "https://github.com/sarahgerweck/scala-utils/blob/master€{FILE_PATH}.scala"
     )
  )
}
