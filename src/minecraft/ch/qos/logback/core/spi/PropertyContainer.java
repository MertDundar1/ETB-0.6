package ch.qos.logback.core.spi;

import java.util.Map;

public abstract interface PropertyContainer
{
  public abstract String getProperty(String paramString);
  
  public abstract Map<String, String> getCopyOfPropertyMap();
}
