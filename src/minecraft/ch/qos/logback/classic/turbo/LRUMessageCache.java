package ch.qos.logback.classic.turbo;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
















class LRUMessageCache
  extends LinkedHashMap<String, Integer>
{
  private static final long serialVersionUID = 1L;
  final int cacheSize;
  
  LRUMessageCache(int cacheSize)
  {
    super((int)(cacheSize * 1.3333334F), 0.75F, true);
    if (cacheSize < 1) {
      throw new IllegalArgumentException("Cache size cannot be smaller than 1");
    }
    this.cacheSize = cacheSize;
  }
  
  int getMessageCountAndThenIncrement(String msg)
  {
    if (msg == null) {
      return 0;
    }
    
    Integer i;
    
    synchronized (this) {
      i = (Integer)super.get(msg);
      if (i == null) {
        i = Integer.valueOf(0);
      } else {
        i = Integer.valueOf(i.intValue() + 1);
      }
      super.put(msg, i);
    }
    return i.intValue();
  }
  

  protected boolean removeEldestEntry(Map.Entry eldest)
  {
    return size() > cacheSize;
  }
  
  public synchronized void clear()
  {
    super.clear();
  }
}
