package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.Level;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;




























public class LoggingEventVO
  implements ILoggingEvent, Serializable
{
  private static final long serialVersionUID = 6553722650255690312L;
  private static final int NULL_ARGUMENT_ARRAY = -1;
  private static final String NULL_ARGUMENT_ARRAY_ELEMENT = "NULL_ARGUMENT_ARRAY_ELEMENT";
  private String threadName;
  private String loggerName;
  private LoggerContextVO loggerContextVO;
  private transient Level level;
  private String message;
  private transient String formattedMessage;
  private transient Object[] argumentArray;
  private ThrowableProxyVO throwableProxy;
  private StackTraceElement[] callerDataArray;
  private Marker marker;
  private Map<String, String> mdcPropertyMap;
  private long timeStamp;
  
  public LoggingEventVO() {}
  
  public static LoggingEventVO build(ILoggingEvent le)
  {
    LoggingEventVO ledo = new LoggingEventVO();
    loggerName = le.getLoggerName();
    loggerContextVO = le.getLoggerContextVO();
    threadName = le.getThreadName();
    level = le.getLevel();
    message = le.getMessage();
    argumentArray = le.getArgumentArray();
    marker = le.getMarker();
    mdcPropertyMap = le.getMDCPropertyMap();
    timeStamp = le.getTimeStamp();
    throwableProxy = ThrowableProxyVO.build(le.getThrowableProxy());
    

    if (le.hasCallerData()) {
      callerDataArray = le.getCallerData();
    }
    return ledo;
  }
  
  public String getThreadName() {
    return threadName;
  }
  
  public LoggerContextVO getLoggerContextVO() {
    return loggerContextVO;
  }
  
  public String getLoggerName() {
    return loggerName;
  }
  
  public Level getLevel() {
    return level;
  }
  
  public String getMessage() {
    return message;
  }
  
  public String getFormattedMessage() {
    if (formattedMessage != null) {
      return formattedMessage;
    }
    
    if (argumentArray != null) {
      formattedMessage = MessageFormatter.arrayFormat(message, argumentArray).getMessage();
    }
    else {
      formattedMessage = message;
    }
    
    return formattedMessage;
  }
  
  public Object[] getArgumentArray() {
    return argumentArray;
  }
  
  public IThrowableProxy getThrowableProxy() {
    return throwableProxy;
  }
  
  public StackTraceElement[] getCallerData() {
    return callerDataArray;
  }
  
  public boolean hasCallerData() {
    return callerDataArray != null;
  }
  
  public Marker getMarker() {
    return marker;
  }
  
  public long getTimeStamp() {
    return timeStamp;
  }
  
  public long getContextBirthTime() {
    return loggerContextVO.getBirthTime();
  }
  
  public LoggerContextVO getContextLoggerRemoteView() {
    return loggerContextVO;
  }
  
  public Map<String, String> getMDCPropertyMap() {
    return mdcPropertyMap;
  }
  
  public Map<String, String> getMdc() { return mdcPropertyMap; }
  
  public void prepareForDeferredProcessing() {}
  
  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    out.writeInt(level.levelInt);
    if (argumentArray != null) {
      int len = argumentArray.length;
      out.writeInt(len);
      for (int i = 0; i < argumentArray.length; i++) {
        if (argumentArray[i] != null) {
          out.writeObject(argumentArray[i].toString());
        } else {
          out.writeObject("NULL_ARGUMENT_ARRAY_ELEMENT");
        }
      }
    } else {
      out.writeInt(-1);
    }
  }
  
  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    int levelInt = in.readInt();
    level = Level.toLevel(levelInt);
    
    int argArrayLen = in.readInt();
    if (argArrayLen != -1) {
      argumentArray = new String[argArrayLen];
      for (int i = 0; i < argArrayLen; i++) {
        Object val = in.readObject();
        if (!"NULL_ARGUMENT_ARRAY_ELEMENT".equals(val)) {
          argumentArray[i] = val;
        }
      }
    }
  }
  
  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (message == null ? 0 : message.hashCode());
    result = 31 * result + (threadName == null ? 0 : threadName.hashCode());
    
    result = 31 * result + (int)(timeStamp ^ timeStamp >>> 32);
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
    LoggingEventVO other = (LoggingEventVO)obj;
    if (message == null) {
      if (message != null)
        return false;
    } else if (!message.equals(message)) {
      return false;
    }
    if (loggerName == null) {
      if (loggerName != null)
        return false;
    } else if (!loggerName.equals(loggerName)) {
      return false;
    }
    if (threadName == null) {
      if (threadName != null)
        return false;
    } else if (!threadName.equals(threadName))
      return false;
    if (timeStamp != timeStamp) {
      return false;
    }
    if (marker == null) {
      if (marker != null)
        return false;
    } else if (!marker.equals(marker)) {
      return false;
    }
    if (mdcPropertyMap == null) {
      if (mdcPropertyMap != null)
        return false;
    } else if (!mdcPropertyMap.equals(mdcPropertyMap))
      return false;
    return true;
  }
}
