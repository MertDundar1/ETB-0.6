package ch.qos.logback.core.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;






















public class DriverManagerConnectionSource
  extends ConnectionSourceBase
{
  private String driverClass = null;
  private String url = null;
  
  public DriverManagerConnectionSource() {}
  
  public void start() { try { if (driverClass != null) {
        Class.forName(driverClass);
        discoverConnectionProperties();
      } else {
        addError("WARNING: No JDBC driver specified for logback DriverManagerConnectionSource.");
      }
    } catch (ClassNotFoundException cnfe) {
      addError("Could not load JDBC driver class: " + driverClass, cnfe);
    }
  }
  

  public Connection getConnection()
    throws SQLException
  {
    if (getUser() == null) {
      return DriverManager.getConnection(url);
    }
    return DriverManager.getConnection(url, getUser(), getPassword());
  }
  





  public String getUrl()
  {
    return url;
  }
  





  public void setUrl(String url)
  {
    this.url = url;
  }
  




  public String getDriverClass()
  {
    return driverClass;
  }
  





  public void setDriverClass(String driverClass)
  {
    this.driverClass = driverClass;
  }
}
