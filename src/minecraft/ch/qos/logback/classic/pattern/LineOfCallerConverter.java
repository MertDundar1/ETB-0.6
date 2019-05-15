package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;











public class LineOfCallerConverter
  extends ClassicConverter
{
  public LineOfCallerConverter() {}
  
  public String convert(ILoggingEvent le)
  {
    StackTraceElement[] cda = le.getCallerData();
    if ((cda != null) && (cda.length > 0)) {
      return Integer.toString(cda[0].getLineNumber());
    }
    return "?";
  }
}
