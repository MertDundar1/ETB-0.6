package ch.qos.logback.core.db.dialect;






public class HSQLDBDialect
  implements SQLDialect
{
  public static final String SELECT_CURRVAL = "CALL IDENTITY()";
  




  public HSQLDBDialect() {}
  




  public String getSelectInsertId()
  {
    return "CALL IDENTITY()";
  }
}
