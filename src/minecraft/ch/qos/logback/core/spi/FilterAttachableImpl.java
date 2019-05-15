package ch.qos.logback.core.spi;

import ch.qos.logback.core.filter.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;



















public final class FilterAttachableImpl<E>
  implements FilterAttachable<E>
{
  CopyOnWriteArrayList<Filter<E>> filterList = new CopyOnWriteArrayList();
  
  public FilterAttachableImpl() {}
  
  public void addFilter(Filter<E> newFilter)
  {
    filterList.add(newFilter);
  }
  


  public void clearAllFilters()
  {
    filterList.clear();
  }
  




  public FilterReply getFilterChainDecision(E event)
  {
    for (Filter<E> f : filterList) {
      FilterReply r = f.decide(event);
      if ((r == FilterReply.DENY) || (r == FilterReply.ACCEPT)) {
        return r;
      }
    }
    return FilterReply.NEUTRAL;
  }
  
  public List<Filter<E>> getCopyOfAttachedFiltersList() {
    return new ArrayList(filterList);
  }
}
