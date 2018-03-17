package org.gerweck.scala.util.jdbc

import java.sql.{ Array => SqlArray, _ }
import java.util.concurrent.Executor
import java.util.{ Map => JMap, Properties }

import org.gerweck.scala.util.Support.{ support => nse }

/** A JDBC
  * `[[http://docs.oracle.com/javase/7/docs/api/java/sql/Connection.html java.sql.Connection]]`
  * that throws an exception for all methods.
  *
  * This is useful as a base class for a very limited implementation. You probably don't want to
  * use this for a real driver, because you won't be able to easily tell what you have and haven't
  * implemented.
  *
  * @author Sarah Gerweck <sarah.a180@gmail.com>
  */
trait ThrowingConnection extends Connection with ThrowingWrapper {
  override def abort(x$1: Executor): Unit = nse
  override def clearWarnings(): Unit = nse
  override def close(): Unit = nse
  override def commit(): Unit = nse
  override def createArrayOf(x$1: String, x$2: Array[Object]): SqlArray = nse
  override def createBlob(): Blob = nse
  override def createClob(): Clob = nse
  override def createNClob(): NClob = nse
  override def createSQLXML(): SQLXML = nse
  override def createStatement(x$1: Int, x$2: Int, x$3: Int): Statement = nse
  override def createStatement(x$1: Int, x$2: Int): Statement = nse
  override def createStatement(): Statement = nse
  override def createStruct(x$1: String, x$2: Array[Object]): Struct = nse
  override def getAutoCommit(): Boolean = nse
  override def getCatalog(): String = nse
  override def getClientInfo(): Properties = nse
  override def getClientInfo(x$1: String): String = nse
  override def getHoldability(): Int = nse
  override def getMetaData(): DatabaseMetaData = nse
  override def getNetworkTimeout(): Int = nse
  override def getSchema(): String = nse
  override def getTransactionIsolation(): Int = nse
  override def getTypeMap(): JMap[String, Class[_]] = nse
  override def getWarnings(): SQLWarning = nse
  override def isClosed(): Boolean = nse
  override def isReadOnly(): Boolean = nse
  override def isValid(x$1: Int): Boolean = nse
  override def nativeSQL(x$1: String): String = nse
  override def prepareCall(x$1: String, x$2: Int, x$3: Int, x$4: Int): CallableStatement = nse
  override def prepareCall(x$1: String, x$2: Int, x$3: Int): CallableStatement = nse
  override def prepareCall(x$1: String): CallableStatement = nse
  override def prepareStatement(x$1: String, x$2: Array[String]): PreparedStatement = nse
  override def prepareStatement(x$1: String, x$2: Array[Int]): PreparedStatement = nse
  override def prepareStatement(x$1: String, x$2: Int): PreparedStatement = nse
  override def prepareStatement(x$1: String, x$2: Int, x$3: Int, x$4: Int): PreparedStatement = nse
  override def prepareStatement(x$1: String, x$2: Int, x$3: Int): PreparedStatement = nse
  override def prepareStatement(x$1: String): PreparedStatement = nse
  override def releaseSavepoint(x$1: Savepoint): Unit = nse
  override def rollback(x$1: Savepoint): Unit = nse
  override def rollback(): Unit = nse
  override def setAutoCommit(x$1: Boolean): Unit = nse
  override def setCatalog(x$1: String): Unit = nse
  override def setClientInfo(x$1: Properties): Unit = nse
  override def setClientInfo(x$1: String, x$2: String): Unit = nse
  override def setHoldability(x$1: Int): Unit = nse
  override def setNetworkTimeout(x$1: Executor, x$2: Int): Unit = nse
  override def setReadOnly(x$1: Boolean): Unit = nse
  override def setSavepoint(x$1: String): Savepoint = nse
  override def setSavepoint(): Savepoint = nse
  override def setSchema(x$1: String): Unit = nse
  override def setTransactionIsolation(x$1: Int): Unit = nse
  override def setTypeMap(x$1: JMap[String, Class[_]]): Unit = nse
}
