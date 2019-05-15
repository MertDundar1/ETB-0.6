package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Map;
import java.util.Map.Entry;














public class MDCConverter
  extends ClassicConverter
{
  private String key;
  private String defaultValue = "";
  
  public MDCConverter() {}
  
  public void start() { String[] keyInfo = OptionHelper.extractDefaultReplacement(getFirstOption());
    key = keyInfo[0];
    if (keyInfo[1] != null) {
      defaultValue = keyInfo[1];
    }
    super.start();
  }
  
  public void stop()
  {
    key = null;
    super.stop();
  }
  
  public String convert(ILoggingEvent event)
  {
    Map<String, String> mdcPropertyMap = event.getMDCPropertyMap();
    
    if (mdcPropertyMap == null) {
      return defaultValue;
    }
    
    if (key == null) {
      return outputMDCForAllKeys(mdcPropertyMap);
    }
    
    String value = (String)event.getMDCPropertyMap().get(key);
    if (value != null) {
      return value;
    }
    return defaultValue;
  }
  




  private String outputMDCForAllKeys(Map<String, String> mdcPropertyMap)
  {
    StringBuilder buf = new StringBuilder();
    boolean first = true;
    for (Map.Entry<String, String> entry : mdcPropertyMap.entrySet()) {
      if (first) {
        first = false;
      } else {
        buf.append(", ");
      }
      
      buf.append((String)entry.getKey()).append('=').append((String)entry.getValue());
    }
    return buf.toString();
  }
}
