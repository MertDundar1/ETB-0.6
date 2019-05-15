package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;














public class LevelConverter
  extends ClassicConverter
{
  public LevelConverter() {}
  
  public String convert(ILoggingEvent le)
  {
    return le.getLevel().toString();
  }
}
