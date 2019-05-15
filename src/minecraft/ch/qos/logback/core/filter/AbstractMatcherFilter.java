package ch.qos.logback.core.filter;

import ch.qos.logback.core.spi.FilterReply;











public abstract class AbstractMatcherFilter<E>
  extends Filter<E>
{
  public AbstractMatcherFilter() {}
  
  protected FilterReply onMatch = FilterReply.NEUTRAL;
  protected FilterReply onMismatch = FilterReply.NEUTRAL;
  
  public final void setOnMatch(FilterReply reply) {
    onMatch = reply;
  }
  
  public final void setOnMismatch(FilterReply reply) {
    onMismatch = reply;
  }
  
  public final FilterReply getOnMatch() {
    return onMatch;
  }
  
  public final FilterReply getOnMismatch() {
    return onMismatch;
  }
}
