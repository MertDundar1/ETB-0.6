package ch.qos.logback.classic.turbo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.MDC;
import org.slf4j.Marker;



































public class MDCFilter
  extends MatchingFilter
{
  String MDCKey;
  String value;
  
  public MDCFilter() {}
  
  public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t)
  {
    if (MDCKey == null) {
      return FilterReply.NEUTRAL;
    }
    
    String value = MDC.get(MDCKey);
    if (this.value.equals(value)) {
      return onMatch;
    }
    return onMismatch;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
  
  public void setMDCKey(String MDCKey) {
    this.MDCKey = MDCKey;
  }
}
