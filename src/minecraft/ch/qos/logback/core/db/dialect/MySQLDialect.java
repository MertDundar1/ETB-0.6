package ch.qos.logback.core.db.dialect;






public class MySQLDialect
  implements SQLDialect
{
  public static final String SELECT_LAST_INSERT_ID = "SELECT LAST_INSERT_ID()";
  





  public MySQLDialect() {}
  




  public String getSelectInsertId()
  {
    return "SELECT LAST_INSERT_ID()";
  }
}
