package org.gerweck.scala.util.jdbc

import java.io.{ InputStream, Reader }
import java.math.BigDecimal
import java.net.URL
import java.sql.{ Array => SqlArray, _ }
import java.util.{ Calendar, Map => JMap }

import org.gerweck.scala.util.Support.{ support => nse }

/** A JDBC
  * `[[http://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html java.sql.ResultSet]]`.
  * that throws an exception for all methods.
  *
  * This is useful as a base class for a very limited implementation. You probably don't want to
  * use this for a real driver, because you won't be able to easily tell what you have and haven't
  * implemented.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
trait ThrowingResultSet extends ResultSet with ThrowingWrapper {
  override def absolute(x$1: Int): Boolean = nse
  override def afterLast(): Unit = nse
  override def beforeFirst(): Unit = nse
  override def cancelRowUpdates(): Unit = nse
  override def clearWarnings(): Unit = nse
  override def close(): Unit = nse
  override def deleteRow(): Unit = nse
  override def findColumn(x$1: String): Int = nse
  override def first(): Boolean = nse
  override def getArray(x$1: String): SqlArray = nse
  override def getArray(x$1: Int): SqlArray = nse
  override def getAsciiStream(x$1: String): InputStream = nse
  override def getAsciiStream(x$1: Int): InputStream = nse
  override def getBigDecimal(x$1: String): BigDecimal = nse
  override def getBigDecimal(x$1: Int): BigDecimal = nse
  @deprecated("1.0", "Deprecated by Java.")
  override def getBigDecimal(x$1: String, x$2: Int): BigDecimal = nse
  @deprecated("1.0", "Deprecated by Java.")
  override def getBigDecimal(x$1: Int, x$2: Int): BigDecimal = nse
  override def getBinaryStream(x$1: String): InputStream = nse
  override def getBinaryStream(x$1: Int): InputStream = nse
  override def getBlob(x$1: String): Blob = nse
  override def getBlob(x$1: Int): Blob = nse
  override def getBoolean(x$1: String): Boolean = nse
  override def getBoolean(x$1: Int): Boolean = nse
  override def getByte(x$1: String): Byte = nse
  override def getByte(x$1: Int): Byte = nse
  override def getBytes(x$1: String): Array[Byte] = nse
  override def getBytes(x$1: Int): Array[Byte] = nse
  override def getCharacterStream(x$1: String): Reader = nse
  override def getCharacterStream(x$1: Int): Reader = nse
  override def getClob(x$1: String): Clob = nse
  override def getClob(x$1: Int): Clob = nse
  override def getConcurrency(): Int = nse
  override def getCursorName(): String = nse
  override def getDate(x$1: String, x$2: Calendar): Date = nse
  override def getDate(x$1: Int, x$2: Calendar): Date = nse
  override def getDate(x$1: String): Date = nse
  override def getDate(x$1: Int): Date = nse
  override def getDouble(x$1: String): Double = nse
  override def getDouble(x$1: Int): Double = nse
  override def getFetchDirection(): Int = nse
  override def getFetchSize(): Int = nse
  override def getFloat(x$1: String): Float = nse
  override def getFloat(x$1: Int): Float = nse
  override def getHoldability(): Int = nse
  override def getInt(x$1: String): Int = nse
  override def getInt(x$1: Int): Int = nse
  override def getLong(x$1: String): Long = nse
  override def getLong(x$1: Int): Long = nse
  override def getMetaData(): ResultSetMetaData = nse
  override def getNCharacterStream(x$1: String): Reader = nse
  override def getNCharacterStream(x$1: Int): Reader = nse
  override def getNClob(x$1: String): NClob = nse
  override def getNClob(x$1: Int): NClob = nse
  override def getNString(x$1: String): String = nse
  override def getNString(x$1: Int): String = nse
  override def getObject[T](x$1: String, x$2: Class[T]): T = nse
  override def getObject[T](x$1: Int, x$2: Class[T]): T = nse
  override def getObject(x$1: String, x$2: JMap[String, Class[_]]): Object = nse
  override def getObject(x$1: Int, x$2: JMap[String, Class[_]]): Object = nse
  override def getObject(x$1: String): Object = nse
  override def getObject(x$1: Int): Object = nse
  override def getRef(x$1: String): Ref = nse
  override def getRef(x$1: Int): Ref = nse
  override def getRow(): Int = nse
  override def getRowId(x$1: String): RowId = nse
  override def getRowId(x$1: Int): RowId = nse
  override def getSQLXML(x$1: String): SQLXML = nse
  override def getSQLXML(x$1: Int): SQLXML = nse
  override def getShort(x$1: String): Short = nse
  override def getShort(x$1: Int): Short = nse
  override def getStatement(): Statement = nse
  override def getString(x$1: String): String = nse
  override def getString(x$1: Int): String = nse
  override def getTime(x$1: String, x$2: Calendar): Time = nse
  override def getTime(x$1: Int, x$2: Calendar): Time = nse
  override def getTime(x$1: String): Time = nse
  override def getTime(x$1: Int): Time = nse
  override def getTimestamp(x$1: String, x$2: Calendar): Timestamp = nse
  override def getTimestamp(x$1: Int, x$2: Calendar): Timestamp = nse
  override def getTimestamp(x$1: String): Timestamp = nse
  override def getTimestamp(x$1: Int): Timestamp = nse
  override def getType(): Int = nse
  override def getURL(x$1: String): java.net.URL = nse
  override def getURL(x$1: Int): java.net.URL = nse
  @deprecated("1.0", "Deprecated by Java.")
  override def getUnicodeStream(x$1: String): InputStream = nse
  @deprecated("1.0", "Deprecated by Java.")
  override def getUnicodeStream(x$1: Int): InputStream = nse
  override def getWarnings(): SQLWarning = nse
  override def insertRow(): Unit = nse
  override def isAfterLast(): Boolean = nse
  override def isBeforeFirst(): Boolean = nse
  override def isClosed(): Boolean = nse
  override def isFirst(): Boolean = nse
  override def isLast(): Boolean = nse
  override def last(): Boolean = nse
  override def moveToCurrentRow(): Unit = nse
  override def moveToInsertRow(): Unit = nse
  override def next(): Boolean = nse
  override def previous(): Boolean = nse
  override def refreshRow(): Unit = nse
  override def relative(x$1: Int): Boolean = nse
  override def rowDeleted(): Boolean = nse
  override def rowInserted(): Boolean = nse
  override def rowUpdated(): Boolean = nse
  override def setFetchDirection(x$1: Int): Unit = nse
  override def setFetchSize(x$1: Int): Unit = nse
  override def updateArray(x$1: String, x$2: SqlArray): Unit = nse
  override def updateArray(x$1: Int, x$2: SqlArray): Unit = nse
  override def updateAsciiStream(x$1: String, x$2: InputStream): Unit = nse
  override def updateAsciiStream(x$1: Int, x$2: InputStream): Unit = nse
  override def updateAsciiStream(x$1: String, x$2: InputStream, x$3: Long): Unit = nse
  override def updateAsciiStream(x$1: Int, x$2: InputStream, x$3: Long): Unit = nse
  override def updateAsciiStream(x$1: String, x$2: InputStream, x$3: Int): Unit = nse
  override def updateAsciiStream(x$1: Int, x$2: InputStream, x$3: Int): Unit = nse
  override def updateBigDecimal(x$1: String, x$2: BigDecimal): Unit = nse
  override def updateBigDecimal(x$1: Int, x$2: BigDecimal): Unit = nse
  override def updateBinaryStream(x$1: String, x$2: InputStream): Unit = nse
  override def updateBinaryStream(x$1: Int, x$2: InputStream): Unit = nse
  override def updateBinaryStream(x$1: String, x$2: InputStream, x$3: Long): Unit = nse
  override def updateBinaryStream(x$1: Int, x$2: InputStream, x$3: Long): Unit = nse
  override def updateBinaryStream(x$1: String, x$2: InputStream, x$3: Int): Unit = nse
  override def updateBinaryStream(x$1: Int, x$2: InputStream, x$3: Int): Unit = nse
  override def updateBlob(x$1: String, x$2: InputStream): Unit = nse
  override def updateBlob(x$1: Int, x$2: InputStream): Unit = nse
  override def updateBlob(x$1: String, x$2: InputStream, x$3: Long): Unit = nse
  override def updateBlob(x$1: Int, x$2: InputStream, x$3: Long): Unit = nse
  override def updateBlob(x$1: String, x$2: Blob): Unit = nse
  override def updateBlob(x$1: Int, x$2: Blob): Unit = nse
  override def updateBoolean(x$1: String, x$2: Boolean): Unit = nse
  override def updateBoolean(x$1: Int, x$2: Boolean): Unit = nse
  override def updateByte(x$1: String, x$2: Byte): Unit = nse
  override def updateByte(x$1: Int, x$2: Byte): Unit = nse
  override def updateBytes(x$1: String, x$2: Array[Byte]): Unit = nse
  override def updateBytes(x$1: Int, x$2: Array[Byte]): Unit = nse
  override def updateCharacterStream(x$1: String, x$2: Reader): Unit = nse
  override def updateCharacterStream(x$1: Int, x$2: Reader): Unit = nse
  override def updateCharacterStream(x$1: String, x$2: Reader, x$3: Long): Unit = nse
  override def updateCharacterStream(x$1: Int, x$2: Reader, x$3: Long): Unit = nse
  override def updateCharacterStream(x$1: String, x$2: Reader, x$3: Int): Unit = nse
  override def updateCharacterStream(x$1: Int, x$2: Reader, x$3: Int): Unit = nse
  override def updateClob(x$1: String, x$2: Reader): Unit = nse
  override def updateClob(x$1: Int, x$2: Reader): Unit = nse
  override def updateClob(x$1: String, x$2: Reader, x$3: Long): Unit = nse
  override def updateClob(x$1: Int, x$2: Reader, x$3: Long): Unit = nse
  override def updateClob(x$1: String, x$2: Clob): Unit = nse
  override def updateClob(x$1: Int, x$2: Clob): Unit = nse
  override def updateDate(x$1: String, x$2: Date): Unit = nse
  override def updateDate(x$1: Int, x$2: Date): Unit = nse
  override def updateDouble(x$1: String, x$2: Double): Unit = nse
  override def updateDouble(x$1: Int, x$2: Double): Unit = nse
  override def updateFloat(x$1: String, x$2: Float): Unit = nse
  override def updateFloat(x$1: Int, x$2: Float): Unit = nse
  override def updateInt(x$1: String, x$2: Int): Unit = nse
  override def updateInt(x$1: Int, x$2: Int): Unit = nse
  override def updateLong(x$1: String, x$2: Long): Unit = nse
  override def updateLong(x$1: Int, x$2: Long): Unit = nse
  override def updateNCharacterStream(x$1: String, x$2: Reader): Unit = nse
  override def updateNCharacterStream(x$1: Int, x$2: Reader): Unit = nse
  override def updateNCharacterStream(x$1: String, x$2: Reader, x$3: Long): Unit = nse
  override def updateNCharacterStream(x$1: Int, x$2: Reader, x$3: Long): Unit = nse
  override def updateNClob(x$1: String, x$2: Reader): Unit = nse
  override def updateNClob(x$1: Int, x$2: Reader): Unit = nse
  override def updateNClob(x$1: String, x$2: Reader, x$3: Long): Unit = nse
  override def updateNClob(x$1: Int, x$2: Reader, x$3: Long): Unit = nse
  override def updateNClob(x$1: String, x$2: NClob): Unit = nse
  override def updateNClob(x$1: Int, x$2: NClob): Unit = nse
  override def updateNString(x$1: String, x$2: String): Unit = nse
  override def updateNString(x$1: Int, x$2: String): Unit = nse
  override def updateNull(x$1: String): Unit = nse
  override def updateNull(x$1: Int): Unit = nse
  override def updateObject(x$1: String, x$2: Any): Unit = nse
  override def updateObject(x$1: String, x$2: Any, x$3: Int): Unit = nse
  override def updateObject(x$1: Int, x$2: Any): Unit = nse
  override def updateObject(x$1: Int, x$2: Any, x$3: Int): Unit = nse
  override def updateRef(x$1: String, x$2: Ref): Unit = nse
  override def updateRef(x$1: Int, x$2: Ref): Unit = nse
  override def updateRow(): Unit = nse
  override def updateRowId(x$1: String, x$2: RowId): Unit = nse
  override def updateRowId(x$1: Int, x$2: RowId): Unit = nse
  override def updateSQLXML(x$1: String, x$2: SQLXML): Unit = nse
  override def updateSQLXML(x$1: Int, x$2: SQLXML): Unit = nse
  override def updateShort(x$1: String, x$2: Short): Unit = nse
  override def updateShort(x$1: Int, x$2: Short): Unit = nse
  override def updateString(x$1: String, x$2: String): Unit = nse
  override def updateString(x$1: Int, x$2: String): Unit = nse
  override def updateTime(x$1: String, x$2: Time): Unit = nse
  override def updateTime(x$1: Int, x$2: Time): Unit = nse
  override def updateTimestamp(x$1: String, x$2: Timestamp): Unit = nse
  override def updateTimestamp(x$1: Int, x$2: Timestamp): Unit = nse
  override def wasNull(): Boolean = nse
}
