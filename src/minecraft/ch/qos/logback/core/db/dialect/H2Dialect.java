package ch.qos.logback.core.db.dialect;






public class H2Dialect
  implements SQLDialect
{
  public static final String SELECT_CURRVAL = "CALL IDENTITY()";
  




  public H2Dialect() {}
  




  public String getSelectInsertId()
  {
    return "CALL IDENTITY()";
  }
}
