package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextVO;










public class RelativeTimeConverter
  extends ClassicConverter
{
  public RelativeTimeConverter() {}
  
  long lastTimestamp = -1L;
  String timesmapCache = null;
  
  public String convert(ILoggingEvent event) {
    long now = event.getTimeStamp();
    
    synchronized (this)
    {
      if (now != lastTimestamp) {
        lastTimestamp = now;
        timesmapCache = Long.toString(now - event.getLoggerContextVO().getBirthTime());
      }
      return timesmapCache;
    }
  }
}
