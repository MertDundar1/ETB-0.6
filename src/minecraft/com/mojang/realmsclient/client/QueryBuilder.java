package com.mojang.realmsclient.client;

import java.util.Map;

public class QueryBuilder
{
  public QueryBuilder() {}
  
  private Map<String, String> queryParams = new java.util.HashMap();
  
  public static QueryBuilder of(String key, String value) {
    QueryBuilder queryBuilder = new QueryBuilder();
    queryParams.put(key, value);
    return queryBuilder;
  }
  
  public static QueryBuilder empty() {
    return new QueryBuilder();
  }
  
  public QueryBuilder with(String key, String value) {
    queryParams.put(key, value);
    return this;
  }
  
  public QueryBuilder with(Object key, Object value) {
    queryParams.put(String.valueOf(key), String.valueOf(value));
    return this;
  }
  
  public String toQueryString() {
    StringBuilder stringBuilder = new StringBuilder();
    
    java.util.Iterator<String> keyIterator = queryParams.keySet().iterator();
    if (!keyIterator.hasNext()) {
      return null;
    }
    
    String firstKey = (String)keyIterator.next();
    stringBuilder.append(firstKey).append("=").append((String)queryParams.get(firstKey));
    
    while (keyIterator.hasNext()) {
      String key = (String)keyIterator.next();
      stringBuilder.append("&").append(key).append("=").append((String)queryParams.get(key));
    }
    
    return stringBuilder.toString();
  }
}
