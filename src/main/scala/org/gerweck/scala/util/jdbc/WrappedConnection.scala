package org.gerweck.scala.util.jdbc

import java.sql.Connection

/** A wrapper around a JDBC connection.
  *
  * This is useful to allow you to override or wrap certain methods: you can
  * use this as a base class and override just the methods you care about.
  * Everything else will delegate to the underlying connection.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
class WrappedConnection(val inner: Connection) extends Connection {
  def abort(x$1: java.util.concurrent.Executor): Unit = { inner.abort(x$1) }
  def clearWarnings(): Unit = { inner.clearWarnings() }
  def close(): Unit = { inner.close() }
  def commit(): Unit = { inner.commit() }
  def createArrayOf(x$1: String,x$2: Array[Object]): java.sql.Array = { inner.createArrayOf(x$1, x$2) }
  def createBlob(): java.sql.Blob = { inner.createBlob() }
  def createClob(): java.sql.Clob = { inner.createClob() }
  def createNClob(): java.sql.NClob = { inner.createNClob() }
  def createSQLXML(): java.sql.SQLXML = { inner.createSQLXML() }
  def createStatement(x$1: Int,x$2: Int,x$3: Int): java.sql.Statement = { inner.createStatement(x$1, x$2, x$3) }
  def createStatement(x$1: Int,x$2: Int): java.sql.Statement = { inner.createStatement(x$1, x$2) }
  def createStatement(): java.sql.Statement = { inner.createStatement }
  def createStruct(x$1: String,x$2: Array[Object]): java.sql.Struct = { inner.createStruct(x$1, x$2) }
  def getAutoCommit(): Boolean = { inner.getAutoCommit() }
  def getCatalog(): String = { inner.getCatalog() }
  def getClientInfo(): java.util.Properties = { inner.getClientInfo() }
  def getClientInfo(x$1: String): String = { inner.getClientInfo(x$1) }
  def getHoldability(): Int = { inner.getHoldability() }
  def getMetaData(): java.sql.DatabaseMetaData = { inner.getMetaData() }
  def getNetworkTimeout(): Int = { inner.getNetworkTimeout() }
  def getSchema(): String = { inner.getSchema() }
  def getTransactionIsolation(): Int = { inner.getTransactionIsolation() }
  def getTypeMap(): java.util.Map[String,Class[_]] = { inner.getTypeMap() }
  def getWarnings(): java.sql.SQLWarning = { inner.getWarnings() }
  def isClosed(): Boolean = { inner.isClosed() }
  def isReadOnly(): Boolean = { inner.isReadOnly() }
  def isValid(x$1: Int): Boolean = { inner.isValid(x$1) }
  def nativeSQL(x$1: String): String = { inner.nativeSQL(x$1) }
  def prepareCall(x$1: String,x$2: Int,x$3: Int,x$4: Int): java.sql.CallableStatement = { inner.prepareCall(x$1, x$2, x$3, x$4) }
  def prepareCall(x$1: String,x$2: Int,x$3: Int): java.sql.CallableStatement = { inner.prepareCall(x$1, x$2, x$3) }
  def prepareCall(x$1: String): java.sql.CallableStatement = { inner.prepareCall(x$1) }
  def prepareStatement(x$1: String,x$2: Array[String]): java.sql.PreparedStatement = { inner.prepareStatement(x$1, x$2) }
  def prepareStatement(x$1: String,x$2: Array[Int]): java.sql.PreparedStatement = { inner.prepareStatement(x$1, x$2) }
  def prepareStatement(x$1: String,x$2: Int): java.sql.PreparedStatement = { inner.prepareStatement(x$1, x$2) }
  def prepareStatement(x$1: String,x$2: Int,x$3: Int,x$4: Int): java.sql.PreparedStatement = { inner.prepareStatement(x$1, x$2, x$3, x$4) }
  def prepareStatement(x$1: String,x$2: Int,x$3: Int): java.sql.PreparedStatement = { inner.prepareStatement(x$1, x$2, x$3) }
  def prepareStatement(x$1: String): java.sql.PreparedStatement = { inner.prepareStatement(x$1) }
  def releaseSavepoint(x$1: java.sql.Savepoint): Unit = { inner.releaseSavepoint(x$1) }
  def rollback(x$1: java.sql.Savepoint): Unit = { inner.rollback(x$1) }
  def rollback(): Unit = { inner.rollback() }
  def setAutoCommit(x$1: Boolean): Unit = { inner.setAutoCommit(x$1) }
  def setCatalog(x$1: String): Unit = { inner.setCatalog(x$1) }
  def setClientInfo(x$1: java.util.Properties): Unit = { inner.setClientInfo(x$1) }
  def setClientInfo(x$1: String,x$2: String): Unit = { inner.setClientInfo(x$1, x$2) }
  def setHoldability(x$1: Int): Unit = { inner.setHoldability(x$1) }
  def setNetworkTimeout(x$1: java.util.concurrent.Executor,x$2: Int): Unit = { inner.setNetworkTimeout(x$1, x$2) }
  def setReadOnly(x$1: Boolean): Unit = { inner.setReadOnly(x$1) }
  def setSavepoint(x$1: String): java.sql.Savepoint = { inner.setSavepoint(x$1) }
  def setSavepoint(): java.sql.Savepoint = { inner.setSavepoint() }
  def setSchema(x$1: String): Unit = { inner.setSchema(x$1) }
  def setTransactionIsolation(x$1: Int): Unit = { inner.setTransactionIsolation(x$1) }
  def setTypeMap(x$1: java.util.Map[String,Class[_]]): Unit = { inner.setTypeMap(x$1) }

  // Members declared in java.sql.Wrapper
  def isWrapperFor(x$1: Class[_]): Boolean = { inner.isWrapperFor(x$1) }
  def unwrap[T](x$1: Class[T]): T = { inner.unwrap(x$1) }

  override def hashCode: Int = inner.hashCode
  override def equals(o: Any) = inner.equals(o)
}
