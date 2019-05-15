package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;











public class FileOfCallerConverter
  extends ClassicConverter
{
  public FileOfCallerConverter() {}
  
  public String convert(ILoggingEvent le)
  {
    StackTraceElement[] cda = le.getCallerData();
    if ((cda != null) && (cda.length > 0)) {
      return cda[0].getFileName();
    }
    return "?";
  }
}
