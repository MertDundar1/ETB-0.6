package io.netty.channel.pool;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReadOnlyIterator;
import java.io.Closeable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;




















public abstract class AbstractChannelPoolMap<K, P extends ChannelPool>
  implements ChannelPoolMap<K, P>, Iterable<Map.Entry<K, P>>, Closeable
{
  private final ConcurrentMap<K, P> map = PlatformDependent.newConcurrentHashMap();
  
  public AbstractChannelPoolMap() {}
  
  public final P get(K key) { P pool = (ChannelPool)map.get(ObjectUtil.checkNotNull(key, "key"));
    if (pool == null) {
      pool = newPool(key);
      P old = (ChannelPool)map.putIfAbsent(key, pool);
      if (old != null)
      {
        pool.close();
        pool = old;
      }
    }
    return pool;
  }
  




  public final boolean remove(K key)
  {
    P pool = (ChannelPool)map.remove(ObjectUtil.checkNotNull(key, "key"));
    if (pool != null) {
      pool.close();
      return true;
    }
    return false;
  }
  
  public final Iterator<Map.Entry<K, P>> iterator()
  {
    return new ReadOnlyIterator(map.entrySet().iterator());
  }
  


  public final int size()
  {
    return map.size();
  }
  


  public final boolean isEmpty()
  {
    return map.isEmpty();
  }
  
  public final boolean contains(K key)
  {
    return map.containsKey(ObjectUtil.checkNotNull(key, "key"));
  }
  


  protected abstract P newPool(K paramK);
  

  public final void close()
  {
    for (K key : map.keySet()) {
      remove(key);
    }
  }
}
