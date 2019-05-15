package ch.qos.logback.core.spi;

import ch.qos.logback.core.filter.Filter;
import java.util.List;

public abstract interface FilterAttachable<E>
{
  public abstract void addFilter(Filter<E> paramFilter);
  
  public abstract void clearAllFilters();
  
  public abstract List<Filter<E>> getCopyOfAttachedFiltersList();
  
  public abstract FilterReply getFilterChainDecision(E paramE);
}
