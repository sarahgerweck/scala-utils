package org.gerweck.scala.util.jdbc

import java.sql.{ Array => SqlArray, _ }
import java.io.{ InputStream, Reader }
import java.util.Calendar

import org.gerweck.scala.util.Support.{ support => nse }

/** A JDBC
  * `[[http://docs.oracle.com/javase/7/docs/api/java/sql/PreparedStatement.html java.sql.PreparedStatement]]`.
  * that throws an exception for all methods.
  *
  * This is useful as a base class for a very limited implementation. You probably don't want to
  * use this for a real driver, because you won't be able to easily tell what you have and haven't
  * implemented.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
trait ThrowingPreparedStatement extends ThrowingStatement with PreparedStatement {
  override def addBatch(): Unit = nse
  override def clearParameters(): Unit = nse
  override def execute(): Boolean = nse
  override def executeQuery(): ResultSet = nse
  override def executeUpdate(): Int = nse
  override def getMetaData(): ResultSetMetaData = nse
  override def getParameterMetaData(): ParameterMetaData = nse
  override def setArray(x$1: Int, x$2: SqlArray): Unit = nse
  override def setAsciiStream(x$1: Int, x$2: InputStream): Unit = nse
  override def setAsciiStream(x$1: Int, x$2: InputStream, x$3: Long): Unit = nse
  override def setAsciiStream(x$1: Int, x$2: InputStream, x$3: Int): Unit = nse
  override def setBigDecimal(x$1: Int, x$2: java.math.BigDecimal): Unit = nse
  override def setBinaryStream(x$1: Int, x$2: InputStream): Unit = nse
  override def setBinaryStream(x$1: Int, x$2: InputStream, x$3: Long): Unit = nse
  override def setBinaryStream(x$1: Int, x$2: InputStream, x$3: Int): Unit = nse
  override def setBlob(x$1: Int, x$2: InputStream): Unit = nse
  override def setBlob(x$1: Int, x$2: InputStream, x$3: Long): Unit = nse
  override def setBlob(x$1: Int, x$2: Blob): Unit = nse
  override def setBoolean(x$1: Int, x$2: Boolean): Unit = nse
  override def setByte(x$1: Int, x$2: Byte): Unit = nse
  override def setBytes(x$1: Int, x$2: Array[Byte]): Unit = nse
  override def setCharacterStream(x$1: Int, x$2: Reader): Unit = nse
  override def setCharacterStream(x$1: Int, x$2: Reader, x$3: Long): Unit = nse
  override def setCharacterStream(x$1: Int, x$2: Reader, x$3: Int): Unit = nse
  override def setClob(x$1: Int, x$2: Reader): Unit = nse
  override def setClob(x$1: Int, x$2: Reader, x$3: Long): Unit = nse
  override def setClob(x$1: Int, x$2: Clob): Unit = nse
  override def setDate(x$1: Int, x$2: Date, x$3: Calendar): Unit = nse
  override def setDate(x$1: Int, x$2: Date): Unit = nse
  override def setDouble(x$1: Int, x$2: Double): Unit = nse
  override def setFloat(x$1: Int, x$2: Float): Unit = nse
  override def setInt(x$1: Int, x$2: Int): Unit = nse
  override def setLong(x$1: Int, x$2: Long): Unit = nse
  override def setNCharacterStream(x$1: Int, x$2: Reader): Unit = nse
  override def setNCharacterStream(x$1: Int, x$2: Reader, x$3: Long): Unit = nse
  override def setNClob(x$1: Int, x$2: Reader): Unit = nse
  override def setNClob(x$1: Int, x$2: Reader, x$3: Long): Unit = nse
  override def setNClob(x$1: Int, x$2: NClob): Unit = nse
  override def setNString(x$1: Int, x$2: String): Unit = nse
  override def setNull(x$1: Int, x$2: Int, x$3: String): Unit = nse
  override def setNull(x$1: Int, x$2: Int): Unit = nse
  override def setObject(x$1: Int, x$2: Any, x$3: Int, x$4: Int): Unit = nse
  override def setObject(x$1: Int, x$2: Any): Unit = nse
  override def setObject(x$1: Int, x$2: Any, x$3: Int): Unit = nse
  override def setRef(x$1: Int, x$2: Ref): Unit = nse
  override def setRowId(x$1: Int, x$2: RowId): Unit = nse
  override def setSQLXML(x$1: Int, x$2: SQLXML): Unit = nse
  override def setShort(x$1: Int, x$2: Short): Unit = nse
  override def setString(x$1: Int, x$2: String): Unit = nse
  override def setTime(x$1: Int, x$2: Time, x$3: Calendar): Unit = nse
  override def setTime(x$1: Int, x$2: Time): Unit = nse
  override def setTimestamp(x$1: Int, x$2: Timestamp, x$3: Calendar): Unit = nse
  override def setTimestamp(x$1: Int, x$2: Timestamp): Unit = nse
  override def setURL(x$1: Int, x$2: java.net.URL): Unit = nse
  override def setUnicodeStream(x$1: Int, x$2: InputStream, x$3: Int): Unit = nse
}
