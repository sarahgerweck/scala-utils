package org.gerweck.scala.util.jdbc

import java.sql.ResultSetMetaData

/** A wrapper around a JDBC
  * `[[http://docs.oracle.com/javase/7/docs/api/java/sql/ResultSetMetaData.html java.sql.ResultSetMetaData]]`.
  *
  * This is useful to allow you to override or wrap certain methods: you can
  * use this as a base class and override just the methods you care about.
  * Everything else will delegate to the underlying connection.
  *
  * @author Rouzbeh Safaie <rouzbeh.safaie@gmail.com>
  */
class WrappedResultSetMetaData(val inner: ResultSetMetaData) extends ResultSetMetaData with WrapperWrapping[ResultSetMetaData] {

  def getColumnCount(): Int = inner.getColumnCount()
  def isAutoIncrement(x$1: Int): Boolean = inner.isAutoIncrement(x$1)
  def isCaseSensitive(x$1: Int): Boolean = inner.isCaseSensitive(x$1)
  def isSearchable(x$1: Int): Boolean = inner.isSearchable(x$1)
  def isCurrency(x$1: Int): Boolean = inner.isCurrency(x$1)
  def isNullable(x$1: Int): Int = inner.isNullable(x$1)
  def isSigned(x$1: Int): Boolean = inner.isSigned(x$1)
  def getColumnDisplaySize(x$1: Int): Int = inner.getColumnDisplaySize(x$1)
  def getColumnLabel(x$1: Int): String = inner.getColumnLabel(x$1)
  def getColumnName(x$1: Int): String = inner.getColumnName(x$1)
  def getSchemaName(x$1: Int): String = inner.getSchemaName(x$1)
  def getPrecision(x$1: Int): Int = inner.getPrecision(x$1)
  def getScale(x$1: Int): Int = inner.getScale(x$1)
  def getTableName(x$1: Int): String = inner.getTableName(x$1)
  def getCatalogName(x$1: Int): String = inner.getCatalogName(x$1)
  def getColumnType(x$1: Int): Int = inner.getColumnType(x$1)
  def getColumnTypeName(x$1: Int): String = inner.getColumnTypeName(x$1)
  def isReadOnly(x$1: Int): Boolean = inner.isReadOnly(x$1)
  def isWritable(x$1: Int): Boolean = inner.isWritable(x$1)
  def isDefinitelyWritable(x$1: Int): Boolean = inner.isDefinitelyWritable(x$1)
  def getColumnClassName(x$1: Int): String = inner.getColumnClassName(x$1)

}
