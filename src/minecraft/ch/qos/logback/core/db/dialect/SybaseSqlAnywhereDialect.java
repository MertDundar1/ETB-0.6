package ch.qos.logback.core.db.dialect;







public class SybaseSqlAnywhereDialect
  implements SQLDialect
{
  public static final String SELECT_CURRVAL = "SELECT @@identity id";
  






  public SybaseSqlAnywhereDialect() {}
  






  public String getSelectInsertId()
  {
    return "SELECT @@identity id";
  }
}
