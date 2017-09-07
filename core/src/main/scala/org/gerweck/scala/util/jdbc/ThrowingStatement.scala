package org.gerweck.scala.util.jdbc

import java.sql.Statement

import org.gerweck.scala.util.Support.{support => nse}

/** A JDBC
  * `[[http://docs.oracle.com/javase/7/docs/api/java/sql/Statement.html java.sql.Statement]]`.
  * that throws an exception for all methods.
  *
  * This is useful as a base class for a very limited implementation. You probably don't want to
  * use this for a real driver, because you won't be able to easily tell what you have and haven't
  * implemented.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
class ThrowingStatement extends Statement with ThrowingWrapper {
  override def addBatch(x$1: String): Unit = nse
  override def cancel(): Unit = nse
  override def clearBatch(): Unit = nse
  override def clearWarnings(): Unit = nse
  override def close(): Unit = nse
  override def closeOnCompletion(): Unit = nse
  override def execute(x$1: String,x$2: Array[String]): Boolean = nse
  override def execute(x$1: String,x$2: Array[Int]): Boolean = nse
  override def execute(x$1: String,x$2: Int): Boolean = nse
  override def execute(x$1: String): Boolean = nse
  override def executeBatch(): Array[Int] = nse
  override def executeQuery(x$1: String): java.sql.ResultSet = nse
  override def executeUpdate(x$1: String,x$2: Array[String]): Int = nse
  override def executeUpdate(x$1: String,x$2: Array[Int]): Int = nse
  override def executeUpdate(x$1: String,x$2: Int): Int = nse
  override def executeUpdate(x$1: String): Int = nse
  override def getConnection(): java.sql.Connection = nse
  override def getFetchDirection(): Int = nse
  override def getFetchSize(): Int = nse
  override def getGeneratedKeys(): java.sql.ResultSet = nse
  override def getMaxFieldSize(): Int = nse
  override def getMaxRows(): Int = nse
  override def getMoreResults(x$1: Int): Boolean = nse
  override def getMoreResults(): Boolean = nse
  override def getQueryTimeout(): Int = nse
  override def getResultSet(): java.sql.ResultSet = nse
  override def getResultSetConcurrency(): Int = nse
  override def getResultSetHoldability(): Int = nse
  override def getResultSetType(): Int = nse
  override def getUpdateCount(): Int = nse
  override def getWarnings(): java.sql.SQLWarning = nse
  override def isCloseOnCompletion(): Boolean = nse
  override def isClosed(): Boolean = nse
  override def isPoolable(): Boolean = nse
  override def setCursorName(x$1: String): Unit = nse
  override def setEscapeProcessing(x$1: Boolean): Unit = nse
  override def setFetchDirection(x$1: Int): Unit = nse
  override def setFetchSize(x$1: Int): Unit = nse
  override def setMaxFieldSize(x$1: Int): Unit = nse
  override def setMaxRows(x$1: Int): Unit = nse
  override def setPoolable(x$1: Boolean): Unit = nse
  override def setQueryTimeout(x$1: Int): Unit = nse
}
