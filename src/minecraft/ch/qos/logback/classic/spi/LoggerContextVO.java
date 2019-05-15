package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.LoggerContext;
import java.io.Serializable;
import java.util.Map;





























public class LoggerContextVO
  implements Serializable
{
  private static final long serialVersionUID = 5488023392483144387L;
  final String name;
  final Map<String, String> propertyMap;
  final long birthTime;
  
  public LoggerContextVO(LoggerContext lc)
  {
    name = lc.getName();
    propertyMap = lc.getCopyOfPropertyMap();
    birthTime = lc.getBirthTime();
  }
  
  public LoggerContextVO(String name, Map<String, String> propertyMap, long birthTime) {
    this.name = name;
    this.propertyMap = propertyMap;
    this.birthTime = birthTime;
  }
  
  public String getName() {
    return name;
  }
  
  public Map<String, String> getPropertyMap() {
    return propertyMap;
  }
  
  public long getBirthTime() {
    return birthTime;
  }
  

  public String toString()
  {
    return "LoggerContextVO{name='" + name + '\'' + ", propertyMap=" + propertyMap + ", birthTime=" + birthTime + '}';
  }
  



  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof LoggerContextVO)) { return false;
    }
    LoggerContextVO that = (LoggerContextVO)o;
    
    if (birthTime != birthTime) return false;
    if (name != null ? !name.equals(name) : name != null) return false;
    if (propertyMap != null ? !propertyMap.equals(propertyMap) : propertyMap != null) { return false;
    }
    return true;
  }
  
  public int hashCode()
  {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (propertyMap != null ? propertyMap.hashCode() : 0);
    result = 31 * result + (int)(birthTime ^ birthTime >>> 32);
    
    return result;
  }
}
