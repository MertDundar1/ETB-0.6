package ch.qos.logback.classic;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AsyncAppenderBase;























public class AsyncAppender
  extends AsyncAppenderBase<ILoggingEvent>
{
  boolean includeCallerData = false;
  


  public AsyncAppender() {}
  

  protected boolean isDiscardable(ILoggingEvent event)
  {
    Level level = event.getLevel();
    return level.toInt() <= 20000;
  }
  
  protected void preprocess(ILoggingEvent eventObject) {
    eventObject.prepareForDeferredProcessing();
    if (includeCallerData)
      eventObject.getCallerData();
  }
  
  public boolean isIncludeCallerData() {
    return includeCallerData;
  }
  
  public void setIncludeCallerData(boolean includeCallerData) {
    this.includeCallerData = includeCallerData;
  }
}
