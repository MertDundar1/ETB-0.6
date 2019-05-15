package ch.qos.logback.core.db;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.db.dialect.DBUtil;
import ch.qos.logback.core.db.dialect.SQLDialect;
import ch.qos.logback.core.db.dialect.SQLDialectCode;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



















public abstract class DBAppenderBase<E>
  extends UnsynchronizedAppenderBase<E>
{
  protected ConnectionSource connectionSource;
  protected boolean cnxSupportsGetGeneratedKeys = false;
  protected boolean cnxSupportsBatchUpdates = false;
  protected SQLDialect sqlDialect;
  
  public DBAppenderBase() {}
  
  protected abstract Method getGeneratedKeysMethod();
  
  protected abstract String getInsertSQL();
  
  public void start() {
    if (connectionSource == null) {
      throw new IllegalStateException("DBAppender cannot function without a connection source");
    }
    

    sqlDialect = DBUtil.getDialectFromCode(connectionSource.getSQLDialectCode());
    
    if (getGeneratedKeysMethod() != null) {
      cnxSupportsGetGeneratedKeys = connectionSource.supportsGetGeneratedKeys();
    } else {
      cnxSupportsGetGeneratedKeys = false;
    }
    cnxSupportsBatchUpdates = connectionSource.supportsBatchUpdates();
    if ((!cnxSupportsGetGeneratedKeys) && (sqlDialect == null)) {
      throw new IllegalStateException("DBAppender cannot function if the JDBC driver does not support getGeneratedKeys method *and* without a specific SQL dialect");
    }
    


    super.start();
  }
  


  public ConnectionSource getConnectionSource()
  {
    return connectionSource;
  }
  



  public void setConnectionSource(ConnectionSource connectionSource)
  {
    this.connectionSource = connectionSource;
  }
  
  public void append(E eventObject)
  {
    Connection connection = null;
    PreparedStatement insertStatement = null;
    try {
      connection = connectionSource.getConnection();
      connection.setAutoCommit(false);
      
      if (cnxSupportsGetGeneratedKeys) {
        String EVENT_ID_COL_NAME = "EVENT_ID";
        
        if (connectionSource.getSQLDialectCode() == SQLDialectCode.POSTGRES_DIALECT) {
          EVENT_ID_COL_NAME = EVENT_ID_COL_NAME.toLowerCase();
        }
        insertStatement = connection.prepareStatement(getInsertSQL(), new String[] { EVENT_ID_COL_NAME });
      }
      else {
        insertStatement = connection.prepareStatement(getInsertSQL());
      }
      
      long eventId;
      
      synchronized (this) {
        subAppend(eventObject, connection, insertStatement);
        eventId = selectEventId(insertStatement, connection);
      }
      secondarySubAppend(eventObject, connection, eventId);
      
      connection.commit();
    } catch (Throwable sqle) {
      addError("problem appending event", sqle);
    } finally {
      DBHelper.closeStatement(insertStatement);
      DBHelper.closeConnection(connection);
    }
  }
  
  protected abstract void subAppend(E paramE, Connection paramConnection, PreparedStatement paramPreparedStatement)
    throws Throwable;
  
  protected abstract void secondarySubAppend(E paramE, Connection paramConnection, long paramLong)
    throws Throwable;
  
  protected long selectEventId(PreparedStatement insertStatement, Connection connection) throws SQLException, InvocationTargetException
  {
    ResultSet rs = null;
    Statement idStatement = null;
    try {
      boolean gotGeneratedKeys = false;
      if (cnxSupportsGetGeneratedKeys) {
        try {
          rs = (ResultSet)getGeneratedKeysMethod().invoke(insertStatement, (Object[])null);
          
          gotGeneratedKeys = true;
        } catch (InvocationTargetException ex) {
          Throwable target = ex.getTargetException();
          if ((target instanceof SQLException)) {
            throw ((SQLException)target);
          }
          throw ex;
        } catch (IllegalAccessException ex) {
          addWarn("IllegalAccessException invoking PreparedStatement.getGeneratedKeys", ex);
        }
      }
      


      if (!gotGeneratedKeys) {
        idStatement = connection.createStatement();
        idStatement.setMaxRows(1);
        String selectInsertIdStr = sqlDialect.getSelectInsertId();
        rs = idStatement.executeQuery(selectInsertIdStr);
      }
      


      rs.next();
      long eventId = rs.getLong(1);
      return eventId;
    } finally {
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException e) {}
      }
      DBHelper.closeStatement(idStatement);
    }
  }
  
  public void stop()
  {
    super.stop();
  }
}
