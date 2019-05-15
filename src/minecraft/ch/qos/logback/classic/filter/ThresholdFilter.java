package ch.qos.logback.classic.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

























public class ThresholdFilter
  extends Filter<ILoggingEvent>
{
  Level level;
  
  public ThresholdFilter() {}
  
  public FilterReply decide(ILoggingEvent event)
  {
    if (!isStarted()) {
      return FilterReply.NEUTRAL;
    }
    
    if (event.getLevel().isGreaterOrEqual(level)) {
      return FilterReply.NEUTRAL;
    }
    return FilterReply.DENY;
  }
  
  public void setLevel(String level)
  {
    this.level = Level.toLevel(level);
  }
  
  public void start() {
    if (level != null) {
      super.start();
    }
  }
}
