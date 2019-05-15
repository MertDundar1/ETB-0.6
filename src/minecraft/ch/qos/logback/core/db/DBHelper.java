package ch.qos.logback.core.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;















public class DBHelper
{
  public DBHelper() {}
  
  public static void closeConnection(Connection connection)
  {
    if (connection != null) {
      try {
        connection.close();
      }
      catch (SQLException sqle) {}
    }
  }
  

  public static void closeStatement(Statement statement)
  {
    if (statement != null) {
      try {
        statement.close();
      }
      catch (SQLException sqle) {}
    }
  }
}
