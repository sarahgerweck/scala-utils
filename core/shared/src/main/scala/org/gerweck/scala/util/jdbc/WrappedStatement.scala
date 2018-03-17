package org.gerweck.scala.util.jdbc

import java.sql.Statement

/** A wrapper around a JDBC
  * `[[http://docs.oracle.com/javase/7/docs/api/java/sql/Statement.html java.sql.Statement]]`.
  *
  * This is useful to allow you to override or wrap certain methods: you can
  * use this as a base class and override just the methods you care about.
  * Everything else will delegate to the underlying connection.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
class WrappedStatement(val inner: Statement) extends Statement with WrapperWrapping[Statement] {
  def addBatch(x$1: String): Unit = inner.addBatch(x$1)
  def cancel(): Unit = inner.cancel()
  def clearBatch(): Unit = inner.clearBatch()
  def clearWarnings(): Unit = inner.clearWarnings()
  def close(): Unit = inner.close()
  def closeOnCompletion(): Unit = inner.closeOnCompletion()
  def execute(x$1: String,x$2: Array[String]): Boolean = inner.execute(x$1, x$2)
  def execute(x$1: String,x$2: Array[Int]): Boolean = inner.execute(x$1, x$2)
  def execute(x$1: String,x$2: Int): Boolean = inner.execute(x$1, x$2)
  def execute(x$1: String): Boolean = inner.execute(x$1)
  def executeBatch(): Array[Int] = inner.executeBatch()
  def executeQuery(x$1: String): java.sql.ResultSet = inner.executeQuery(x$1)
  def executeUpdate(x$1: String,x$2: Array[String]): Int = inner.executeUpdate(x$1, x$2)
  def executeUpdate(x$1: String,x$2: Array[Int]): Int = inner.executeUpdate(x$1, x$2)
  def executeUpdate(x$1: String,x$2: Int): Int = inner.executeUpdate(x$1, x$2)
  def executeUpdate(x$1: String): Int = inner.executeUpdate(x$1)
  def getConnection(): java.sql.Connection = inner.getConnection()
  def getFetchDirection(): Int = inner.getFetchDirection()
  def getFetchSize(): Int = inner.getFetchSize()
  def getGeneratedKeys(): java.sql.ResultSet = inner.getGeneratedKeys()
  def getMaxFieldSize(): Int = inner.getMaxFieldSize()
  def getMaxRows(): Int = inner.getMaxRows()
  def getMoreResults(x$1: Int): Boolean = inner.getMoreResults(x$1)
  def getMoreResults(): Boolean = inner.getMoreResults()
  def getQueryTimeout(): Int = inner.getQueryTimeout()
  def getResultSet(): java.sql.ResultSet = inner.getResultSet()
  def getResultSetConcurrency(): Int = inner.getResultSetConcurrency()
  def getResultSetHoldability(): Int = inner.getResultSetHoldability()
  def getResultSetType(): Int = inner.getResultSetType()
  def getUpdateCount(): Int = inner.getUpdateCount()
  def getWarnings(): java.sql.SQLWarning = inner.getWarnings()
  def isCloseOnCompletion(): Boolean = inner.isCloseOnCompletion()
  def isClosed(): Boolean = inner.isClosed()
  def isPoolable(): Boolean = inner.isPoolable()
  def setCursorName(x$1: String): Unit = inner.setCursorName(x$1)
  def setEscapeProcessing(x$1: Boolean): Unit = inner.setEscapeProcessing(x$1)
  def setFetchDirection(x$1: Int): Unit = inner.setFetchDirection(x$1)
  def setFetchSize(x$1: Int): Unit = inner.setFetchSize(x$1)
  def setMaxFieldSize(x$1: Int): Unit = inner.setMaxFieldSize(x$1)
  def setMaxRows(x$1: Int): Unit = inner.setMaxRows(x$1)
  def setPoolable(x$1: Boolean): Unit = inner.setPoolable(x$1)
  def setQueryTimeout(x$1: Int): Unit = inner.setQueryTimeout(x$1)
}
