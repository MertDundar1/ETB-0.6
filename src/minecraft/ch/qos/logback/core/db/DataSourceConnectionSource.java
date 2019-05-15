package ch.qos.logback.core.db;

import ch.qos.logback.core.db.dialect.SQLDialectCode;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

























public class DataSourceConnectionSource
  extends ConnectionSourceBase
{
  private DataSource dataSource;
  
  public DataSourceConnectionSource() {}
  
  public void start()
  {
    if (dataSource == null) {
      addWarn("WARNING: No data source specified");
    } else {
      discoverConnectionProperties();
      if ((!supportsGetGeneratedKeys()) && (getSQLDialectCode() == SQLDialectCode.UNKNOWN_DIALECT))
      {
        addWarn("Connection does not support GetGeneratedKey method and could not discover the dialect.");
      }
    }
    super.start();
  }
  

  public Connection getConnection()
    throws SQLException
  {
    if (dataSource == null) {
      addError("WARNING: No data source specified");
      return null;
    }
    
    if (getUser() == null) {
      return dataSource.getConnection();
    }
    return dataSource.getConnection(getUser(), getPassword());
  }
  
  public DataSource getDataSource()
  {
    return dataSource;
  }
  
  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }
}
