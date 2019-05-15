package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.LogbackMDCAdapter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.MDCAdapter;






























































public class LoggingEvent
  implements ILoggingEvent
{
  transient String fqnOfLoggerClass;
  private String threadName;
  private String loggerName;
  private LoggerContext loggerContext;
  private LoggerContextVO loggerContextVO;
  private transient Level level;
  private String message;
  transient String formattedMessage;
  private transient Object[] argumentArray;
  private ThrowableProxy throwableProxy;
  private StackTraceElement[] callerDataArray;
  private Marker marker;
  private Map<String, String> mdcPropertyMap;
  private static final Map<String, String> CACHED_NULL_MAP = new HashMap();
  

  private long timeStamp;
  


  public LoggingEvent() {}
  


  public LoggingEvent(String fqcn, Logger logger, Level level, String message, Throwable throwable, Object[] argArray)
  {
    fqnOfLoggerClass = fqcn;
    loggerName = logger.getName();
    loggerContext = logger.getLoggerContext();
    loggerContextVO = loggerContext.getLoggerContextRemoteView();
    this.level = level;
    
    this.message = message;
    argumentArray = argArray;
    
    if (throwable == null) {
      throwable = extractThrowableAnRearrangeArguments(argArray);
    }
    
    if (throwable != null) {
      throwableProxy = new ThrowableProxy(throwable);
      LoggerContext lc = logger.getLoggerContext();
      if (lc.isPackagingDataEnabled()) {
        throwableProxy.calculatePackagingData();
      }
    }
    
    timeStamp = System.currentTimeMillis();
  }
  
  private Throwable extractThrowableAnRearrangeArguments(Object[] argArray) {
    Throwable extractedThrowable = EventArgUtil.extractThrowable(argArray);
    if (EventArgUtil.successfulExtraction(extractedThrowable)) {
      argumentArray = EventArgUtil.trimmedCopy(argArray);
    }
    return extractedThrowable;
  }
  
  public void setArgumentArray(Object[] argArray) {
    if (argumentArray != null) {
      throw new IllegalStateException("argArray has been already set");
    }
    argumentArray = argArray;
  }
  
  public Object[] getArgumentArray() {
    return argumentArray;
  }
  
  public Level getLevel() {
    return level;
  }
  
  public String getLoggerName() {
    return loggerName;
  }
  
  public void setLoggerName(String loggerName) {
    this.loggerName = loggerName;
  }
  
  public String getThreadName() {
    if (threadName == null) {
      threadName = Thread.currentThread().getName();
    }
    return threadName;
  }
  


  public void setThreadName(String threadName)
    throws IllegalStateException
  {
    if (this.threadName != null) {
      throw new IllegalStateException("threadName has been already set");
    }
    this.threadName = threadName;
  }
  



  public IThrowableProxy getThrowableProxy()
  {
    return throwableProxy;
  }
  


  public void setThrowableProxy(ThrowableProxy tp)
  {
    if (throwableProxy != null) {
      throw new IllegalStateException("ThrowableProxy has been already set.");
    }
    throwableProxy = tp;
  }
  








  public void prepareForDeferredProcessing()
  {
    getFormattedMessage();
    getThreadName();
    
    getMDCPropertyMap();
  }
  
  public LoggerContextVO getLoggerContextVO() {
    return loggerContextVO;
  }
  
  public void setLoggerContextRemoteView(LoggerContextVO loggerContextVO) {
    this.loggerContextVO = loggerContextVO;
  }
  
  public String getMessage() {
    return message;
  }
  
  public void setMessage(String message) {
    if (this.message != null) {
      throw new IllegalStateException("The message for this event has been set already.");
    }
    
    this.message = message;
  }
  
  public long getTimeStamp() {
    return timeStamp;
  }
  
  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
  }
  
  public void setLevel(Level level) {
    if (this.level != null) {
      throw new IllegalStateException("The level has been already set for this event.");
    }
    
    this.level = level;
  }
  









  public StackTraceElement[] getCallerData()
  {
    if (callerDataArray == null) {
      callerDataArray = CallerData.extract(new Throwable(), fqnOfLoggerClass, loggerContext.getMaxCallerDataDepth(), loggerContext.getFrameworkPackages());
    }
    
    return callerDataArray;
  }
  
  public boolean hasCallerData() {
    return callerDataArray != null;
  }
  
  public void setCallerData(StackTraceElement[] callerDataArray) {
    this.callerDataArray = callerDataArray;
  }
  
  public Marker getMarker() {
    return marker;
  }
  
  public void setMarker(Marker marker) {
    if (this.marker != null) {
      throw new IllegalStateException("The marker has been already set for this event.");
    }
    
    this.marker = marker;
  }
  
  public long getContextBirthTime() {
    return loggerContextVO.getBirthTime();
  }
  
  public String getFormattedMessage()
  {
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
  
  public Map<String, String> getMDCPropertyMap()
  {
    if (mdcPropertyMap == null) {
      MDCAdapter mdc = MDC.getMDCAdapter();
      if ((mdc instanceof LogbackMDCAdapter)) {
        mdcPropertyMap = ((LogbackMDCAdapter)mdc).getPropertyMap();
      } else {
        mdcPropertyMap = mdc.getCopyOfContextMap();
      }
    }
    if (mdcPropertyMap == null) {
      mdcPropertyMap = CACHED_NULL_MAP;
    }
    return mdcPropertyMap;
  }
  





  public void setMDCPropertyMap(Map<String, String> map)
  {
    if (mdcPropertyMap != null) {
      throw new IllegalStateException("The MDCPropertyMap has been already set for this event.");
    }
    
    mdcPropertyMap = map;
  }
  


  /**
   * @deprecated
   */
  public Map<String, String> getMdc()
  {
    return getMDCPropertyMap();
  }
  
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    sb.append(level).append("] ");
    sb.append(getFormattedMessage());
    return sb.toString();
  }
  




  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    throw new UnsupportedOperationException(getClass() + " does not support serialization. " + "Use LoggerEventVO instance instead. See also LoggerEventVO.build method.");
  }
}
