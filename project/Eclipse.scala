import sbt._

import com.typesafe.sbteclipse.plugin.EclipsePlugin._

object Eclipse {
  lazy val settings = Seq (
    EclipseKeys.createSrc            := EclipseCreateSrc.Default,
    EclipseKeys.projectFlavor        := EclipseProjectFlavor.ScalaIDE,
    EclipseKeys.executionEnvironment := Some(EclipseExecutionEnvironment.JavaSE18),
    EclipseKeys.withSource           := true,
    EclipseKeys.skipParents          := false
  )
}
