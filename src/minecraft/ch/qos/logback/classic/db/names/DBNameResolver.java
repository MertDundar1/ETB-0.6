package ch.qos.logback.classic.db.names;

public abstract interface DBNameResolver
{
  public abstract <N extends Enum<?>> String getTableName(N paramN);
  
  public abstract <N extends Enum<?>> String getColumnName(N paramN);
}
