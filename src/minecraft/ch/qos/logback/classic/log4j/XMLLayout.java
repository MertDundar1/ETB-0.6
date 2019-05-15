package ch.qos.logback.classic.log4j;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.helpers.Transform;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


























public class XMLLayout
  extends LayoutBase<ILoggingEvent>
{
  private final int DEFAULT_SIZE = 256;
  private final int UPPER_LIMIT = 2048;
  
  private StringBuilder buf = new StringBuilder(256);
  private boolean locationInfo = false;
  private boolean properties = false;
  
  public XMLLayout() {}
  
  public void start() { super.start(); }
  










  public void setLocationInfo(boolean flag)
  {
    locationInfo = flag;
  }
  


  public boolean getLocationInfo()
  {
    return locationInfo;
  }
  






  public void setProperties(boolean flag)
  {
    properties = flag;
  }
  





  public boolean getProperties()
  {
    return properties;
  }
  





  public String doLayout(ILoggingEvent event)
  {
    if (buf.capacity() > 2048) {
      buf = new StringBuilder(256);
    } else {
      buf.setLength(0);
    }
    


    buf.append("<log4j:event logger=\"");
    buf.append(Transform.escapeTags(event.getLoggerName()));
    buf.append("\"\r\n");
    buf.append("             timestamp=\"");
    buf.append(event.getTimeStamp());
    buf.append("\" level=\"");
    buf.append(event.getLevel());
    buf.append("\" thread=\"");
    buf.append(Transform.escapeTags(event.getThreadName()));
    buf.append("\">\r\n");
    
    buf.append("  <log4j:message>");
    buf.append(Transform.escapeTags(event.getFormattedMessage()));
    buf.append("</log4j:message>\r\n");
    




    IThrowableProxy tp = event.getThrowableProxy();
    if (tp != null) {
      StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
      buf.append("  <log4j:throwable><![CDATA[");
      for (StackTraceElementProxy step : stepArray) {
        buf.append('\t');
        buf.append(step.toString());
        buf.append("\r\n");
      }
      buf.append("]]></log4j:throwable>\r\n");
    }
    
    if (locationInfo) {
      StackTraceElement[] callerDataArray = event.getCallerData();
      if ((callerDataArray != null) && (callerDataArray.length > 0)) {
        StackTraceElement immediateCallerData = callerDataArray[0];
        buf.append("  <log4j:locationInfo class=\"");
        buf.append(immediateCallerData.getClassName());
        buf.append("\"\r\n");
        buf.append("                      method=\"");
        buf.append(Transform.escapeTags(immediateCallerData.getMethodName()));
        buf.append("\" file=\"");
        buf.append(Transform.escapeTags(immediateCallerData.getFileName()));
        buf.append("\" line=\"");
        buf.append(immediateCallerData.getLineNumber());
        buf.append("\"/>\r\n");
      }
    }
    




    if (getProperties()) {
      Map<String, String> propertyMap = event.getMDCPropertyMap();
      
      if ((propertyMap != null) && (propertyMap.size() != 0)) {
        Set<Map.Entry<String, String>> entrySet = propertyMap.entrySet();
        buf.append("  <log4j:properties>");
        for (Map.Entry<String, String> entry : entrySet) {
          buf.append("\r\n    <log4j:data");
          buf.append(" name='" + Transform.escapeTags((String)entry.getKey()) + "'");
          buf.append(" value='" + Transform.escapeTags((String)entry.getValue()) + "'");
          buf.append(" />");
        }
        buf.append("\r\n  </log4j:properties>");
      }
    }
    
    buf.append("\r\n</log4j:event>\r\n\r\n");
    
    return buf.toString();
  }
  
  public String getContentType()
  {
    return "text/xml";
  }
}
