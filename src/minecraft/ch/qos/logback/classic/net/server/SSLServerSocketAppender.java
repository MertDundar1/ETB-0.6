package ch.qos.logback.classic.net.server;

import ch.qos.logback.classic.net.LoggingEventPreSerializationTransformer;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.net.server.SSLServerSocketAppenderBase;
import ch.qos.logback.core.spi.PreSerializationTransformer;



















public class SSLServerSocketAppender
  extends SSLServerSocketAppenderBase<ILoggingEvent>
{
  private static final PreSerializationTransformer<ILoggingEvent> pst = new LoggingEventPreSerializationTransformer();
  private boolean includeCallerData;
  
  public SSLServerSocketAppender() {}
  
  protected void postProcessEvent(ILoggingEvent event)
  {
    if (isIncludeCallerData()) {
      event.getCallerData();
    }
  }
  
  protected PreSerializationTransformer<ILoggingEvent> getPST()
  {
    return pst;
  }
  
  public boolean isIncludeCallerData() {
    return includeCallerData;
  }
  
  public void setIncludeCallerData(boolean includeCallerData) {
    this.includeCallerData = includeCallerData;
  }
}
