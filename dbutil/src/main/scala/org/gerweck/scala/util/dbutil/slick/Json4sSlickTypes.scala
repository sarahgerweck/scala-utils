package org.gerweck.scala.util.dbutil.slick

import slick.driver.JdbcProfile

import org.json4s._
import org.json4s.native.JsonMethods._

/** Slick types for json4s.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
trait Json4sSlickTypes {
  protected val profile: JdbcProfile
  import profile.api._

  implicit lazy val jvalueType = MappedColumnType.base[JValue, String](v => compact(render(v)), parse(_))
}
