package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextVO;

















public class ContextNameConverter
  extends ClassicConverter
{
  public ContextNameConverter() {}
  
  public String convert(ILoggingEvent event)
  {
    return event.getLoggerContextVO().getName();
  }
}
