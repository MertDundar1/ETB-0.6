package ch.qos.logback.classic.db;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextVO;
import java.util.Map;
import java.util.Set;












public class DBHelper
{
  public static final short PROPERTIES_EXIST = 1;
  public static final short EXCEPTION_EXISTS = 2;
  
  public DBHelper() {}
  
  public static short computeReferenceMask(ILoggingEvent event)
  {
    short mask = 0;
    
    int mdcPropSize = 0;
    if (event.getMDCPropertyMap() != null) {
      mdcPropSize = event.getMDCPropertyMap().keySet().size();
    }
    int contextPropSize = 0;
    if (event.getLoggerContextVO().getPropertyMap() != null) {
      contextPropSize = event.getLoggerContextVO().getPropertyMap().size();
    }
    
    if ((mdcPropSize > 0) || (contextPropSize > 0)) {
      mask = 1;
    }
    if (event.getThrowableProxy() != null) {
      mask = (short)(mask | 0x2);
    }
    return mask;
  }
}
