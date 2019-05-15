package ch.qos.logback.core.db.dialect;







public class MsSQLDialect
  implements SQLDialect
{
  public static final String SELECT_CURRVAL = "SELECT @@identity id";
  





  public MsSQLDialect() {}
  





  public String getSelectInsertId()
  {
    return "SELECT @@identity id";
  }
}
