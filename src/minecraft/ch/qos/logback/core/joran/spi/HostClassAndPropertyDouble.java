package ch.qos.logback.core.joran.spi;









public class HostClassAndPropertyDouble
{
  final Class<?> hostClass;
  






  final String propertyName;
  







  public HostClassAndPropertyDouble(Class<?> hostClass, String propertyName)
  {
    this.hostClass = hostClass;
    this.propertyName = propertyName;
  }
  
  public Class<?> getHostClass() {
    return hostClass;
  }
  
  public String getPropertyName() {
    return propertyName;
  }
  
  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (hostClass == null ? 0 : hostClass.hashCode());
    result = 31 * result + (propertyName == null ? 0 : propertyName.hashCode());
    
    return result;
  }
  
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    HostClassAndPropertyDouble other = (HostClassAndPropertyDouble)obj;
    if (hostClass == null) {
      if (hostClass != null)
        return false;
    } else if (!hostClass.equals(hostClass))
      return false;
    if (propertyName == null) {
      if (propertyName != null)
        return false;
    } else if (!propertyName.equals(propertyName))
      return false;
    return true;
  }
}
