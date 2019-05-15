package ch.qos.logback.core.db.dialect;

public abstract interface SQLDialect
{
  public abstract String getSelectInsertId();
}
