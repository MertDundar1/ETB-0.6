package ch.qos.logback.classic.db.names;









public class SimpleDBNameResolver
  implements DBNameResolver
{
  public SimpleDBNameResolver() {}
  








  private String tableNamePrefix = "";
  
  private String tableNameSuffix = "";
  
  private String columnNamePrefix = "";
  
  private String columnNameSuffix = "";
  
  public <N extends Enum<?>> String getTableName(N tableName) {
    return tableNamePrefix + tableName.name().toLowerCase() + tableNameSuffix;
  }
  
  public <N extends Enum<?>> String getColumnName(N columnName) {
    return columnNamePrefix + columnName.name().toLowerCase() + columnNameSuffix;
  }
  
  public void setTableNamePrefix(String tableNamePrefix) {
    this.tableNamePrefix = (tableNamePrefix != null ? tableNamePrefix : "");
  }
  
  public void setTableNameSuffix(String tableNameSuffix) {
    this.tableNameSuffix = (tableNameSuffix != null ? tableNameSuffix : "");
  }
  
  public void setColumnNamePrefix(String columnNamePrefix) {
    this.columnNamePrefix = (columnNamePrefix != null ? columnNamePrefix : "");
  }
  
  public void setColumnNameSuffix(String columnNameSuffix) {
    this.columnNameSuffix = (columnNameSuffix != null ? columnNameSuffix : "");
  }
}
