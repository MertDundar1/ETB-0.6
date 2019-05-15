package ch.qos.logback.core.property;

import ch.qos.logback.core.PropertyDefinerBase;
import ch.qos.logback.core.util.OptionHelper;
import java.io.File;




















public class FileExistsPropertyDefiner
  extends PropertyDefinerBase
{
  String path;
  
  public FileExistsPropertyDefiner() {}
  
  public String getPath()
  {
    return path;
  }
  




  public void setPath(String path)
  {
    this.path = path;
  }
  





  public String getPropertyValue()
  {
    if (OptionHelper.isEmpty(path)) {
      addError("The \"path\" property must be set.");
      return null;
    }
    
    File file = new File(path);
    return booleanAsStr(file.exists());
  }
}
