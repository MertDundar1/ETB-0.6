package ch.qos.logback.core.db;

import ch.qos.logback.core.db.dialect.DBUtil;
import ch.qos.logback.core.db.dialect.SQLDialectCode;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;


















public abstract class ConnectionSourceBase
  extends ContextAwareBase
  implements ConnectionSource
{
  private boolean started;
  private String user = null;
  private String password = null;
  

  private SQLDialectCode dialectCode = SQLDialectCode.UNKNOWN_DIALECT;
  private boolean supportsGetGeneratedKeys = false;
  private boolean supportsBatchUpdates = false;
  

  public ConnectionSourceBase() {}
  

  public void discoverConnectionProperties()
  {
    Connection connection = null;
    try {
      connection = getConnection();
      if (connection == null) {
        addWarn("Could not get a connection");
      }
      else {
        DatabaseMetaData meta = connection.getMetaData();
        DBUtil util = new DBUtil();
        util.setContext(getContext());
        supportsGetGeneratedKeys = util.supportsGetGeneratedKeys(meta);
        supportsBatchUpdates = util.supportsBatchUpdates(meta);
        dialectCode = DBUtil.discoverSQLDialect(meta);
        addInfo("Driver name=" + meta.getDriverName());
        addInfo("Driver version=" + meta.getDriverVersion());
        addInfo("supportsGetGeneratedKeys=" + supportsGetGeneratedKeys);
      }
    } catch (SQLException se) {
      addWarn("Could not discover the dialect to use.", se);
    } finally {
      DBHelper.closeConnection(connection);
    }
  }
  


  public final boolean supportsGetGeneratedKeys()
  {
    return supportsGetGeneratedKeys;
  }
  
  public final SQLDialectCode getSQLDialectCode() {
    return dialectCode;
  }
  


  public final String getPassword()
  {
    return password;
  }
  



  public final void setPassword(String password)
  {
    this.password = password;
  }
  


  public final String getUser()
  {
    return user;
  }
  



  public final void setUser(String username)
  {
    user = username;
  }
  


  public final boolean supportsBatchUpdates()
  {
    return supportsBatchUpdates;
  }
  
  public boolean isStarted() {
    return started;
  }
  
  public void start() {
    started = true;
  }
  
  public void stop() {
    started = false;
  }
}
