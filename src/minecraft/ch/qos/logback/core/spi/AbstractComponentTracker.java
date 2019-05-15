package ch.qos.logback.core.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;



















































public abstract class AbstractComponentTracker<C>
  implements ComponentTracker<C>
{
  private static final boolean ACCESS_ORDERED = true;
  public static final long LINGERING_TIMEOUT = 10000L;
  public static final long WAIT_BETWEEN_SUCCESSIVE_REMOVAL_ITERATIONS = 1000L;
  protected int maxComponents;
  protected long timeout;
  LinkedHashMap<String, Entry<C>> liveMap;
  LinkedHashMap<String, Entry<C>> lingerersMap;
  long lastCheck;
  private RemovalPredicator<C> byExcedent;
  private RemovalPredicator<C> byTimeout;
  private RemovalPredicator<C> byLingering;
  
  public int getComponentCount()
  {
    return liveMap.size() + lingerersMap.size();
  }
  





  private Entry<C> getFromEitherMap(String key)
  {
    Entry<C> entry = (Entry)liveMap.get(key);
    if (entry != null) {
      return entry;
    }
    return (Entry)lingerersMap.get(key);
  }
  









  public synchronized C find(String key)
  {
    Entry<C> entry = getFromEitherMap(key);
    if (entry == null) return null;
    return component;
  }
  








  public synchronized C getOrCreate(String key, long timestamp)
  {
    Entry<C> entry = getFromEitherMap(key);
    if (entry == null) {
      C c = buildComponent(key);
      entry = new Entry(key, c, timestamp);
      
      liveMap.put(key, entry);
    } else {
      entry.setTimestamp(timestamp);
    }
    return component;
  }
  




  public void endOfLife(String key)
  {
    Entry entry = (Entry)liveMap.remove(key);
    if (entry == null)
      return;
    lingerersMap.put(key, entry);
  }
  





  public synchronized void removeStaleComponents(long now)
  {
    if (isTooSoonForRemovalIteration(now)) return;
    removeExcedentComponents();
    removeStaleComponentsFromMainMap(now);
    removeStaleComponentsFromLingerersMap(now);
  }
  
  private void removeExcedentComponents() {
    genericStaleComponentRemover(liveMap, 0L, byExcedent);
  }
  
  private void removeStaleComponentsFromMainMap(long now) {
    genericStaleComponentRemover(liveMap, now, byTimeout);
  }
  
  private void removeStaleComponentsFromLingerersMap(long now) {
    genericStaleComponentRemover(lingerersMap, now, byLingering);
  }
  
  private void genericStaleComponentRemover(LinkedHashMap<String, Entry<C>> map, long now, RemovalPredicator<C> removalPredicator)
  {
    Iterator<Map.Entry<String, Entry<C>>> iter = map.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<String, Entry<C>> mapEntry = (Map.Entry)iter.next();
      Entry<C> entry = (Entry)mapEntry.getValue();
      if (!removalPredicator.isSlatedForRemoval(entry, now)) break;
      iter.remove();
      C c = component;
      processPriorToRemoval(c);
    }
  }
  
  public AbstractComponentTracker()
  {
    maxComponents = Integer.MAX_VALUE;
    timeout = 1800000L;
    

    liveMap = new LinkedHashMap(32, 0.75F, true);
    

    lingerersMap = new LinkedHashMap(16, 0.75F, true);
    lastCheck = 0L;
    






































































































































    byExcedent = new RemovalPredicator() {
      public boolean isSlatedForRemoval(AbstractComponentTracker.Entry<C> entry, long timestamp) {
        return liveMap.size() > maxComponents;
      }
      
    };
    byTimeout = new RemovalPredicator() {
      public boolean isSlatedForRemoval(AbstractComponentTracker.Entry<C> entry, long timestamp) {
        return AbstractComponentTracker.this.isEntryStale(entry, timestamp);
      }
    };
    byLingering = new RemovalPredicator()
    {
      public boolean isSlatedForRemoval(AbstractComponentTracker.Entry<C> entry, long timestamp) { return AbstractComponentTracker.this.isEntryDoneLingering(entry, timestamp); }
    };
  }
  
  private boolean isTooSoonForRemovalIteration(long now) {
    if (lastCheck + 1000L > now) {
      return true;
    }
    lastCheck = now;
    return false;
  }
  

  private boolean isEntryStale(Entry<C> entry, long now)
  {
    C c = component;
    if (isComponentStale(c)) {
      return true;
    }
    return timestamp + timeout < now;
  }
  
  private boolean isEntryDoneLingering(Entry entry, long now) {
    return timestamp + 10000L < now;
  }
  
  public Set<String> allKeys() {
    HashSet<String> allKeys = new HashSet(liveMap.keySet());
    allKeys.addAll(lingerersMap.keySet());
    return allKeys;
  }
  
  public Collection<C> allComponents() {
    List<C> allComponents = new ArrayList();
    for (Entry<C> e : liveMap.values())
      allComponents.add(component);
    for (Entry<C> e : lingerersMap.values()) {
      allComponents.add(component);
    }
    return allComponents;
  }
  
  public long getTimeout() {
    return timeout;
  }
  
  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }
  
  public int getMaxComponents() {
    return maxComponents;
  }
  

  public void setMaxComponents(int maxComponents) { this.maxComponents = maxComponents; }
  
  protected abstract void processPriorToRemoval(C paramC);
  
  protected abstract C buildComponent(String paramString);
  
  protected abstract boolean isComponentStale(C paramC);
  
  private static class Entry<C> {
    String key;
    C component;
    long timestamp;
    
    Entry(String k, C c, long timestamp) {
      key = k;
      component = c;
      this.timestamp = timestamp;
    }
    
    public void setTimestamp(long timestamp) {
      this.timestamp = timestamp;
    }
    
    public int hashCode()
    {
      return key.hashCode();
    }
    
    public boolean equals(Object obj)
    {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Entry other = (Entry)obj;
      if (key == null) {
        if (key != null)
          return false;
      } else if (!key.equals(key))
        return false;
      if (component == null) {
        if (component != null)
          return false;
      } else if (!component.equals(component))
        return false;
      return true;
    }
    
    public String toString()
    {
      return "(" + key + ", " + component + ")";
    }
  }
  
  private static abstract interface RemovalPredicator<C>
  {
    public abstract boolean isSlatedForRemoval(AbstractComponentTracker.Entry<C> paramEntry, long paramLong);
  }
}
