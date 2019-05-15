package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;























public class DefaultDnsCache
  implements DnsCache
{
  private final ConcurrentMap<String, List<DnsCacheEntry>> resolveCache = PlatformDependent.newConcurrentHashMap();
  
  private final int minTtl;
  
  private final int maxTtl;
  
  private final int negativeTtl;
  
  public DefaultDnsCache()
  {
    this(0, Integer.MAX_VALUE, 0);
  }
  





  public DefaultDnsCache(int minTtl, int maxTtl, int negativeTtl)
  {
    this.minTtl = ObjectUtil.checkPositiveOrZero(minTtl, "minTtl");
    this.maxTtl = ObjectUtil.checkPositiveOrZero(maxTtl, "maxTtl");
    if (minTtl > maxTtl) {
      throw new IllegalArgumentException("minTtl: " + minTtl + ", maxTtl: " + maxTtl + " (expected: 0 <= minTtl <= maxTtl)");
    }
    
    this.negativeTtl = ObjectUtil.checkPositiveOrZero(negativeTtl, "negativeTtl");
  }
  




  public int minTtl()
  {
    return minTtl;
  }
  




  public int maxTtl()
  {
    return maxTtl;
  }
  



  public int negativeTtl()
  {
    return negativeTtl;
  }
  
  public void clear()
  {
    for (Iterator<Map.Entry<String, List<DnsCacheEntry>>> i = resolveCache.entrySet().iterator(); i.hasNext();) {
      Map.Entry<String, List<DnsCacheEntry>> e = (Map.Entry)i.next();
      i.remove();
      cancelExpiration((List)e.getValue());
    }
  }
  
  public boolean clear(String hostname)
  {
    ObjectUtil.checkNotNull(hostname, "hostname");
    boolean removed = false;
    for (Iterator<Map.Entry<String, List<DnsCacheEntry>>> i = resolveCache.entrySet().iterator(); i.hasNext();) {
      Map.Entry<String, List<DnsCacheEntry>> e = (Map.Entry)i.next();
      if (((String)e.getKey()).equals(hostname)) {
        i.remove();
        cancelExpiration((List)e.getValue());
        removed = true;
      }
    }
    return removed;
  }
  
  private static boolean emptyAdditionals(DnsRecord[] additionals) {
    return (additionals == null) || (additionals.length == 0);
  }
  
  public List<DnsCacheEntry> get(String hostname, DnsRecord[] additionals)
  {
    ObjectUtil.checkNotNull(hostname, "hostname");
    if (!emptyAdditionals(additionals)) {
      return null;
    }
    return (List)resolveCache.get(hostname);
  }
  
  private List<DnsCacheEntry> cachedEntries(String hostname) {
    List<DnsCacheEntry> oldEntries = (List)resolveCache.get(hostname);
    List<DnsCacheEntry> entries;
    List<DnsCacheEntry> entries; if (oldEntries == null) {
      List<DnsCacheEntry> newEntries = new ArrayList(8);
      oldEntries = (List)resolveCache.putIfAbsent(hostname, newEntries);
      entries = oldEntries != null ? oldEntries : newEntries;
    } else {
      entries = oldEntries;
    }
    return entries;
  }
  

  public void cache(String hostname, DnsRecord[] additionals, InetAddress address, long originalTtl, EventLoop loop)
  {
    ObjectUtil.checkNotNull(hostname, "hostname");
    ObjectUtil.checkNotNull(address, "address");
    ObjectUtil.checkNotNull(loop, "loop");
    if ((maxTtl == 0) || (!emptyAdditionals(additionals))) {
      return;
    }
    int ttl = Math.max(minTtl, (int)Math.min(maxTtl, originalTtl));
    List<DnsCacheEntry> entries = cachedEntries(hostname);
    DnsCacheEntry e = new DnsCacheEntry(hostname, address);
    
    synchronized (entries) {
      if (!entries.isEmpty()) {
        DnsCacheEntry firstEntry = (DnsCacheEntry)entries.get(0);
        if (firstEntry.cause() != null) {
          assert (entries.size() == 1);
          firstEntry.cancelExpiration();
          entries.clear();
        }
      }
      entries.add(e);
    }
    
    scheduleCacheExpiration(entries, e, ttl, loop);
  }
  
  public void cache(String hostname, DnsRecord[] additionals, Throwable cause, EventLoop loop)
  {
    ObjectUtil.checkNotNull(hostname, "hostname");
    ObjectUtil.checkNotNull(cause, "cause");
    ObjectUtil.checkNotNull(loop, "loop");
    
    if ((negativeTtl == 0) || (!emptyAdditionals(additionals))) {
      return;
    }
    List<DnsCacheEntry> entries = cachedEntries(hostname);
    DnsCacheEntry e = new DnsCacheEntry(hostname, cause);
    
    synchronized (entries) {
      int numEntries = entries.size();
      for (int i = 0; i < numEntries; i++) {
        ((DnsCacheEntry)entries.get(i)).cancelExpiration();
      }
      entries.clear();
      entries.add(e);
    }
    
    scheduleCacheExpiration(entries, e, negativeTtl, loop);
  }
  
  private static void cancelExpiration(List<DnsCacheEntry> entries) {
    int numEntries = entries.size();
    for (int i = 0; i < numEntries; i++) {
      ((DnsCacheEntry)entries.get(i)).cancelExpiration();
    }
  }
  


  private void scheduleCacheExpiration(final List<DnsCacheEntry> entries, final DnsCacheEntry e, int ttl, EventLoop loop)
  {
    e.scheduleExpiration(loop, new Runnable()
    {
      public void run() {
        synchronized (entries) {
          entries.remove(e);
          if (entries.isEmpty())
            resolveCache.remove(e.hostname()); } } }, ttl, TimeUnit.SECONDS);
  }
  




  public String toString()
  {
    return "DefaultDnsCache(minTtl=" + minTtl + ", maxTtl=" + maxTtl + ", negativeTtl=" + negativeTtl + ", cached resolved hostname=" + resolveCache.size() + ")";
  }
}
