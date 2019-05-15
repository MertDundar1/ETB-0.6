package ch.qos.logback.classic.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;





















public class LevelFilter
  extends AbstractMatcherFilter<ILoggingEvent>
{
  Level level;
  
  public LevelFilter() {}
  
  public FilterReply decide(ILoggingEvent event)
  {
    if (!isStarted()) {
      return FilterReply.NEUTRAL;
    }
    
    if (event.getLevel().equals(level)) {
      return onMatch;
    }
    return onMismatch;
  }
  
  public void setLevel(Level level)
  {
    this.level = level;
  }
  
  public void start() {
    if (level != null) {
      super.start();
    }
  }
}
