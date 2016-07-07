package org.gerweck.scala.util.dbutil.slick

import _root_.slick.driver.JdbcProfile

import akka.http.scaladsl.model.Uri

/** Slick type mappers for Akka
  *
  * @author Sarah Gerweck <sarah@atscale.com>
  */
trait AkkaSlickTypes {
  protected val profile: JdbcProfile
  import profile.api._

  implicit lazy val akkaUriType = MappedColumnType.base[Uri, String]({_.toString}, {Uri(_)})
}
