package ch.qos.logback.classic.turbo;

import ch.qos.logback.core.spi.FilterReply;

















public abstract class MatchingFilter
  extends TurboFilter
{
  public MatchingFilter() {}
  
  protected FilterReply onMatch = FilterReply.NEUTRAL;
  protected FilterReply onMismatch = FilterReply.NEUTRAL;
  
  public final void setOnMatch(String action) {
    if ("NEUTRAL".equals(action)) {
      onMatch = FilterReply.NEUTRAL;
    } else if ("ACCEPT".equals(action)) {
      onMatch = FilterReply.ACCEPT;
    } else if ("DENY".equals(action)) {
      onMatch = FilterReply.DENY;
    }
  }
  
  public final void setOnMismatch(String action) {
    if ("NEUTRAL".equals(action)) {
      onMismatch = FilterReply.NEUTRAL;
    } else if ("ACCEPT".equals(action)) {
      onMismatch = FilterReply.ACCEPT;
    } else if ("DENY".equals(action)) {
      onMismatch = FilterReply.DENY;
    }
  }
}
