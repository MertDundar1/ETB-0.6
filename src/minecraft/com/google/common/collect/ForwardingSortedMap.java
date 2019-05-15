package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import javax.annotation.Nullable;














































@GwtCompatible
public abstract class ForwardingSortedMap<K, V>
  extends ForwardingMap<K, V>
  implements SortedMap<K, V>
{
  protected ForwardingSortedMap() {}
  
  protected abstract SortedMap<K, V> delegate();
  
  public Comparator<? super K> comparator()
  {
    return delegate().comparator();
  }
  
  public K firstKey()
  {
    return delegate().firstKey();
  }
  
  public SortedMap<K, V> headMap(K toKey)
  {
    return delegate().headMap(toKey);
  }
  
  public K lastKey()
  {
    return delegate().lastKey();
  }
  
  public SortedMap<K, V> subMap(K fromKey, K toKey)
  {
    return delegate().subMap(fromKey, toKey);
  }
  
  public SortedMap<K, V> tailMap(K fromKey)
  {
    return delegate().tailMap(fromKey);
  }
  





  @Beta
  protected class StandardKeySet
    extends Maps.SortedKeySet<K, V>
  {
    public StandardKeySet()
    {
      super();
    }
  }
  

  private int unsafeCompare(Object k1, Object k2)
  {
    Comparator<? super K> comparator = comparator();
    if (comparator == null) {
      return ((Comparable)k1).compareTo(k2);
    }
    return comparator.compare(k1, k2);
  }
  








  @Beta
  protected boolean standardContainsKey(@Nullable Object key)
  {
    try
    {
      SortedMap<Object, V> self = this;
      Object ceilingKey = self.tailMap(key).firstKey();
      return unsafeCompare(ceilingKey, key) == 0;
    } catch (ClassCastException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    } catch (NullPointerException e) {}
    return false;
  }
  







  @Beta
  protected SortedMap<K, V> standardSubMap(K fromKey, K toKey)
  {
    Preconditions.checkArgument(unsafeCompare(fromKey, toKey) <= 0, "fromKey must be <= toKey");
    return tailMap(fromKey).headMap(toKey);
  }
}
