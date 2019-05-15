package ch.qos.logback.core.property;

import ch.qos.logback.core.PropertyDefinerBase;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import java.net.URL;






















public class ResourceExistsPropertyDefiner
  extends PropertyDefinerBase
{
  String resourceStr;
  
  public ResourceExistsPropertyDefiner() {}
  
  public String getResource()
  {
    return resourceStr;
  }
  




  public void setResource(String resource)
  {
    resourceStr = resource;
  }
  





  public String getPropertyValue()
  {
    if (OptionHelper.isEmpty(resourceStr)) {
      addError("The \"resource\" property must be set.");
      return null;
    }
    
    URL resourceURL = Loader.getResourceBySelfClassLoader(resourceStr);
    return booleanAsStr(resourceURL != null);
  }
}
