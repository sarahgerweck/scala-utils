package org.gerweck.scala.util.jdbc

import java.io.{InputStream, Reader}
import java.net.URL
import java.sql
import java.sql.{Blob, Clob, Date, NClob, ParameterMetaData, PreparedStatement, Ref, ResultSet, ResultSetMetaData, RowId, SQLType, SQLXML, Time, Timestamp}
import java.util.Calendar

/** A wrapper around a JDBC
  * `[[http://docs.oracle.com/javase/7/docs/api/java/sql/PreparedStatement.html java.sql.PreparedStatement]]`.
  *
  * This is useful to allow you to override or wrap certain methods: you can
  * use this as a base class and override just the methods you care about.
  * Everything else will delegate to the underlying connection.
  *
  * @author Rouzbeh Safaie <rouzbeh.safaie@gmail.com>
  */
class WrappedPreparedStatement(override val inner: PreparedStatement) extends WrappedStatement(inner) with PreparedStatement with WrapperWrapping[PreparedStatement] {
  def executeQuery(): ResultSet = inner.executeQuery()
  def executeUpdate(): Int = inner.executeUpdate()
  def setNull(x$1: Int, x$2: Int): Unit = inner.setNull(x$1, x$2)
  def setBoolean(x$1: Int, x$2: Boolean): Unit = inner.setBoolean(x$1, x$2)
  def setByte(x$1: Int, x$2: Byte): Unit = inner.setByte(x$1, x$2)
  def setShort(x$1: Int, x$2: Short): Unit = inner.setShort(x$1, x$2)
  def setInt(x$1: Int, x$2: Int): Unit = inner.setInt(x$1, x$2)
  def setLong(x$1: Int, x$2: Long): Unit = inner.setLong(x$1, x$2)
  def setFloat(x$1: Int, x$2: Float): Unit = inner.setFloat(x$1, x$2)
  def setDouble(x$1: Int, x$2: Double): Unit = inner.setDouble(x$1, x$2)
  def setBigDecimal(x$1: Int, x$2: java.math.BigDecimal): Unit = inner.setBigDecimal(x$1, x$2)
  def setString(x$1: Int, x$2: String): Unit = inner.setString(x$1, x$2)
  def setBytes(x$1: Int, x$2: Array[Byte]): Unit = inner.setBytes(x$1, x$2)
  def setDate(x$1: Int, x$2: Date): Unit = inner.setDate(x$1, x$2)
  def setTime(x$1: Int, x$2: Time): Unit = inner.setTime(x$1, x$2)
  def setTimestamp(x$1: Int, x$2: Timestamp): Unit = inner.setTimestamp(x$1, x$2)
  def setAsciiStream(x$1: Int, x$2: InputStream, x$3: Int): Unit = inner.setAsciiStream(x$1, x$2, x$3)
  @deprecated("1.0", "Deprecated by Java.")
  def setUnicodeStream(x$1: Int, x$2: InputStream, x$3: Int): Unit = inner.setUnicodeStream(x$1, x$2, x$3)
  def setBinaryStream(x$1: Int, x$2: InputStream, x$3: Int): Unit = inner.setBinaryStream(x$1, x$2, x$3)
  def clearParameters(): Unit = inner.clearParameters()
  def setObject(x$1: Int, x$2: scala.Any, x$3: Int): Unit = inner.setObject(x$1, x$2, x$3)
  def setObject(x$1: Int, x$2: scala.Any): Unit = inner.setObject(x$1, x$2)
  def execute(): Boolean = inner.execute()
  def addBatch(): Unit = inner.addBatch()
  def setCharacterStream(x$1: Int, x$2: Reader, x$3: Int): Unit = inner.setCharacterStream(x$1, x$2, x$3)
  def setRef(x$1: Int, x$2: Ref): Unit = inner.setRef(x$1, x$2)
  def setBlob(x$1: Int, x$2: Blob): Unit = inner.setBlob(x$1, x$2)
  def setClob(x$1: Int, x$2: Clob): Unit = inner.setClob(x$1, x$2)
  def setArray(x$1: Int, x$2: sql.Array): Unit = inner.setArray(x$1, x$2)
  def getMetaData(): ResultSetMetaData = inner.getMetaData()
  def setDate(x$1: Int, x$2: Date, x$3: Calendar): Unit = inner.setDate(x$1, x$2, x$3)
  def setTime(x$1: Int, x$2: Time, x$3: Calendar): Unit = inner.setTime(x$1, x$2, x$3)
  def setTimestamp(x$1: Int, x$2: Timestamp, x$3: Calendar): Unit = inner.setTimestamp(x$1, x$2, x$3)
  def setNull(x$1: Int, x$2: Int, x$3: String): Unit = inner.setNull(x$1, x$2, x$3)
  def setURL(x$1: Int, x$2: URL): Unit = inner.setURL(x$1, x$2)
  def getParameterMetaData(): ParameterMetaData = inner.getParameterMetaData()
  def setRowId(x$1: Int, x$2: RowId): Unit = inner.setRowId(x$1, x$2)
  def setNString(x$1: Int, x$2: String): Unit = inner.setNString(x$1, x$2)
  def setNCharacterStream(x$1: Int, x$2: Reader, x$3: Long): Unit = inner.setNCharacterStream(x$1, x$2, x$3)
  def setNClob(x$1: Int, x$2: NClob): Unit = inner.setNClob(x$1, x$2)
  def setClob(x$1: Int, x$2: Reader, x$3: Long): Unit = inner.setClob(x$1, x$2, x$3)
  def setBlob(x$1: Int, x$2: InputStream, x$3: Long): Unit = inner.setBlob(x$1, x$2, x$3)
  def setNClob(x$1: Int, x$2: Reader, x$3: Long): Unit = inner.setNClob(x$1, x$2, x$3)
  def setSQLXML(x$1: Int, x$2: SQLXML): Unit = inner.setSQLXML(x$1, x$2)
  def setObject(x$1: Int, x$2: scala.Any, x$3: Int, x$4: Int): Unit = inner.setObject(x$1, x$2, x$3, x$4)
  def setAsciiStream(x$1: Int, x$2: InputStream, x$3: Long): Unit = inner.setAsciiStream(x$1, x$2, x$3)
  def setBinaryStream(x$1: Int, x$2: InputStream, x$3: Long): Unit = inner.setBinaryStream(x$1, x$2, x$3)
  def setCharacterStream(x$1: Int, x$2: Reader, x$3: Long): Unit = inner.setCharacterStream(x$1, x$2, x$3)
  def setAsciiStream(x$1: Int, x$2: InputStream): Unit = inner.setAsciiStream(x$1, x$2)
  def setBinaryStream(x$1: Int, x$2: InputStream): Unit = inner.setBinaryStream(x$1, x$2)
  def setCharacterStream(x$1: Int, x$2: Reader): Unit = inner.setCharacterStream(x$1, x$2)
  def setNCharacterStream(x$1: Int, x$2: Reader): Unit = inner.setNCharacterStream(x$1, x$2)
  def setClob(x$1: Int, x$2: Reader): Unit = inner.setClob(x$1, x$2)
  def setBlob(x$1: Int, x$2: InputStream): Unit = inner.setBlob(x$1, x$2)
  def setNClob(x$1: Int, x$2: Reader): Unit = inner.setNClob(x$1, x$2)
  override def setObject(x$1: Int, x$2: scala.Any, x$3: SQLType, x$4: Int): Unit = inner.setObject(x$1, x$2, x$3, x$4)
  override def setObject(x$1: Int, x$2: scala.Any, x$3: SQLType): Unit = inner.setObject(x$1, x$2, x$3)
  override def executeLargeUpdate(): Long = inner.executeLargeUpdate()

}
