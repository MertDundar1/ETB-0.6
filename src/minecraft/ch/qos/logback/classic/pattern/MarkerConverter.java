package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.Marker;

















public class MarkerConverter
  extends ClassicConverter
{
  public MarkerConverter() {}
  
  private static String EMPTY = "";
  
  public String convert(ILoggingEvent le) {
    Marker marker = le.getMarker();
    if (marker == null) {
      return EMPTY;
    }
    return marker.toString();
  }
}
