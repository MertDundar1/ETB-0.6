package ch.qos.logback.classic.spi;

import java.io.Serializable;












public class ClassPackagingData
  implements Serializable
{
  private static final long serialVersionUID = -804643281218337001L;
  final String codeLocation;
  final String version;
  private final boolean exact;
  
  public ClassPackagingData(String codeLocation, String version)
  {
    this.codeLocation = codeLocation;
    this.version = version;
    exact = true;
  }
  
  public ClassPackagingData(String classLocation, String version, boolean exact) {
    codeLocation = classLocation;
    this.version = version;
    this.exact = exact;
  }
  
  public String getCodeLocation() {
    return codeLocation;
  }
  
  public String getVersion() {
    return version;
  }
  
  public boolean isExact() {
    return exact;
  }
  
  public int hashCode()
  {
    int PRIME = 31;
    int result = 1;
    result = 31 * result + (codeLocation == null ? 0 : codeLocation.hashCode());
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
    ClassPackagingData other = (ClassPackagingData)obj;
    if (codeLocation == null) {
      if (codeLocation != null)
        return false;
    } else if (!codeLocation.equals(codeLocation))
      return false;
    if (exact != exact)
      return false;
    if (version == null) {
      if (version != null)
        return false;
    } else if (!version.equals(version))
      return false;
    return true;
  }
}
